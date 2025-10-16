-- Add cascading deletes for better data integrity
-- When a category is deleted, all its expenses should also be deleted
-- When an expense is deleted, all its files should also be deleted

-- First, drop the existing foreign key constraints that need to be updated
ALTER TABLE expenses DROP CONSTRAINT IF EXISTS expenses_category_id_fkey;
ALTER TABLE files DROP CONSTRAINT IF EXISTS files_expense_id_fkey;

-- Add the foreign key constraints with proper cascading deletes
ALTER TABLE expenses 
ADD CONSTRAINT expenses_category_id_fkey 
FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE;

ALTER TABLE files 
ADD CONSTRAINT files_expense_id_fkey 
FOREIGN KEY (expense_id) REFERENCES expenses(id) ON DELETE CASCADE;