# PDV API

![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Backend-green)
![API REST](https://img.shields.io/badge/API-REST-blue)
![Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)

## 🚀 Sobre o Projeto

API REST de um sistema de Ponto de Venda (PDV), responsável por gerenciar produtos, mesas e pedidos, aplicando regras de negócio relacionadas ao fluxo de atendimento e cálculo de valores.

As responsabilidades da API incluem: 

- gerenciamento de produtos
- criação e controle de mesas com regras de transição de status
- criação e controle de status de pedidos
- adição de itens ao pedido
- registro de vendas
- cálculo de subtotal/total

## Arquitetura e decisões de Projeto

A aplicação segue uma arquitetura em camadas, baseada em MVC:

- Utilização de SQL puro para maior controle sobre queries e entendimento do banco
- Regras de negócio centralizadas na camada Service
- Tratamento de erros com exceptions customizadas
- Controle de estado de entidades com validações explícitas

---

Controller  
↓  
Service (regras de negócio)  
↓  
Repository (acesso ao banco)  
↓  
Database

Principais conceitos utilizados:

- arquitetura REST
- separação de responsabilidades
- DTOs para comunicação da API
- validação de dados
- tratamento de erros

---

## Estrutura do Projeto
```
src
└── main
    └── java
        └── com.store.pdvapi
            ├── controller
            ├── dto
            ├── enumtype
            ├── exception
            ├── mapper
            ├── model
            ├── repository
            └── service
```            

## Tecnologias utilizadas 

- Java 21
- Spring Boot
- Spring Web
- Jakarta Validation
- Maven
- Swagger / OpenAPI
- PostgreSQL
- Docker

## Funcionalidades

Funcionalidades atualmente implementadas:

### Produtos

- cadastrar produto
- listar produtos
- buscar produto por id

### Pedidos

- criar pedido
- consultar pedido
- alterar status do pedido

### Itens do Pedido

- adicionar item ao pedido
- definir quantidade e preço
- cálculo automático de subtotal

---

## 💻 Como executar o Projeto

## Pré-requisitos

Escolha uma das opções abaixo:

**Com Docker:**
- Docker
- Git

> No Linux, certifique-se de que seu usuário tem permissão para usar o Docker sem `sudo`:
> ```bash
> sudo usermod -aG docker $USER
> # faça logout e login novamente
> ```

**Local:**
- Java 21+
- Maven
- PostgreSQL
- Git

---

## 1. Clone o repositório

``` bash
git clone https://github.com/lucas-luch/pdv-api.git
cd pdv-api
```

---

## 2. Configuração e banco de dados

Execute o script de setup:

```bash
./scripts/setup-db.sh
```

O script irá perguntar como deseja executar a aplicação:

**Opção 1 — Docker:**

Cria o arquivo `.env` a partir do `.env.example` caso não exista, sobe os containers e aplica o schema do banco automaticamente. A aplicação já estará disponível ao final.

**Opção 2 — Local:**

Cria o banco de dados e aplica o schema via `psql`. Também copia o `application.properties.example` para `application.properties` caso não exista — edite o arquivo com suas credenciais antes de iniciar a aplicação.

---

## 3. Executar a aplicação (somente fluxo local)

Execute o projeto com o Maven Wrapper:
``` bash
./mvnw spring-boot:run
```
ou com Maven instalado:
``` bash
mvn spring-boot:run
```
---

## 4. Acessar a aplicação

A API ficará disponível em:

http://localhost:8080

## 5. Documentação da API

A documentação da API está disponível via Swagger:

`http://localhost:8080/swagger-ui.html`


<p align="center">
  <img src="docs/swagger-ui.png" width="800">
</p>

