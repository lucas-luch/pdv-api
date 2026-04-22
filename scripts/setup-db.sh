#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"
SQL_FILE="$SCRIPT_DIR/init-db.sql"

# Verifica se o arquivo SQL existe
if [ ! -f "$SQL_FILE" ]; then
  echo "Erro: $SQL_FILE não encontrado."
  exit 1
fi

echo ""
echo "Como deseja executar a aplicação?"
echo "  1) Docker"
echo "  2) Local (PostgreSQL instalado)"
echo ""
read -rp "Escolha [1/2]: " OPCAO

# ─── Docker ───────────────────────────────────────────────────────────────────
if [ "$OPCAO" = "1" ]; then

  if ! command -v docker &> /dev/null; then
    echo "Erro: Docker não encontrado. Instale o Docker e tente novamente."
    exit 1
  fi

  ENV_FILE="$ROOT_DIR/.env"

  if [ ! -f "$ENV_FILE" ]; then
    echo ""
    echo "Arquivo .env não encontrado. Copiando do .env.example..."
    cp "$ROOT_DIR/.env.example" "$ENV_FILE"
    echo "Arquivo .env criado em $ENV_FILE"
    echo "Edite o arquivo com suas credenciais antes de continuar."
    echo ""
    read -rp "Pressione Enter após editar o .env para continuar..."
  fi

  export $(grep -v '^#' "$ENV_FILE" | xargs)

  echo ""
  echo "Subindo containers..."
  docker compose --env-file "$ENV_FILE" -f "$ROOT_DIR/docker-compose.yml" up -d

  CONTAINER="${POSTGRES_CONTAINER:-pdv-db}"
  DB_USER="${POSTGRES_USER:-pdv_user}"
  DB_NAME="${POSTGRES_DB:-pdv}"

  echo "Aguardando o banco ficar disponível..."
  until docker exec "$CONTAINER" pg_isready -U "$DB_USER" -d "$DB_NAME" -q; do
    sleep 1
  done

  echo "Aplicando schema..."
  docker exec -i "$CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" < "$SQL_FILE"

  echo ""
  echo "Pronto! Aplicação disponível em http://localhost:8080"

# ─── Local ────────────────────────────────────────────────────────────────────
elif [ "$OPCAO" = "2" ]; then

  if ! command -v psql &> /dev/null; then
    echo "Erro: psql não encontrado. Instale o PostgreSQL e tente novamente."
    exit 1
  fi

  DB_HOST="localhost"
  DB_USER="postgres"
  DB_NAME="pdv"

  echo ""
  echo "Configuração padrão: host=$DB_HOST, usuário=$DB_USER, banco=$DB_NAME"
  read -rp "Deseja alterar? [s/N]: " ALTERAR
  if [[ "$ALTERAR" =~ ^[sS]$ ]]; then
    read -rp "Host do PostgreSQL [$DB_HOST]: " INPUT_HOST
    DB_HOST="${INPUT_HOST:-$DB_HOST}"

    read -rp "Usuário do PostgreSQL [$DB_USER]: " INPUT_USER
    DB_USER="${INPUT_USER:-$DB_USER}"

    read -rp "Nome do banco [$DB_NAME]: " INPUT_NAME
    DB_NAME="${INPUT_NAME:-$DB_NAME}"
  fi

  echo ""
  echo "Criando banco '$DB_NAME' se não existir..."
  psql -h "$DB_HOST" -U "$DB_USER" -d postgres -tc "SELECT 1 FROM pg_database WHERE datname = '$DB_NAME'" \
    | grep -q 1 || psql -h "$DB_HOST" -U "$DB_USER" -d postgres -c "CREATE DATABASE $DB_NAME;"

  echo "Aplicando schema..."
  psql -h "$DB_HOST" -U "$DB_USER" -d "$DB_NAME" -f "$SQL_FILE"

  PROPS_FILE="$ROOT_DIR/src/main/resources/application.properties"
  if [ ! -f "$PROPS_FILE" ]; then
    echo ""
    echo "Arquivo application.properties não encontrado. Copiando do exemplo..."
    cp "$ROOT_DIR/src/main/resources/application.properties.example" "$PROPS_FILE"
    echo "Edite $PROPS_FILE com suas credenciais antes de rodar a aplicação."
  fi

  echo ""
  echo "Banco configurado. Para iniciar a aplicação:"
  echo "  ./mvnw spring-boot:run"

else
  echo "Opção inválida."
  exit 1
fi
