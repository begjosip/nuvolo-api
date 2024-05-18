CREATE TABLE IF NOT EXISTS user_address
(
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT                              NOT NULL,
    street           VARCHAR(255)                        NOT NULL,
    city             VARCHAR(100)                        NOT NULL,
    state            VARCHAR(100)                        NOT NULL,
    zip_code         VARCHAR(20)                         NOT NULL,
    country          VARCHAR(100)                        NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_at TIMESTAMP                           NULL,
    FOREIGN KEY (user_id) REFERENCES nuvolo_user (id) ON DELETE CASCADE
);