CREATE TABLE IF NOT EXISTS promotion_issue
(
    id            BIGINT NOT NULL AUTO_INCREMENT,
    promotion_id  BIGINT NOT NULL,
    user_id       VARCHAR(255) NOT NULL,
    issued_at     DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_promotion_issue_promotion_user UNIQUE (promotion_id, user_id),
    CONSTRAINT fk_promotion_issue_promotion FOREIGN KEY (promotion_id) REFERENCES promotion (id)
);
