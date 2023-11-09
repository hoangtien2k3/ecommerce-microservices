
CREATE TABLE carts (
	cart_id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
	user_id INT(11),
	created_at TIMESTAMP DEFAULT LOCALTIMESTAMP NOT NULL NULL_TO_DEFAULT,
	updated_at TIMESTAMP
);


INSERT INTO carts
(user_id) VALUES
(1),
(2),
(3),
(4);


CREATE TABLE orders (
	order_id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
	cart_id INT(11),
	order_date TIMESTAMP DEFAULT LOCALTIMESTAMP NOT NULL NULL_TO_DEFAULT,
	order_desc VARCHAR(255),
	order_fee DECIMAL(7, 2),
	created_at TIMESTAMP DEFAULT LOCALTIMESTAMP NOT NULL NULL_TO_DEFAULT,
	updated_at TIMESTAMP
);


INSERT INTO orders
(cart_id, order_desc, order_fee) VALUES
(1, 'init', 5000),
(2, 'init', 5000),
(3, 'init', 5000),
(4, 'init', 5000);


ALTER TABLE orders
ADD CONSTRAINT fk5_assign FOREIGN KEY (cart_id) REFERENCES carts (cart_id);

