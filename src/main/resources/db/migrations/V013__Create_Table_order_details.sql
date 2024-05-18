CREATE TABLE IF NOT EXISTS order_details
(
    id                   BIGSERIAL PRIMARY KEY,
    user_id              BIGINT                              NOT NULL,
    product_inventory_id BIGINT                              NOT NULL,
    total                DECIMAL(10, 2)                      NOT NULL,
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_at     TIMESTAMP                           NULL,
    FOREIGN KEY (user_id) REFERENCES nuvolo_user (id) ON DELETE CASCADE
);