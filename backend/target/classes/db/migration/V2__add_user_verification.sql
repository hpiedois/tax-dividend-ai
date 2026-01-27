ALTER TABLE users ADD COLUMN status VARCHAR(50) DEFAULT 'ACTIVE'; -- Default active for existing dev users
ALTER TABLE users ADD COLUMN verification_token VARCHAR(255);
ALTER TABLE users ADD COLUMN token_expiry TIMESTAMP;

-- Ideally strict PENDING for new applications, but since we have dev data, we migrate safely.
-- For new users, application code will set to PENDING.
