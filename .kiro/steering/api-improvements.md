# Steering — Correções REST API

> Estado atual do projeto e plano de correção das inconsistências REST identificadas.
> Atualizado em: 2026-07-08

---

## Estado Atual dos Endpoints

### ProdutoController (`/produtos`)

| Método | Endpoint | Status Code Atual | Observação |
|--------|----------|-------------------|------------|
| POST | `/produtos` | 201 + Location | ✅ Corrigido |
| GET | `/produtos` | 200 | ✅ Correto |
| GET | `/produtos/{id}` | 200 / 404 | ✅ Correto |
| PUT | `/produtos/{id}` | 200 | ✅ Correto |
| PATCH | `/produtos/{id}/ativar` | 200 | ⚠️ Redundante com PUT |
| PATCH | `/produtos/{id}/inativar` | 200 | ⚠️ Redundante com PUT |

### MesaController (`/mesas`)

| Método | Endpoint | Status Code Atual | Observação |
|--------|----------|-------------------|------------|
| POST | `/mesas` | 201 + Location | ✅ Corrigido |
| GET | `/mesas` | 200 | ✅ Correto |
| GET | `/mesas/{id}` | 200 / 404 | ✅ Correto |
| GET | `/mesas/{id}/total` | 200 | ✅ Correto |
| GET | `/mesas/{id}/pedidos` | 200 | ✅ Correto |
| PATCH | `/mesas/{id}/abrir` | 200 / 400 | ⚠️ 400 deveria ser 409 |
| PATCH | `/mesas/{id}/fechar` | 200 / 400 | ⚠️ 400 deveria ser 409 |
| PATCH | `/mesas/{id}/liberar` | 200 / 400 | ⚠️ 400 deveria ser 409 |

### PedidoController (`/pedidos`)

| Método | Endpoint | Status Code Atual | Observação |
|--------|----------|-------------------|------------|
| POST | `/pedidos` | 201 + Location | ✅ Corrigido |
| GET | `/pedidos/{id}` | 200 / 404 | ✅ Correto |
| GET | `/pedidos/mesa/{mesaId}` | ~~200~~ | ✅ Removido (duplicava `/mesas/{id}/pedidos`) |
| PATCH | `/pedidos/{id}/fechar` | 200 / 400 | ⚠️ 400 deveria ser 409 |
| GET | `/pedidos/{id}/itens` | 200 | ✅ Correto |

### ItemPedidoController (`/item-pedidos`)

| Método | Endpoint | Status Code Atual | Observação |
|--------|----------|-------------------|------------|
| POST | `/item-pedidos` | ~~201~~ | ✅ Movido para `POST /pedidos/{id}/itens` |
| GET | `/item-pedidos/pedido/{pedidoId}` | ~~200~~ | ✅ Removido (duplicava `/pedidos/{id}/itens`) |

---

## Problemas Identificados

### 🔴 P1 — POST retorna 200 em vez de 201 Created + Location

**Onde:** todos os POST (produtos, mesas, pedidos, item-pedidos)

**Problema:** POST de criação MUST retornar 201 com header Location apontando para o recurso criado. Atualmente os controllers retornam o DTO diretamente sem ResponseEntity.

**Correção:** Alterar retorno dos controllers para `ResponseEntity.created(location).body(response)`.

**Status:** [x] Concluído

---

### 🔴 P2 — Rotas duplicadas com hierarquia invertida

**Onde:**
- `GET /pedidos/mesa/{mesaId}` duplica `GET /mesas/{id}/pedidos`
- `GET /item-pedidos/pedido/{pedidoId}` duplica `GET /pedidos/{id}/itens`

**Problema:** A mesma informação é acessível por duas URIs diferentes, com uma delas usando hierarquia invertida (sub-recurso no path do recurso filho). Isso confunde o consumidor e viola o princípio de uma URI canônica por recurso.

**Correção:** Remover `GET /pedidos/mesa/{mesaId}` e `GET /item-pedidos/pedido/{pedidoId}`. Manter apenas as rotas hierárquicas corretas.

**Status:** [x] Concluído

---

### 🔴 P3 — Item de pedido criado fora da hierarquia

**Onde:** `POST /item-pedidos`

**Problema:** Item de pedido é um sub-recurso de pedido. A URI deveria refletir essa relação: `POST /pedidos/{pedidoId}/itens`. O pedidoId está no body quando deveria estar no path.

**Correção:** Mover a criação de item para `POST /pedidos/{pedidoId}/itens` dentro do PedidoController (ou um controller dedicado nesse path). Remover `pedidoId` do body do request. Avaliar se o `ItemPedidoController` top-level ainda precisa existir.

**Status:** [x] Concluído

---

### 🟡 P4 — `/ativar` e `/inativar` redundantes com PUT

**Onde:** `PATCH /produtos/{id}/ativar` e `PATCH /produtos/{id}/inativar`

**Problema:** O `PUT /produtos/{id}` já recebe o campo `ativo` no `AtualizarProdutoRequest`, tornando os dois endpoints de toggle redundantes. Dois endpoints separados para um booleano simples não se justificam sem regras de negócio distintas.

**Correção possível:**
- Opção A: Remover ambos e usar apenas PUT para alterar o campo `ativo`.
- Opção B: Manter se houver intenção de adicionar regras diferentes (ex: inativar bloqueia pedidos em aberto). Nesse caso, documentar a justificativa.

**Decisão:** A definir — avaliar se existem side-effects planejados.

**Status:** [ ] Pendente

---

### 🟡 P5 — Status code 400 para conflitos de estado (deveria ser 409)

**Onde:**
- `PATCH /mesas/{id}/abrir` (mesa não está livre)
- `PATCH /mesas/{id}/fechar` (mesa não está ocupada)
- `PATCH /mesas/{id}/liberar` (mesa já está livre)
- `PATCH /pedidos/{id}/fechar` (pedido já fechado)
- `PATCH /produtos/{id}/ativar` (produto já ativo)
- `PATCH /produtos/{id}/inativar` (produto já inativo)

**Problema:** O request é bem-formado e válido sintaticamente. O que impede a operação é o **estado atual do recurso**, não um erro do cliente. O status semântico correto é `409 Conflict`.

**Correção:** Alterar as exceptions de status inválido para retornar 409 no `ApiExceptionHandler`.

**Status:** [ ] Pendente

---

### 🟢 P6 — Controllers sem ResponseEntity

**Onde:** todos os controllers

**Problema:** Retornar o DTO diretamente funciona para 200, mas impede controlar status code e headers. É pré-requisito para corrigir P1.

**Correção:** Será resolvido junto com P1 (alterar retorno para ResponseEntity nos métodos de criação). Métodos GET que retornam 200 podem permanecer retornando DTO diretamente — é questão de estilo.

**Status:** [x] Resolvido com P1

---

## Ordem de Execução Sugerida

1. **P1** — POST → 201 + Location (pré-requisito: P6 parcial)
2. **P5** — 400 → 409 para conflitos de estado
3. **P2** — Remover rotas duplicadas
4. **P3** — Mover criação de item para `/pedidos/{id}/itens`
5. **P4** — Decidir sobre ativar/inativar

> A ordem prioriza mudanças que não quebram o contrato da API (1, 5) antes das que alteram URIs (2, 3, 4).

---

## Referências

- [rest-api-rules](./../skills/rest-api-rules.md) — Regras REST do projeto
- [pdv-api-conventions](./../skills/pdv-api-conventions.md) — Convenções de arquitetura
