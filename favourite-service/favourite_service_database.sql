CREATE TABLE favourites
(
    user_id    INT                                 NOT NULL,
    product_id INT                                 NOT NULL,
    like_date  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, product_id, like_date)
);


INSERT INTO favourites(user_id, product_id)
VALUES (1, 1),
       (1, 2),
       (2, 2);

