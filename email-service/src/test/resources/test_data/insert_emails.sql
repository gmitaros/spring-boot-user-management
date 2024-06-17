INSERT INTO email_service_te.emails (user_id, subject, message, recipient_email, sent_at, status)
SELECT 12,
       'Welcome to My Project',
        '<html><body><p>Hello gmitaros@yahoo.com,</p><p>Thank you for registering on My Project.</body></html>',
        'gmitaros@yahoo.com', '2024-06-16 21:36:23.477519 +00:00',
        'SENT'
WHERE NOT EXISTS (SELECT 1
                  FROM email_service_te.emails
                  WHERE user_id = 12);
