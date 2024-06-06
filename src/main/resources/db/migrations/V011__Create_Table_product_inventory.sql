CREATE TABLE IF NOT EXISTS product_inventory
(
    id               BIGSERIAL PRIMARY KEY,
    quantity         INT                                 NOT NULL CHECK (quantity >= 0),
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_at TIMESTAMP                           NULL
);