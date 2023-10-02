
CREATE TABLE order_items (
	product_id INT(11) NOT NULL,
	order_id INT(11) NOT NULL,
	ordered_quantity INT(11),
	created_at TIMESTAMP DEFAULT LOCALTIMESTAMP NOT NULL NULL_TO_DEFAULT,
	updated_at TIMESTAMP,
	PRIMARY KEY (product_id, order_id)
);


INSERT INTO order_items
(product_id, order_id, ordered_quantity) VALUES
(1, 1, 2),
(1, 2, 1),
(2, 1, 1),
(2, 2, 1);

