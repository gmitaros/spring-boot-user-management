-- Check and insert user if it doesn't exist
INSERT INTO users (first_name, last_name, email, password, account_locked, enabled, created_date)
SELECT 'Test',
       'Integration',
       'john.doe@example.com',
       '$2a$10$lG1O3ZA0EYOlpMZpUGCazOKjmjnohf9RGtPlaPgrcIzJnAdlPfPSm',
       0,
       0,
       NOW()
WHERE NOT EXISTS (SELECT 1
                  FROM users
                  WHERE email = 'john.doe@example.com');
-- Check and insert user role for ROLE_USER if it doesn't exist
INSERT INTO users_roles (roles_id, user_id)
SELECT (SELECT id FROM roles WHERE name = 'ROLE_USER'), (SELECT id FROM users WHERE email = 'john.doe@example.com')
WHERE NOT EXISTS (SELECT 1
                  FROM users_roles
                  WHERE roles_id = (SELECT id FROM roles WHERE name = 'ROLE_USER')
                    AND user_id = (SELECT id FROM users WHERE email = 'john.doe@example.com'));
-----------------------------------------------------------------------------------------------------------
-- Check and insert user if it doesn't exist
INSERT INTO users (first_name, last_name, email, password, account_locked, enabled, created_date)
SELECT 'Dokimastikos ',
       'Xristis',
       'test@example.com',
       '$2a$10$lG1O3ZA0EYOlpMZpUGCazOKjmjnohf9RGtPlaPgrcIzJnAdlPfPSm',
       0,
       0,
       NOW()
WHERE NOT EXISTS (SELECT 1
                  FROM users
                  WHERE email = 'test@example.com');
-- Check and insert user role for ROLE_USER if it doesn't exist
INSERT INTO users_roles (roles_id, user_id)
SELECT (SELECT id FROM roles WHERE name = 'ROLE_USER'), (SELECT id FROM users WHERE email = 'test@example.com')
WHERE NOT EXISTS (SELECT 1
                  FROM users_roles
                  WHERE roles_id = (SELECT id FROM roles WHERE name = 'ROLE_USER')
                    AND user_id = (SELECT id FROM users WHERE email = 'test@example.com'));

