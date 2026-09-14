CREATE TABLE inventory_items (
    id BIGSERIAL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL UNIQUE,
    quantity_available INTEGER NOT NULL
);

INSERT INTO inventory_items (product_name, quantity_available) VALUES
    ('Keyboard', 100),
    ('Mouse', 150),
    ('Monitor', 50);
