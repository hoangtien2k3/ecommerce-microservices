-- Tạo bảng hibernate_sequence
CREATE TABLE hibernate_sequence (
    next_val BIGINT
) ENGINE=MyISAM;

-- Thêm dữ liệu vào bảng hibernate_sequence
INSERT INTO hibernate_sequence VALUES (1);

-- Tạo bảng roles
CREATE TABLE roles (
    id BIGINT NOT NULL,
    role_name VARCHAR(60),
    PRIMARY KEY (id)
) ENGINE=MyISAM;

INSERT INTO roles(id, role_name) VALUES
(1, 'USER'),
(2, 'PM'),
(3, 'ADMIN');

-- Tạo chỉ mục unique cho cột role_name trong bảng roles
ALTER TABLE roles
    DROP INDEX UK_nb4h0p6txrmfc0xbrd1kglp9t;

ALTER TABLE roles
    ADD CONSTRAINT UK_nb4h0p6txrmfc0xbrd1kglp9t UNIQUE (role_name);

-- Tạo bảng user_role
CREATE TABLE user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
) ENGINE=MyISAM;

-- Thêm các ràng buộc khóa ngoại cho bảng user_role
ALTER TABLE user_role
    ADD CONSTRAINT FKt7e7djp752sqn6w22i6ocqy6q
    FOREIGN KEY (role_id)
    REFERENCES roles (id);

ALTER TABLE user_role
    ADD CONSTRAINT FKj345gk1bovqvfame88rcx7yyx
    FOREIGN KEY (user_id)
    REFERENCES users (user_id);

-- Tạo bảng users
CREATE TABLE users (
    user_id BIGINT NOT NULL AUTO_INCREMENT,
    image_url LONGTEXT,
    email VARCHAR(50),
    full_name VARCHAR(50),
    gender VARCHAR(255) NOT NULL,
    password VARCHAR(100),
    phone_number VARCHAR(11),
    user_name VARCHAR(50),
    PRIMARY KEY (user_id)
) ENGINE=MyISAM;

-- Tạo các chỉ mục unique cho cột user_name và email trong bảng users
ALTER TABLE users
    DROP INDEX UKr43af9ap4edm43mmtq01oddj6;

ALTER TABLE users
    ADD CONSTRAINT UKr43af9ap4edm43mmtq01oddj6 UNIQUE (user_name);

ALTER TABLE users
    DROP INDEX UK6dotkott2kjsp8vw4d0m25fb7;

ALTER TABLE users
    ADD CONSTRAINT UK6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);
