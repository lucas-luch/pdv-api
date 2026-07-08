---
name: pdv-api-conventions
description: Convenções de arquitetura, regras de negócio e padrões do projeto PDV API. Consultar ao criar/alterar endpoints, services, repositories ou regras de transição de estado.
---

# PDV API — Convenções e Regras do Projeto

> Baseado em: Clean Code (Robert C. Martin), REST API Design Rulebook (Mark Massé),
> Effective Java (Joshua Bloch), Domain-Driven Design (Eric Evans).

---

## 1. Arquitetura em Camadas

```
Controller → Service → Repository → Database
     ↓           ↓          ↓
   DTOs     Models/Enums   SQL/JDBC
```

### Responsabilidades

| Camada | Faz | NÃO faz |
|--------|-----|---------|
| Controller | Recebe HTTP, delega, retorna ResponseEntity | Regra de negócio, SQL, validação complexa |
| Service | Regras de negócio, validação de estado, orquestração | SQL direto, montar ResponseEntity |
| Mapper | Traduz DTO ↔ Entity | Regra de negócio, acesso a banco |
| Repository | JDBC, SQL, mapRow | Decisão de negócio, lançar exceções de domínio |

### Princípios (Clean Code / DDD)

- **Single Responsibility**: cada classe tem um motivo para mudar
- **Tell, Don't Ask**: service decide e ordena, não fica perguntando estado
- **Fail Fast**: validar no início do método, antes de qualquer efeito colateral
- **Guard Clauses**: if inválido → throw (evitar if/else profundo)

---

## 2. Entidades e Estado

### Produto

| Campo | Tipo | Regra |
|-------|------|-------|
| id | Long | Gerado pelo banco (serial) |
| nome | String | Obrigatório, não vazio |
| preco | double | Obrigatório, > 0 |
| ativo | boolean | Controla disponibilidade (soft delete) |

Transições:
- `ativo=true` → pode ser adicionado a pedidos
- `ativo=false` → bloqueado para novos itens, mas registros históricos permanecem

### Mesa

| Campo | Tipo | Regra |
|-------|------|-------|
| id | Long | Gerado pelo banco |
| numero | String | Obrigatório, único, trimmed |
| status | StatusMesa | LIVRE → OCUPADA → FECHADA → LIVRE |
| observacao | String | Opcional, atualizada nas transições |

Transições de estado:
```
LIVRE ──[abrir]──→ OCUPADA ──[fechar]──→ FECHADA ──[liberar]──→ LIVRE
                                                         ↑
                      OCUPADA ──────────[liberar]─────────┘
```

Regras:
- Abrir: **somente** se LIVRE
- Fechar: **somente** se OCUPADA
- Liberar: qualquer status **exceto** LIVRE (já está livre)
- Número duplicado é bloqueado na criação

### Pedido

| Campo | Tipo | Regra |
|-------|------|-------|
| id | Long | Gerado pelo banco |
| mesa | Mesa | FK obrigatória, mesa deve estar OCUPADA |
| status | StatusPedido | ABERTO → FECHADO |
| dataCriacao | LocalDate | Definida pelo sistema |
| horaCriacao | LocalTime | Definida pelo sistema |

Regras:
- Criar: mesa **MUST** estar OCUPADA
- Fechar: pedido **MUST** estar ABERTO
- Data e hora são definidas pelo servidor, nunca pelo cliente

### ItemPedido

| Campo | Tipo | Regra |
|-------|------|-------|
| id | Long | Gerado pelo banco |
| pedido | Pedido | FK, pedido deve estar ABERTO |
| produto | Produto | FK, produto deve existir e estar ATIVO |
| quantidade | int | > 0 |
| precoUnitario | double | Capturado do produto no momento da adição |
| subtotal | double | precoUnitario × quantidade (calculado pelo service) |

Regras:
- Pedido **MUST** estar ABERTO para aceitar itens
- Produto **MUST** existir e estar ativo
- precoUnitario é snapshot: se o produto mudar de preço depois, o item não muda
- subtotal é derivado, nunca informado pelo cliente

---

## 3. Padrões de Código

### Naming

| Elemento | Padrão | Exemplo |
|----------|--------|---------|
| Endpoint de coleção | Plural, minúsculo, hífen | `/produtos`, `/item-pedidos` |
| Endpoint de ação | Verbo no infinitivo | `/mesas/{id}/abrir`, `/pedidos/{id}/fechar` |
| Service method | Verbo em português | `criar`, `buscarPorId`, `fechar`, `ativar` |
| Exception | Substantivo + causa | `ProdutoNaoEncontradoException` |
| DTO request | `Criar*Request`, `Atualizar*Request` | `CriarProdutoRequest` |
| DTO response | `*Response` | `ProdutoResponse`, `MesaTotalResponse` |

### Service — padrão de método

```java
public Response metodo(Request request) {
    // 1. Buscar entidades necessárias (fail fast)
    Entidade e = buscarOuFalhar(id);

    // 2. Validar estado/regras de negócio
    validarCondicao(e);

    // 3. Montar/alterar entidade
    e.setCampo(valor);

    // 4. Persistir
    repository.salvar(e);

    // 5. Retornar response
    return mapper.toResponse(e);
}
```

### Repository — padrão JDBC

```java
public void salvar(Entidade e) {
    String sql = "INSERT INTO tabela (...) VALUES (?, ?)";
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        stmt.setString(1, e.getCampo());
        stmt.executeUpdate();
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) e.setId(rs.getLong(1));
        }
    } catch (SQLException ex) {
        throw new BancoDeDadosException("Erro ao salvar entidade", ex);
    }
}
```

Regras de repository:
- Retornar `null` quando não encontra (service interpreta como erro)
- Encapsular `SQLException` em `BancoDeDadosException`
- Enum → String no save (`status.name()`), String → Enum no read (`valueOf`)
- `try-with-resources` obrigatório para Connection/Statement/ResultSet

### Mapper — regras

- Mapper traduz, nunca decide regra de negócio
- Pode definir valor padrão simples (ex: status LIVRE)
- Referências parciais (só id) são válidas — service completa depois
- Nunca acessa repository ou faz I/O

---

## 4. Tratamento de Erros

### Exceções do projeto

| Exception | HTTP Status | Quando usar |
|-----------|-------------|-------------|
| `*NaoEncontrado/a` | 404 | Busca por id retorna null |
| `*StatusInvalido` | 400 (considerar 409) | Transição de estado inválida |
| `*NumeroDuplicado` | 400 (considerar 409) | Violação de unicidade |
| `*NumeroObrigatorio` | 400 | Campo obrigatório ausente |
| `ProdutoInativo` | 400 | Tentativa de usar produto inativo |
| `BancoDeDados` | 500 | Falha técnica no JDBC |
| `MethodArgumentNotValid` | 400 | Jakarta Validation falhou |

### Formato de resposta de erro (padronizado)

```json
{
  "timestamp": "2026-04-01T20:12:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Mesa precisa estar livre para ser ocupada",
  "path": "/mesas/1/abrir",
  "details": []
}
```

### Regras

- Toda exception de negócio **MUST** ter handler no `ApiExceptionHandler`
- Nunca retornar stacktrace em produção
- `details[]` usado para múltiplos erros de validação
- Mensagens em português, claras para o consumidor da API

---

## 5. Validação

### Duas camadas

1. **Controller**: `@Valid @RequestBody` — validação de formato/obrigatoriedade
2. **Service**: `@Validated` + `@Valid` — validação de regra de negócio e estado

### Anotações nos DTOs

- `@NotBlank` para strings obrigatórias
- `@NotNull` para referências obrigatórias
- `@Positive` para valores numéricos > 0
- `@Schema` para documentação OpenAPI

---

## 6. Testes

### Estrutura

- Unitários em `src/test/java` (services com mocks)
- Integração com `@SpringBootTest` (fluxos completos)
- Nomenclatura: `deve_resultado_quandoCondicao`

### Prioridade de cobertura

1. Services (regras de negócio) — **alta**
2. Controllers (status codes, validação) — média
3. Repositories (mapRow, SQL) — caso a caso

---

## 7. Decisões conscientes (tradeoffs)

| Decisão | Motivo |
|---------|--------|
| `double` em vez de `BigDecimal` | Simplicidade didática (evoluir no futuro) |
| JDBC manual em vez de JPA | Controle total sobre SQL, aprendizado |
| Sem HATEOAS | Custo alto, baixo benefício para o escopo |
| Sem versionamento de API | Projeto single-version por enquanto |
| Sem paginação | Dados pequenos, implementar quando necessário |
| POST retorna 200 (corrigir para 201) | Ponto de melhoria identificado |
