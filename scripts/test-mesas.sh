#!/bin/bash
set -euo pipefail

BASE="http://localhost:8080/mesas"

echo "1. Criar mesa"
curl -s -X POST "$BASE" \
  -H "Content-Type: application/json" \
  -d '{"numero":"01","observacao":"teste"}' | jq .

echo "2. Listar mesas"
curl -s "$BASE" | jq .

echo "3. Abrir mesa 1"
curl -s -X PATCH "$BASE/1/abrir" \
  -H "Content-Type: application/json" \
  -d '{"observacao":"cliente chegou"}' | jq .

echo "4. Fechar mesa 1"
curl -s -X PATCH "$BASE/1/fechar" \
  -H "Content-Type: application/json" \
  -d '{"observacao":"pagamento"}' | jq .

echo "5. Liberar mesa 1"
curl -s -X PATCH "$BASE/1/liberar" \
  -H "Content-Type: application/json" \
  -d '{"observacao":"mesa pronta"}' | jq .
