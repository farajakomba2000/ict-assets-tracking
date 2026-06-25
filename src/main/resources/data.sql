INSERT INTO user_account (id, username, password, enabled, check_number) VALUES (1, 'alice', '$2a$10$DOWSD1rzpS60MWTQicYKuOFGG9kW3nqOJQx8P4MY6jE6JzmcJcNfW', true, 1001);
INSERT INTO user_account (id, username, password, enabled, check_number) VALUES (2, 'bob', '$2a$10$DOWSD1rzpS60MWTQicYKuOFGG9kW3nqOJQx8P4MY6jE6JzmcJcNfW', true, 1002);
INSERT INTO user_account (id, username, password, enabled, check_number) VALUES (3, 'admin', '$2a$10$DOWSD1rzpS60MWTQicYKuOFGG9kW3nqOJQx8P4MY6jE6JzmcJcNfW', true, 1003);

INSERT INTO user_roles (user_id, role) VALUES (1, 'ROLE_USER');
INSERT INTO user_roles (user_id, role) VALUES (2, 'ROLE_MANAGER');
INSERT INTO user_roles (user_id, role) VALUES (3, 'ROLE_ADMIN');

INSERT INTO asset (id, name, description, serial_number, owner_id) VALUES (1, 'Laptop', 'Dell Latitude for finance team', 'SN-ICT-001', 1);
INSERT INTO asset (id, name, description, serial_number, owner_id) VALUES (2, 'Printer', 'Network printer for marketing', 'SN-ICT-002', 2);
INSERT INTO asset (id, name, description, serial_number, owner_id) VALUES (3, 'Mobile Phone', 'Company phone for admin', 'SN-ICT-003', 3);
