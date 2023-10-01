
CREATE TABLE payments (
	payment_id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
	order_id INT(11),
	is_payed BOOLEAN,
	payment_status VARCHAR(255),
	created_at TIMESTAMP DEFAULT LOCALTIMESTAMP NOT NULL NULL_TO_DEFAULT,
	updated_at TIMESTAMP
);


INSERT INTO payments
(order_id, is_payed, payment_status) VALUES
(1, false, 'IN_PROGRESS'),
(2, false, 'IN_PROGRESS'),
(3, false, 'IN_PROGRESS'),
(4, false, 'IN_PROGRESS');

