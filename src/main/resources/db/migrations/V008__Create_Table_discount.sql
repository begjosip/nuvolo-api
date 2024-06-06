CREATE TABLE IF NOT EXISTS discount
(
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(255)                        NULL,
    description         TEXT                                NULL,
    discount_percentage DECIMAL(5, 2)                       NOT NULL CHECK (discount_percentage >= 0 AND discount_percentage < 1),
    start_date          TIMESTAMP                           NOT NULL,
    end_date            TIMESTAMP                           NOT NULL,
    active              BOOLEAN                             NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_at    TIMESTAMP                           NULL
);