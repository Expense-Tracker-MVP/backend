-- =====================================================
-- EXPENSE TRACKER MVP - DATABASE POPULATION SCRIPT
-- =====================================================
-- This script populates the database with sample data
-- Run this after your Flyway migrations have been executed
-- =====================================================

-- Clear existing data (uncomment if you want to start fresh)
-- Note: Due to cascading deletes, you only need to delete users to clean everything
-- DELETE FROM users;  -- This will cascade delete categories, expenses, and files
-- 
-- Or delete individually in proper order:
-- DELETE FROM files;
-- DELETE FROM expenses;
-- DELETE FROM categories;
-- DELETE FROM users;

-- =====================================================
-- 1. INSERT SAMPLE USERS
-- =====================================================

-- Note: Using specific UUIDs for consistency in sample data
-- In production, these would be generated automatically

INSERT INTO users (id, email, provider, provider_id, provider_user_id, display_name, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'john.doe@gmail.com', 'google', 'google_12345', '12345', 'John Doe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440002', 'jane.smith@outlook.com', 'microsoft', 'microsoft_67890', '67890', 'Jane Smith', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440003', 'alice.johnson@yahoo.com', 'google', 'google_11111', '11111', 'Alice Johnson', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440004', 'bob.wilson@gmail.com', 'google', 'google_22222', '22222', 'Bob Wilson', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

-- =====================================================
-- 2. INSERT SAMPLE CATEGORIES
-- =====================================================

-- Default "All" categories for each user (created first)

-- Insert categories with explicit UUIDs
-- John Doe
INSERT INTO categories (id, user_id, name, description, color, undeletable) VALUES
('c1a11111-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'All', 'Default category for all expenses', '#6C757D', TRUE),
('c1a11112-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'Food & Dining', 'Restaurant meals, groceries, food delivery', '#FF6B6B', FALSE),
('c1a11113-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'Transportation', 'Public transport, taxi, fuel, car maintenance', '#4ECDC4', FALSE),
('c1a11114-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'Shopping', 'Clothing, electronics, home goods', '#45B7D1', FALSE),
('c1a11115-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'Entertainment', 'Movies, games, concerts, streaming services', '#96CEB4', FALSE),
('c1a11116-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'Healthcare', 'Medical bills, pharmacy, health insurance', '#FFEAA7', FALSE),
('c1a11117-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'Utilities', 'Electricity, water, internet, phone bills', '#DDA0DD', FALSE),
('c1a11118-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'Education', 'Courses, books, training, certification', '#98D8C8', FALSE);

-- Jane Smith
INSERT INTO categories (id, user_id, name, description, color, undeletable) VALUES
('c2a11111-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', 'All', 'Default category for all expenses', '#6C757D', TRUE),
('c2a11112-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', 'Food & Dining', 'Meals and groceries', '#FF6B6B', FALSE),
('c2a11113-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', 'Transportation', 'Travel and commute expenses', '#4ECDC4', FALSE),
('c2a11114-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', 'Rent & Housing', 'Monthly rent, housing expenses', '#FF9FF3', FALSE),
('c2a11115-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', 'Health & Fitness', 'Gym, medical, wellness', '#FFEAA7', FALSE),
('c2a11116-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', 'Personal Care', 'Beauty, grooming, self-care', '#F8BBD0', FALSE);

-- Alice Johnson
INSERT INTO categories (id, user_id, name, description, color, undeletable) VALUES
('c3a11111-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440003', 'All', 'Default category for all expenses', '#6C757D', TRUE),
('c3a11112-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440003', 'Food & Dining', 'Daily meals and dining out', '#FF6B6B', FALSE),
('c3a11113-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440003', 'Transportation', 'Travel expenses', '#4ECDC4', FALSE),
('c3a11114-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440003', 'Home & Garden', 'Home improvement, gardening', '#8FBC8F', FALSE),
('c3a11115-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440003', 'Pets', 'Pet food, vet bills, pet supplies', '#FFB347', FALSE);

-- Bob Wilson
INSERT INTO categories (id, user_id, name, description, color, undeletable) VALUES
('c4a11111-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440004', 'All', 'Default category for all expenses', '#6C757D', TRUE),
('c4a11112-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440004', 'Food & Dining', 'Food and beverage expenses', '#FF6B6B', FALSE),
('c4a11113-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440004', 'Transportation', 'Transport and fuel', '#4ECDC4', FALSE),
('c4a11114-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440004', 'Technology', 'Gadgets, software, subscriptions', '#87CEEB', FALSE),
('c4a11115-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440004', 'Sports & Hobbies', 'Sports gear, hobby supplies', '#90EE90', FALSE);

-- =====================================================
-- 3. INSERT SAMPLE EXPENSES
-- =====================================================


-- Expenses for John Doe (last 3 months)
INSERT INTO expenses (id, user_id, category_id, description, amount, currency, transaction_date, created_at, updated_at, source) VALUES
-- Food & Dining expenses
('650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'c1a11112-e29b-41d4-a716-446655440001', 'Lunch at Hawker Center', 8.50, 'SGD', '2024-10-13', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),
('650e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', 'c1a11112-e29b-41d4-a716-446655440001', 'Grocery shopping at NTUC', 45.80, 'SGD', '2024-10-12', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),
('650e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001', 'c1a11112-e29b-41d4-a716-446655440001', 'Dinner at Marina Bay Sands', 120.00, 'SGD', '2024-10-10', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),
('650e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440001', 'c1a11112-e29b-41d4-a716-446655440001', 'Coffee at Starbucks', 6.90, 'SGD', '2024-10-08', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),
('650e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440001', 'c1a11112-e29b-41d4-a716-446655440001', 'Food delivery - Pizza Hut', 28.50, 'SGD', '2024-10-05', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),

-- Transportation expenses
('650e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440001', 'c1a11113-e29b-41d4-a716-446655440001', 'MRT fare', 2.10, 'SGD', '2024-10-13', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),
('650e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440001', 'c1a11113-e29b-41d4-a716-446655440001', 'Grab ride to airport', 35.00, 'SGD', '2024-10-09', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),
('650e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440001', 'c1a11113-e29b-41d4-a716-446655440001', 'Bus fare', 1.50, 'SGD', '2024-10-07', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),

-- Shopping expenses
('650e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-446655440001', 'c1a11114-e29b-41d4-a716-446655440001', 'New shirt at Uniqlo', 39.90, 'SGD', '2024-10-11', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),
('650e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440001', 'c1a11114-e29b-41d4-a716-446655440001', 'iPhone case', 25.00, 'SGD', '2024-10-04', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),

-- Entertainment expenses
('650e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440001', 'c1a11115-e29b-41d4-a716-446655440001', 'Movie tickets at Golden Village', 26.00, 'SGD', '2024-10-06', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),
('650e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440001', 'c1a11115-e29b-41d4-a716-446655440001', 'Netflix subscription', 17.98, 'SGD', '2024-10-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),

-- Healthcare expenses
('650e8400-e29b-41d4-a716-446655440013', '550e8400-e29b-41d4-a716-446655440001', 'c1a11116-e29b-41d4-a716-446655440001', 'Doctor consultation', 50.00, 'SGD', '2024-09-28', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),
('650e8400-e29b-41d4-a716-446655440014', '550e8400-e29b-41d4-a716-446655440001', 'c1a11116-e29b-41d4-a716-446655440001', 'Pharmacy - vitamins', 18.50, 'SGD', '2024-09-25', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),

-- Utilities expenses
('650e8400-e29b-41d4-a716-446655440015', '550e8400-e29b-41d4-a716-446655440001', 'c1a11117-e29b-41d4-a716-446655440001', 'Electricity bill', 89.50, 'SGD', '2024-10-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),
('650e8400-e29b-41d4-a716-446655440016', '550e8400-e29b-41d4-a716-446655440001', 'c1a11117-e29b-41d4-a716-446655440001', 'Internet bill - StarHub', 49.90, 'SGD', '2024-09-30', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual');

INSERT INTO expenses (id, user_id, category_id, description, amount, currency, transaction_date, created_at, updated_at, source) VALUES
('650e8400-e29b-41d4-a716-446655440020', '550e8400-e29b-41d4-a716-446655440002', 'c2a11112-e29b-41d4-a716-446655440002', 'Breakfast at Ya Kun', 7.20, 'SGD', '2024-10-13', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),
('650e8400-e29b-41d4-a716-446655440021', '550e8400-e29b-41d4-a716-446655440002', 'c2a11112-e29b-41d4-a716-446655440002', 'Groceries at Cold Storage', 62.30, 'SGD', '2024-10-11', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),
('650e8400-e29b-41d4-a716-446655440022', '550e8400-e29b-41d4-a716-446655440002', 'c2a11113-e29b-41d4-a716-446655440002', 'Taxi to office', 12.50, 'SGD', '2024-10-12', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),
('650e8400-e29b-41d4-a716-446655440023', '550e8400-e29b-41d4-a716-446655440002', 'c2a11114-e29b-41d4-a716-446655440002', 'Monthly rent', 1800.00, 'SGD', '2024-10-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),
('650e8400-e29b-41d4-a716-446655440024', '550e8400-e29b-41d4-a716-446655440002', 'c2a11115-e29b-41d4-a716-446655440002', 'Gym membership', 80.00, 'SGD', '2024-10-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual');

INSERT INTO expenses (id, user_id, category_id, description, amount, currency, transaction_date, created_at, updated_at, source) VALUES
('650e8400-e29b-41d4-a716-446655440030', '550e8400-e29b-41d4-a716-446655440003', 'c3a11112-e29b-41d4-a716-446655440003', 'Lunch at food court', 6.50, 'SGD', '2024-10-13', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),
('650e8400-e29b-41d4-a716-446655440031', '550e8400-e29b-41d4-a716-446655440003', 'c3a11113-e29b-41d4-a716-446655440003', 'Bus to town', 2.20, 'SGD', '2024-10-13', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),
('650e8400-e29b-41d4-a716-446655440032', '550e8400-e29b-41d4-a716-446655440003', 'c3a11114-e29b-41d4-a716-446655440003', 'Plants for balcony', 45.00, 'SGD', '2024-10-10', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),
('650e8400-e29b-41d4-a716-446655440033', '550e8400-e29b-41d4-a716-446655440003', 'c3a11115-e29b-41d4-a716-446655440003', 'Cat food', 28.90, 'SGD', '2024-10-09', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual');

INSERT INTO expenses (id, user_id, category_id, description, amount, currency, transaction_date, created_at, updated_at, source) VALUES
('650e8400-e29b-41d4-a716-446655440040', '550e8400-e29b-41d4-a716-446655440004', 'c4a11112-e29b-41d4-a716-446655440004', 'Dinner at Lau Pa Sat', 15.80, 'SGD', '2024-10-13', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),
('650e8400-e29b-41d4-a716-446655440041', '550e8400-e29b-41d4-a716-446655440004', 'c4a11113-e29b-41d4-a716-446655440004', 'MRT top-up', 20.00, 'SGD', '2024-10-12', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),
('650e8400-e29b-41d4-a716-446655440042', '550e8400-e29b-41d4-a716-446655440004', 'c4a11114-e29b-41d4-a716-446655440004', 'Adobe Creative Suite', 29.99, 'USD', '2024-10-11', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual'),
('650e8400-e29b-41d4-a716-446655440043', '550e8400-e29b-41d4-a716-446655440004', 'c4a11115-e29b-41d4-a716-446655440004', 'Tennis racket strings', 35.00, 'SGD', '2024-10-08', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'manual');

-- =====================================================
-- 4. INSERT SAMPLE FILES
-- =====================================================

-- NOTE: File records are commented out because actual files don't exist yet.
-- You can upload files using the /api/v1/files/upload endpoint once your app is running.
-- When you upload files, they'll automatically create database records.

/*
-- Uncomment these INSERT statements only after you have actual files uploaded:

INSERT INTO files (id, user_id, expense_id, file_name, file_type, file_path, purpose, uploaded_at) VALUES
-- Receipt images for John Doe's expenses
('750e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440001', 'hawker_receipt_20241013.jpg', 'image/jpeg', '/uploads/john_doe/receipts/hawker_receipt_20241013.jpg', 'receipt', CURRENT_TIMESTAMP),
('750e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440002', 'ntuc_receipt_20241012.jpg', 'image/jpeg', '/uploads/john_doe/receipts/ntuc_receipt_20241012.jpg', 'receipt', CURRENT_TIMESTAMP);
*/

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Uncomment these queries to verify the data insertion

/*
-- Check users count
SELECT 'Users' as table_name, COUNT(*) as record_count FROM users;

-- Check categories count by user
SELECT 
    'Categories' as table_name, 
    u.display_name, 
    COUNT(c.id) as category_count 
FROM users u 
LEFT JOIN categories c ON u.id = c.user_id 
GROUP BY u.id, u.display_name;

-- Check expenses count by user
SELECT 
    'Expenses' as table_name,
    u.display_name, 
    COUNT(e.id) as expense_count,
    SUM(e.amount) as total_amount
FROM users u 
LEFT JOIN expenses e ON u.id = e.user_id 
GROUP BY u.id, u.display_name;

-- Check files count by user
SELECT 
    'Files' as table_name,
    u.display_name, 
    COUNT(f.id) as file_count 
FROM users u 
LEFT JOIN files f ON u.id = f.user_id 
GROUP BY u.id, u.display_name;

-- Recent expenses by user
SELECT 
    u.display_name,
    c.name as category,
    e.description,
    e.amount,
    e.currency,
    e.transaction_date
FROM expenses e
JOIN users u ON e.user_id = u.id
LEFT JOIN categories c ON e.category_id = c.id
ORDER BY e.transaction_date DESC, u.display_name
LIMIT 20;
*/

-- =====================================================
-- SUCCESS MESSAGE
-- =====================================================

SELECT 'Database population completed successfully!' as status;