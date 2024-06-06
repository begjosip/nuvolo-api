CREATE TABLE IF NOT EXISTS review
(
    id               BIGSERIAL PRIMARY KEY,
    comment          VARCHAR(255)                        NULL,
    rating           INT                                 NOT NULL CHECK (rating >= 1 AND rating <= 5),
    product_id       BIGINT                              NOT NULL,
    user_id          BIGINT                              NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_at TIMESTAMP                           NULL,
    FOREIGN KEY (user_id) REFERENCES nuvolo_user (id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE
);