---
name: rest-api-rules
description: Regras de design REST API baseadas no livro REST API Design Rulebook. Consultar ao criar/revisar endpoints, status codes, URIs e formatos de resposta.
---

# REST API Design Rules — Referência para o Kiro

> Regras extraídas do *REST API Design Rulebook* (Mark Massé), filtradas para o stack atual:
> Java 21, Spring Boot 4, JDBC manual, PostgreSQL, OpenAPI/Swagger.
>
> **MUST** = obrigatório | **SHOULD** = fortemente recomendado | **MAY** = opcional

---

## 1. Métodos HTTP

| Operação | Método | Quando usar |
|----------|--------|-------------|
| Criar recurso em coleção | POST | Servidor gera o ID |
| Recuperar recurso | GET | Sem efeitos colaterais |
| Substituir recurso completo | PUT | Atualização total |
| Atualizar parcialmente | PATCH | Mudança de campo/estado |
| Remover recurso | DELETE | Exclusão efetiva |
| Executar ação de negócio | POST | Ações que não são CRUD (ex: `/resend`) |

Regras:
- GET e POST **MUST NOT** tunelar outros métodos (`?_method=DELETE` é proibido)
- GET **MUST** ser seguro e idempotente (sem efeitos colaterais)
- POST para criação **MUST** retornar `201 Created` + header `Location`
- DELETE **SHOULD** retornar `204 No Content`
- PUT envia representação completa; PATCH envia mudança parcial

---

## 2. Status Codes

### Sucesso

| Código | Uso |
|--------|-----|
| 200 OK | Sucesso genérico (GET, PUT, PATCH com corpo) |
| 201 Created | Recurso criado (POST). Incluir header `Location` |
| 204 No Content | Sucesso sem corpo (DELETE, PATCH sem retorno) |

### Erro do cliente

| Código | Uso |
|--------|-----|
| 400 Bad Request | Validação falhou, dados malformados |
| 404 Not Found | Recurso não existe |
| 405 Method Not Allowed | Método HTTP não suportado no recurso |
| 409 Conflict | Violação de estado (duplicidade, transição inválida) |
| 415 Unsupported Media Type | Content-Type não aceito |
| 422 Unprocessable Entity | Regra de negócio violada (alternativa ao 400 para regras semânticas) |

### Erro do servidor

| Código | Uso |
|--------|-----|
| 500 Internal Server Error | Falha não tratada no backend |

Regras críticas:
- **MUST NOT** retornar 200 com erro no corpo da resposta
- **MUST NOT** retornar 200 para criação de recurso (usar 201)
- **MUST** retornar 404 para recurso inexistente (nunca 200 com corpo vazio)
- 409 **SHOULD** ser usado para conflito de estado (ex: mesa já ocupada, número duplicado)

---

## 3. Design de URIs

### Convenções de nomenclatura

- Barra (`/`) indica hierarquia: `/mesas/{id}/pedidos`
- **SHOULD NOT** incluir barra final: `/produtos` (não `/produtos/`)
- Usar hífens para legibilidade: `/item-pedidos` (não `/item_pedidos` nem `/itempedidos`)
- **SHOULD NOT** usar underscores nas URIs
- Letras minúsculas sempre
- **SHOULD NOT** incluir extensão de arquivo (`.json`)

### Nomenclatura de recursos

- Coleções usam **plural**: `/produtos`, `/mesas`, `/pedidos`
- Ações de negócio usam **verbo**: `/mesas/{id}/abrir`, `/pedidos/{id}/fechar`
- **MUST NOT** usar nomes CRUD na URI: nunca `/deleteUser/1` ou `/criarProduto`

### Query parameters

- Filtros vão na query string: `GET /pedidos?mesaId=3`
- Paginação via query: `?page=0&size=25`

---

## 4. Representação (JSON)

### Formato

- JSON **MUST** ser o formato padrão
- **MUST NOT** criar envelopes de resposta (o HTTP já é o envelope)
  - Correto: `{"id": 1, "nome": "Café"}` direto no corpo
  - Incorreto: `{"status": "success", "data": {"id": 1, "nome": "Café"}}`

### Resposta de erro — formato consistente

Usar estrutura padronizada em toda a API:

```json
{
  "timestamp": "2026-04-01T20:12:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Numero da mesa ja cadastrado",
  "path": "/mesas",
  "details": ["numero: nao deve estar em branco"]
}
```

Regras:
- **SHOULD** retornar múltiplos erros de validação de uma vez (campo `details`)
- Manter a mesma estrutura de erro em TODOS os endpoints
- Considerar migração para RFC 7807 (`ProblemDetail` nativo do Spring 6+) no futuro

---

## 5. Headers HTTP

| Header | Obrigatoriedade | Uso |
|--------|-----------------|-----|
| Content-Type | **MUST** | Sempre presente em respostas com corpo |
| Location | **MUST** (em 201) | URI do recurso recém-criado |
| Content-Length | **SHOULD** | Validação de integridade |

---

## 6. Regras aplicadas ao projeto PDV API

### Endpoints de criação

POST para criação **MUST** retornar `201 Created` + `Location`:
```java
@PostMapping
public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody CriarProdutoRequest request) {
    ProdutoResponse response = service.criar(request);
    URI location = URI.create("/produtos/" + response.getId());
    return ResponseEntity.created(location).body(response);
}
```

### Endpoints de ação/transição de estado

PATCH para mudança de estado é correto (já usado):
- `PATCH /mesas/{id}/abrir`
- `PATCH /mesas/{id}/fechar`
- `PATCH /pedidos/{id}/fechar`

Status retornado **SHOULD** ser `200 OK` com corpo atualizado (já está correto).

Para conflitos de estado (mesa não pode abrir porque já está ocupada):
- Considerar `409 Conflict` em vez de `400 Bad Request`

### Endpoints de busca

- GET retornando recurso: `200 OK` ✓
- GET recurso inexistente: `404 Not Found` ✓
- GET lista vazia: `200 OK` com `[]` (nunca 404 para lista vazia)

### DELETE (futuro)

Quando implementar exclusão:
- `DELETE /produtos/{id}` → `204 No Content`
- Soft delete via campo `ativo` usa PATCH (já modelado como `/inativar`)

### URI naming — pontos de atenção

- `/item-pedidos` ✓ (hífen, plural)
- `/pedidos/mesa/{mesaId}` — alternativa mais RESTful seria `/mesas/{id}/pedidos` (já existe)
- Manter consistência: se tem `/mesas/{id}/pedidos`, não precisa duplicar em `/pedidos/mesa/{mesaId}`

---

## 7. Checklist para novos endpoints

Ao criar ou revisar um endpoint, verificar:

- [ ] Método HTTP correto para a semântica da operação
- [ ] Status code correto (201 para criação, 204 para delete, 409 para conflito)
- [ ] URI em plural, minúscula, com hífen (nunca underscore ou camelCase)
- [ ] Ações de negócio como verbo na URI (`/fechar`, `/ativar`)
- [ ] Não duplicar nomes CRUD na URI (verbo HTTP já expressa a operação)
- [ ] Header `Location` presente em respostas 201
- [ ] Corpo de erro segue o formato padrão do `ErroResponse`
- [ ] Validação com `@Valid` no controller E no service
- [ ] `@PathVariable` para identificadores, `@RequestBody` para payload
- [ ] Documentação Swagger com `@Operation`, `@ApiResponses`

---

## 8. O que NÃO aplicar neste projeto (por ora)

Regras do livro descartadas por não se encaixarem no escopo/recursos atuais:

- **HATEOAS / links hypermedia** — custo alto, baixo retorno para API interna
- **Media types vendor-specific** (`application/vnd.*+json`) — desnecessário sem versionamento
- **ETag / cache condicional** — sem necessidade atual de concorrência otimista
- **Subdomínios para API** (`api.`) — projeto local/Docker
- **HEAD / OPTIONS explícitos** — Spring já trata automaticamente
- **Negociação de conteúdo multi-formato** — só JSON é necessário
