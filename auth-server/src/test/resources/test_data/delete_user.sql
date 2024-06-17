DELETE
FROM users_roles
WHERE user_id = (select id from users where email = 'test@signup.com');

DELETE
FROM users
WHERE email = 'test@signup.com';