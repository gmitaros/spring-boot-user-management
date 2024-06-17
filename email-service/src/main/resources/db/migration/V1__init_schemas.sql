CREATE TABLE emails
(
    id              SERIAL PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    subject         VARCHAR(255) NOT NULL,
    message         TEXT         NOT NULL,
    recipient_email VARCHAR(255) NOT NULL,
    sent_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status          VARCHAR(50)  NOT NULL
);

-- Create an index on user_id for faster lookups
CREATE INDEX idx_user_id ON emails (user_id);
