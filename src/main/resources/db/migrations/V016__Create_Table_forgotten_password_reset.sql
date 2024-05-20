CREATE TABLE IF NOT EXISTS forgotten_pass_reset
(
    id               BIGSERIAL PRIMARY KEY,
    token            VARCHAR(36)                         NOT NULL,
    utilised         BOOLEAN   DEFAULT FALSE             NOT NULL,
    user_id          BIGINT                              NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_at TIMESTAMP                           NULL,
    FOREIGN KEY (user_id) REFERENCES nuvolo_user (id) ON DELETE CASCADE
);

ALTER TABLE forgotten_pass_reset
    ADD CONSTRAINT uc_forgotten_pass_reset__token UNIQUE (token);
);