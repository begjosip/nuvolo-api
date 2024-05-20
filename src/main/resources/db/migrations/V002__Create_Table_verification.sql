CREATE TABLE IF NOT EXISTS verification
(
    id               BIGSERIAL PRIMARY KEY,
    token            VARCHAR(36)                         NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_at TIMESTAMP                           NULL,
    is_verified      BOOLEAN   DEFAULT FALSE             NOT NULL,
    user_id          BIGINT                              NOT NULL,
    FOREIGN KEY (user_id) REFERENCES nuvolo_user (id)
);

ALTER TABLE verification
    ADD CONSTRAINT uc_verification__token UNIQUE (token);