CREATE TABLE IF NOT EXISTS type
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(20) NOT NULL,
    description TEXT
);