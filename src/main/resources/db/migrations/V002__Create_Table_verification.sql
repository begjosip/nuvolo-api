CREATE TABLE IF NOT EXISTS verification
(
    id               BIGSERIAL PRIMARY KEY,
    token            CHAR(36)                            NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_at TIMESTAMP                           NULL,
    fk_user_id       BIGINT                              NOT NULL,
    FOREIGN KEY (fk_user_id) REFERENCES nuvolo_user (id)
);

ALTER TABLE verification
    ADD CONSTRAINT uc_verification__token UNIQUE (token);