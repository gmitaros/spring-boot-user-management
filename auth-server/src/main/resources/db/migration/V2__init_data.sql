INSERT INTO roles (name, created_date)
VALUES ('ROLE_USER', NOW());
INSERT INTO roles (name, created_date)
VALUES ('ROLE_ADMIN', NOW());

INSERT INTO users (first_name, last_name, email, password, account_locked, enabled, created_date)
VALUES ('Giorgos', 'Mitaros', 'gmitaros@gmail.com', '$2a$10$ZtcJdGNi2EZ3s0PT./mQs.XOnWVdFERtH1yJ12WGp7jj.dlWLInZG',
        FALSE, TRUE, NOW());

INSERT INTO users_roles (roles_id, user_id)
VALUES ((select id from roles where name = 'ROLE_USER'), (select id from users where email = 'gmitaros@gmail.com'));
INSERT INTO users_roles (roles_id, user_id)
VALUES ((select id from roles where name = 'ROLE_ADMIN'), (select id from users where email = 'gmitaros@gmail.com'))