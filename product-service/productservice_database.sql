
CREATE TABLE categories (
	category_id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
	parent_category_id INT(11),
	category_title VARCHAR(255),
	image_url VARCHAR(255),
	created_at TIMESTAMP DEFAULT LOCALTIMESTAMP NOT NULL NULL_TO_DEFAULT,
	updated_at TIMESTAMP
);


INSERT INTO categories (parent_category_id, category_title)
VALUES  (null, 'Computer'),
        (null, 'Mode'),
        (null, 'Game');


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


INSERT INTO products (category_id, product_title, image_url, sku, price_unit, quantity)
VALUES  (1, 'asus', 'xxx', 'hoangtien2k3', 0, 50),
        (1, 'hp', 'xxx', 'tie-in', 0, 50),
        (2, 'Armani', 'xxx', 'hoangtien', 0, 50),
        (3, 'GTA', 'xxx', 'hoanganhtien', 0, 50);


ALTER TABLE categories
    ADD CONSTRAINT fk7_assign
    FOREIGN KEY (parent_category_id)
    REFERENCES categories (category_id);


ALTER TABLE products
    ADD CONSTRAINT fk8_assign
    FOREIGN KEY (category_id)
    REFERENCES categories (category_id);
