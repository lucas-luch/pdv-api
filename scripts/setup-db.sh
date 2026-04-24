#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"
ENV_FILE="$ROOT_DIR/.env"

if ! command -v docker &> /dev/null; then
  echo "Erro: Docker não encontrado. Instale o Docker e tente novamente."
  exit 1
fi

if [ ! -f "$ENV_FILE" ]; then
  echo "Arquivo .env não encontrado. Copiando do .env.example..."
  cp "$ROOT_DIR/.env.example" "$ENV_FILE"
  echo "Arquivo .env criado em $ENV_FILE"
  echo "Edite o arquivo com suas credenciais antes de continuar."
  echo ""
  read -rp "Pressione Enter após editar o .env para continuar..."
fi

echo ""
echo "Subindo containers..."
docker compose --env-file "$ENV_FILE" -f "$ROOT_DIR/docker-compose.yml" up -d

echo ""
echo "Pronto! Aplicação disponível em http://localhost:8080"
