-- Create files table
CREATE TABLE files (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  expense_id UUID REFERENCES public.expenses(id) ON DELETE SET NULL, -- nullable for reminder images
  file_name VARCHAR(255) NOT NULL,
  file_type VARCHAR(50),      -- e.g., 'image/jpeg', 'application/pdf'
  file_path TEXT NOT NULL,    -- storage path
  purpose VARCHAR(50) NOT NULL DEFAULT 'general', -- 'receipt', 'reminder', 'other'
  uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for efficient queries
CREATE INDEX IF NOT EXISTS idx_files_user_id 
ON files (user_id);

CREATE INDEX IF NOT EXISTS idx_files_expense_id 
ON files (expense_id);

CREATE INDEX IF NOT EXISTS idx_files_purpose 
ON files (purpose);