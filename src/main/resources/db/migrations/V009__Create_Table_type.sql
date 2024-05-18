CREATE TABLE IF NOT EXISTS type
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(20) NOT NULL UNIQUE,
    description TEXT
);