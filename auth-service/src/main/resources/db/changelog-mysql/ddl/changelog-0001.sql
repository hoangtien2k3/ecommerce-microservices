CREATE TABLE IF NOT EXISTS roles
(
    id        BIGINT NOT NULL AUTO_INCREMENT,
    role_name VARCHAR(60) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_roles_role_name (role_name)
);

CREATE TABLE IF NOT EXISTS users
(
    user_id          BIGINT NOT NULL AUTO_INCREMENT,
    full_name        VARCHAR(100) NULL,
    user_name        VARCHAR(100) NOT NULL,
    email            VARCHAR(50) NOT NULL,
    gender           VARCHAR(255) NOT NULL,
    phone_number     VARCHAR(11) NULL,
    image_url        TEXT NULL,
    keycloak_user_id VARCHAR(64) NULL,
    PRIMARY KEY (user_id),
    UNIQUE KEY unique_username (user_name),
    UNIQUE KEY unique_email (email),
    UNIQUE KEY unique_phone (phone_number),
    UNIQUE KEY uk_users_keycloak_user_id (keycloak_user_id)
);

CREATE TABLE IF NOT EXISTS user_role
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES roles (id)
);
