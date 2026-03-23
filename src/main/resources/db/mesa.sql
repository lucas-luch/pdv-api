CREATE TABLE mesa (
  id BIGSERIAL PRIMARY KEY,
  numero VARCHAR(100) UNIQUE NOT NULL,
  status VARCHAR(20) NOT NULL,
  observacao TEXT,
  data_abertura TIMESTAMP,
  data_fechamento TIMESTAMP
);
