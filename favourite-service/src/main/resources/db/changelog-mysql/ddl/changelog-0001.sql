CREATE TABLE IF NOT EXISTS favourites
(
    user_id    INT NOT NULL,
    product_id INT NOT NULL,
    like_date  DATETIME(6) NOT NULL,
    PRIMARY KEY (user_id, product_id, like_date)
);
