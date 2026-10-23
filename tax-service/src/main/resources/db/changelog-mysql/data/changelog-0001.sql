INSERT IGNORE INTO tax_class (id, `name`, created_by, created_on, last_modified_by, last_modified_on)
VALUES (1, 'Value Added Tax (VAT)', '6a4ccf58-14a7-4c68-8f35-9107f98755b2', '2024-08-01 04:14:58.952', '6a4ccf58-14a7-4c68-8f35-9107f98755b2', '2024-08-01 04:16:40.758');

INSERT IGNORE INTO tax_rate (id, rate, zip_code, tax_class_id, country_id, created_by, created_on, last_modified_by, last_modified_on)
VALUES (1, 8.0, '70000', 1, 1, '6a4ccf58-14a7-4c68-8f35-9107f98755b2', '2024-08-01 04:16:22.909', '6a4ccf58-14a7-4c68-8f35-9107f98755b2', '2024-08-01 04:16:22.909');
