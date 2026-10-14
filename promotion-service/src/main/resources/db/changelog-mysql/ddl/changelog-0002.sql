ALTER TABLE promotion
    ADD COLUMN issue_limit INT NULL,
    ADD COLUMN issued_count INT NOT NULL DEFAULT 0;
