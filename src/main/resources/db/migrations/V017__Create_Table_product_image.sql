CREATE TABLE IF NOT EXISTS product_image
(
    id               BIGSERIAL PRIMARY KEY,
    product_id       BIGINT                              NOT NULL,
    image_no         VARCHAR(255)                        NOT NULL,
    extension        VARCHAR(4)                          NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_at TIMESTAMP                           NULL,
    FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE
);

ALTER TABLE product_image
    ADD CONSTRAINT uc_product_image__image_no UNIQUE (image_no);
);