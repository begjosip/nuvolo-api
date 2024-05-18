CREATE TABLE IF NOT EXISTS nuvolo_user
(
    id               BIGSERIAL PRIMARY KEY,
    first_name       VARCHAR(255)                        NOT NULL,
    last_name        VARCHAR(255)                        NOT NULL,
    email            VARCHAR(255)                        NOT NULL,
    password         VARCHAR(72)                         NOT NULL,
    is_enabled       BOOLEAN   DEFAULT FALSE             NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_at TIMESTAMP                           NULL
);

ALTER TABLE nuvolo_user
    ADD CONSTRAINT uc_nuvolo_user__email UNIQUE (email);