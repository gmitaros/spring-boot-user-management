CREATE TABLE roles
(
    id                 SERIAL PRIMARY KEY,
    name               VARCHAR(255) UNIQUE NOT NULL,
    created_date       TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP           NULL     DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_name UNIQUE (name);

-- Users table
CREATE TABLE users
(
    id                 SERIAL PRIMARY KEY,
    first_name         VARCHAR(255)        NOT NULL,
    last_name          VARCHAR(255)        NOT NULL,
    email              VARCHAR(255) UNIQUE NOT NULL,
    password           VARCHAR(255)        NOT NULL,
    account_locked     BOOLEAN             NOT NULL,
    enabled            BOOLEAN             NOT NULL,
    created_date       TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP           NULL     DEFAULT CURRENT_TIMESTAMP
);

-- User roles relation table
CREATE TABLE users_roles
(
    roles_id INT NOT NULL,
    user_id  INT NOT NULL
);


ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (roles_id) REFERENCES roles (id);
ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES users (id);