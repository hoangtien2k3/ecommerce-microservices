
CREATE TABLE categories (
	category_id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
	parent_category_id INT(11),
	category_title VARCHAR(255),
	image_url VARCHAR(255),
	created_at TIMESTAMP DEFAULT LOCALTIMESTAMP NOT NULL NULL_TO_DEFAULT,
	updated_at TIMESTAMP
);

-- Insert dữ liệu vào bảng categories
INSERT INTO categories (parent_category_id, category_title, image_url, created_at, updated_at)
VALUES
    (NULL, 'Electronics', 'http://hoangtien2k3.com/electronics.jpg', CURRENT_TIMESTAMP, NULL),
    (NULL, 'Clothing', 'http://hoangtien2k3.com/clothing.jpg', CURRENT_TIMESTAMP, NULL),
    (1, 'Laptops', 'http://hoangtien2k3.com/laptops.jpg', CURRENT_TIMESTAMP, NULL),
    (1, 'Smartphones', 'http://hoangtien2k3.com/smartphones.jpg', CURRENT_TIMESTAMP, NULL),
    (2, 'T-Shirts', 'http://hoangtien2k3.com/tshirts.jpg', CURRENT_TIMESTAMP, NULL);


CREATE TABLE products (
	product_id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
	category_id INT(11),
	product_title VARCHAR(255),
	image_url VARCHAR(255),
	sku VARCHAR(255),
	price_unit DECIMAL(7, 2),
	quantity INT(11),
	created_at TIMESTAMP DEFAULT LOCALTIMESTAMP NOT NULL NULL_TO_DEFAULT,
	updated_at TIMESTAMP
);


-- Insert dữ liệu vào bảng products
INSERT INTO products (category_id, product_title, image_url, sku, price_unit, quantity, created_at, updated_at)
VALUES
    (3, 'Laptop Model A', 'http://hoangtien2k3.com/laptopA.jpg', 'SKU001', 999.99, 10, CURRENT_TIMESTAMP, NULL),
    (3, 'Laptop Model B', 'http://hoangtien2k3.com/laptopB.jpg', 'SKU002', 1299.99, 5, CURRENT_TIMESTAMP, NULL),
    (4, 'Smartphone Model X', 'http://hoangtien2k3.com/smartphoneX.jpg', 'SKU101', 599.99, 20, CURRENT_TIMESTAMP, NULL),
    (4, 'Smartphone Model Y', 'http://hoangtien2k3.com/smartphoneY.jpg', 'SKU102', 699.99, 15, CURRENT_TIMESTAMP, NULL),
    (5, 'T-Shirt Red', 'http://hoangtien2k3.com/tshirtRed.jpg', 'SKU200', 19.99, 50, CURRENT_TIMESTAMP, NULL),
    (5, 'T-Shirt Blue', 'http://hoangtien2k3.com/tshirtBlue.jpg', 'SKU201', 19.99, 30, CURRENT_TIMESTAMP, NULL);


ALTER TABLE categories
    ADD CONSTRAINT fk7_assign
    FOREIGN KEY (parent_category_id)
    REFERENCES categories (category_id);


ALTER TABLE products
    ADD CONSTRAINT fk8_assign
    FOREIGN KEY (category_id)
    REFERENCES categories (category_id);
