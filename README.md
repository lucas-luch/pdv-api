# pdv-api
API REST de um sistema de ponto de venda (PDV).

## Tecnologias

- Java 17+
- Spring Boot (Spring Web)
- PostgreSQL
- JDBC
- Docker
- Swagger

## Funcionalidades 

- Cadastro e gerenciamento de produtos
- Cadastro de clientes
- Registro de vendas
- Controle de estoque
- Controle de mesas com regras de transição de status

## Arquitetura e decisões

- Utilização de SQL puro para maior controle sobre queries e entendimento do banco
- Regras de negócio centralizadas na camada Service
- Tratamento de erros com exceptions customizadas
- Controle de estado de entidades com validações explícitas


## Banco de dados

Antes de iniciar a aplicação, é necessário criar o schema do banco.

Opção 1: Script automatizado em pdvapi/scripts

./setup-db.sh

Opção 2: Execução manual

sudo -u postgres psql -d pdv -f src/main/resources/db/produto.sql
sudo -u postgres psql -d pdv -f src/main/resources/db/mesa.sql

Permissões (caso necessário)

sudo -u postgres psql -d pdv -c "GRANT USAGE ON SCHEMA public TO pdv_user;"
sudo -u postgres psql -d pdv -c "GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO pdv_user;"
sudo -u postgres psql -d pdv -c "GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public 