CREATE TABLE IF NOT EXISTS pedido (
  id BIGSERIAL PRIMARY KEY,
  mesa_id BIGINT NOT NULL REFERENCES mesa(id),
  status VARCHAR(20) NOT NULL,
  data_criacao DATE NOT NULL,
  hora_criacao TIME NOT NULL
);
