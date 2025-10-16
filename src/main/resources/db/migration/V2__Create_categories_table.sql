-- Create categories table
CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(1000),
    color VARCHAR(20),
    undeletable BOOLEAN NOT NULL DEFAULT FALSE
);

-- Index for fast user_id lookups
CREATE INDEX IF NOT EXISTS idx_categories_user_id 
ON categories (user_id);