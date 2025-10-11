-- Create users table for OAuth authentication
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    provider VARCHAR(50) NOT NULL,
    provider_id VARCHAR(255) NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Composite unique constraint for provider lookups
    CONSTRAINT uk_users_provider_id UNIQUE (provider, provider_id)
);

-- Index for email lookups (only if it doesn't exist)
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Index for provider lookups (only if it doesn't exist)
CREATE INDEX IF NOT EXISTS idx_users_provider_provider_id ON users(provider, provider_id);