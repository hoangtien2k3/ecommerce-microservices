CREATE USER IF NOT EXISTS 'app_prod'@'10.0.1.%' IDENTIFIED BY '<PASSWORD>';
CREATE USER IF NOT EXISTS 'app_dev'@'10.0.1.%'  IDENTIFIED BY '<PASSWORD>';
CREATE USER IF NOT EXISTS 'monitoring'@'10.0.1.%'  IDENTIFIED BY '<PASSWORD>';
CREATE USER IF NOT EXISTS 'qa'@'10.0.1.%'  IDENTIFIED BY '<PASSWORD>';
CREATE USER IF NOT EXISTS 'qa_setup'@'10.0.1.%'  IDENTIFIED BY '<PASSWORD>';
CREATE USER IF NOT EXISTS 'keycloak'@'10.0.1.%'  IDENTIFIED BY '<PASSWORD>';
CREATE USER IF NOT EXISTS 'keycloak_dev'@'10.0.1.%'  IDENTIFIED BY '<PASSWORD>';

-- app_prod
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, DROP, REFERENCES ON authservice.*      TO 'app_prod'@'10.0.1.%';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, DROP, REFERENCES ON productservice.*   TO 'app_prod'@'10.0.1.%';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, DROP, REFERENCES ON orderservice.*     TO 'app_prod'@'10.0.1.%';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, DROP, REFERENCES ON promotionservice.* TO 'app_prod'@'10.0.1.%';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, DROP, REFERENCES ON keycloak.*         TO 'app_prod'@'10.0.1.%';

-- app_dev
GRANT ALL PRIVILEGES ON authservice_dev.*      TO 'app_dev'@'10.0.1.%';
GRANT ALL PRIVILEGES ON productservice_dev.*   TO 'app_dev'@'10.0.1.%';
GRANT ALL PRIVILEGES ON orderservice_dev.*     TO 'app_dev'@'10.0.1.%';
GRANT ALL PRIVILEGES ON promotionservice_dev.* TO 'app_dev'@'10.0.1.%';
GRANT ALL PRIVILEGES ON keycloak_dev.*         TO 'app_dev'@'10.0.1.%';

-- monitoring
GRANT SELECT ON authservice.*          TO 'monitoring'@'10.0.1.%';
GRANT SELECT ON productservice.*       TO 'monitoring'@'10.0.1.%';
GRANT SELECT ON orderservice.*         TO 'monitoring'@'10.0.1.%';
GRANT SELECT ON promotionservice.*     TO 'monitoring'@'10.0.1.%';
GRANT SELECT ON keycloak.*             TO 'monitoring'@'10.0.1.%';
GRANT SELECT ON authservice_dev.*      TO 'monitoring'@'10.0.1.%';
GRANT SELECT ON productservice_dev.*   TO 'monitoring'@'10.0.1.%';
GRANT SELECT ON orderservice_dev.*     TO 'monitoring'@'10.0.1.%';
GRANT SELECT ON promotionservice_dev.* TO 'monitoring'@'10.0.1.%';
GRANT SELECT ON keycloak_dev.*         TO 'monitoring'@'10.0.1.%';

-- qa
GRANT SELECT ON authservice.*          TO 'qa'@'10.0.1.%';
GRANT SELECT ON productservice.*       TO 'qa'@'10.0.1.%';
GRANT SELECT ON orderservice.*         TO 'qa'@'10.0.1.%';
GRANT SELECT ON promotionservice.*     TO 'qa'@'10.0.1.%';
GRANT SELECT ON keycloak.*             TO 'qa'@'10.0.1.%';
GRANT SELECT ON authservice_dev.*      TO 'qa'@'10.0.1.%';
GRANT SELECT ON productservice_dev.*   TO 'qa'@'10.0.1.%';
GRANT SELECT ON orderservice_dev.*     TO 'qa'@'10.0.1.%';
GRANT SELECT ON promotionservice_dev.* TO 'qa'@'10.0.1.%';
GRANT SELECT ON keycloak_dev.*         TO 'qa'@'10.0.1.%';

-- qa_setup
GRANT SELECT, INSERT, UPDATE, DELETE ON authservice.*      TO 'qa_setup'@'10.0.1.%';
GRANT SELECT, INSERT, UPDATE, DELETE ON productservice.*   TO 'qa_setup'@'10.0.1.%';
GRANT SELECT, INSERT, UPDATE, DELETE ON orderservice.*     TO 'qa_setup'@'10.0.1.%';
GRANT SELECT, INSERT, UPDATE, DELETE ON promotionservice.* TO 'qa_setup'@'10.0.1.%';
GRANT SELECT, INSERT, UPDATE, DELETE ON keycloak.*         TO 'qa_setup'@'10.0.1.%';
GRANT ALL PRIVILEGES ON authservice_dev.*      TO 'qa_setup'@'10.0.1.%';
GRANT ALL PRIVILEGES ON productservice_dev.*   TO 'qa_setup'@'10.0.1.%';
GRANT ALL PRIVILEGES ON orderservice_dev.*     TO 'qa_setup'@'10.0.1.%';
GRANT ALL PRIVILEGES ON promotionservice_dev.* TO 'qa_setup'@'10.0.1.%';
GRANT ALL PRIVILEGES ON keycloak_dev.*         TO 'qa_setup'@'10.0.1.%';

-- keycloak, keycloak_dev
GRANT ALL PRIVILEGES ON keycloak.*     TO 'keycloak'@'10.0.1.%';
GRANT ALL PRIVILEGES ON keycloak_dev.* TO 'keycloak_dev'@'10.0.1.%';
