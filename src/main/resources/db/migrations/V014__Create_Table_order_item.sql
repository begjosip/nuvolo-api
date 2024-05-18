CREATE TABLE IF NOT EXISTS order_item
(
    id               BIGSERIAL PRIMARY KEY,
    product_id       BIGINT                              NOT NULL,
    order_id         BIGINT                              NOT NULL,
    quantity         INT                                 NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_at TIMESTAMP                           NULL,
    FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES order_details (id) ON DELETE CASCADE
);