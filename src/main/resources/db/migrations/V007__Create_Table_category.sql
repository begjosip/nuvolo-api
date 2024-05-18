CREATE TABLE IF NOT EXISTS category
(
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(100)                        NOT NULL,
    description      TEXT                                NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_at TIMESTAMP                           NULL
);

ALTER TABLE category
    ADD CONSTRAINT uc_category__name UNIQUE (name);