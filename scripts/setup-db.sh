#!/bin/bash
set -euo pipefail

PSQL="sudo -u postgres psql -d pdv"

echo "Criando tabelas"
$PSQL -f src/main/resources/db/produto.sql
$PSQL -f src/main/resources/db/mesa.sql
$PSQL -f src/main/resources/db/pedido.sql

echo "Ajustando permissões"
$PSQL -c "GRANT USAGE ON SCHEMA public TO pdv_user;"
$PSQL -c "GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO pdv_user;"
$PSQL -c "GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO pdv_user;"

echo "Setup concluído"
