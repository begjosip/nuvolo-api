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

-- Function to validate review rating
CREATE OR REPLACE FUNCTION validate_review_rating()
    RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.rating <= 0 OR NEW.rating > 5 THEN
        RAISE EXCEPTION 'Product rating needs to be between 1 and 5 included.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to validate review rating before insert or update
CREATE TRIGGER validate_price_trigger
    BEFORE INSERT OR UPDATE
    ON review
    FOR EACH ROW
EXECUTE FUNCTION validate_review_rating();