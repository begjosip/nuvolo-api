CREATE TABLE IF NOT EXISTS product
(
    id                   BIGSERIAL PRIMARY KEY,
    type_id              BIGINT                              NOT NULL,
    category_id          BIGINT                              NOT NULL,
    discount_id          BIGINT                              NULL,
    product_inventory_id BIGINT                              NOT NULL,
    name                 VARCHAR(255)                        NOT NULL,
    description          TEXT                                NOT NULL,
    price                DECIMAL(10, 2)                      NOT NULL CHECK (price >= 0),
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_at     TIMESTAMP                           NULL,
    FOREIGN KEY (type_id) REFERENCES type (id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE CASCADE,
    FOREIGN KEY (product_inventory_id) REFERENCES product_inventory (id) ON DELETE CASCADE ,
    FOREIGN KEY (discount_id) REFERENCES discount (id) ON DELETE SET NULL
);