CREATE TABLE IF NOT EXISTS type
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(20) NOT NULL,
    description TEXT
);

ALTER TABLE type
    ADD CONSTRAINT uc_type__name UNIQUE (name);