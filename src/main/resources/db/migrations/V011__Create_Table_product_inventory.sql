CREATE TABLE IF NOT EXISTS product_inventory
(
    id               BIGSERIAL PRIMARY KEY,
    quantity         INT                                 NOT NULL CHECK (quantity >= 0),
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_at TIMESTAMP                           NULL
);

-- Function to validate quantity of product
CREATE OR REPLACE FUNCTION validate_quantity()
    RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.quantity < 0 THEN
        RAISE EXCEPTION 'Quantity must be greater or equal than 0';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to validate quantity before insert or update
CREATE TRIGGER validate_quantity_trigger
    BEFORE INSERT OR UPDATE
    ON product_inventory
    FOR EACH ROW
EXECUTE FUNCTION validate_quantity();