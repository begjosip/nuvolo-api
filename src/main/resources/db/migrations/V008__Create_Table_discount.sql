CREATE TABLE IF NOT EXISTS discount
(
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(255)                        NULL,
    description         TEXT                                NULL,
    discount_percentage DECIMAL(5, 2)                       NOT NULL CHECK (discount_percentage >= 0 AND discount_percentage <= 100),
    start_date          TIMESTAMP                           NOT NULL,
    end_date            TIMESTAMP                           NOT NULL,
    active              BOOLEAN                             NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_at    TIMESTAMP                           NULL
);

-- Function to validate discount_percentage
CREATE OR REPLACE FUNCTION validate_discount_percentage()
    RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.discount_percentage < 0 OR NEW.discount_percentage > 100 THEN
        RAISE EXCEPTION 'Discount percentage must be between 0 and 100 inclusive';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to validate discount_percentage before insert or update
CREATE TRIGGER validate_discount_percentage_trigger
    BEFORE INSERT OR UPDATE
    ON discount
    FOR EACH ROW
EXECUTE FUNCTION validate_discount_percentage();