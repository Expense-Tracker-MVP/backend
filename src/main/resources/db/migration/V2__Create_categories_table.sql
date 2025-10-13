-- Create categories table
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(1000),
    color VARCHAR(20)
);

-- Index for fast user_id lookups
CREATE INDEX IF NOT EXISTS idx_categories_user_id 
ON categories (user_id);