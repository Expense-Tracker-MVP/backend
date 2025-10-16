-- Create expenses table
CREATE TABLE expenses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    category_id UUID REFERENCES categories(id) ON DELETE SET NULL,
    description TEXT,
    amount NUMERIC(12,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'SGD',
    transaction_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    source VARCHAR(50) DEFAULT 'manual' -- 'manual', 'imported', 'ocr', 'ai'
);

-- Indexes for efficient queries
CREATE INDEX IF NOT EXISTS idx_expenses_user_id 
ON expenses (user_id);

CREATE INDEX IF NOT EXISTS idx_expenses_transaction_date 
ON expenses (transaction_date);

CREATE INDEX IF NOT EXISTS idx_expenses_category_id 
ON expenses (category_id);