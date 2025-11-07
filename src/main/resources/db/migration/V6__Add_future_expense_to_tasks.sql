-- Splitia Database Schema Migration V6
-- Add future expense fields to tasks table

-- Add expense_id column to reference existing expense
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS expense_id UUID REFERENCES expenses(id) ON DELETE SET NULL;

-- Add future expense fields
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS future_expense_amount DECIMAL(19,2);
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS future_expense_currency VARCHAR(10) DEFAULT 'USD';
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS future_expense_paid_by_id UUID REFERENCES users(id) ON DELETE SET NULL;
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS future_expense_shares JSONB;

-- Create index on expense_id
CREATE INDEX IF NOT EXISTS idx_tasks_expense ON tasks(expense_id);

-- Create index on future_expense_paid_by_id
CREATE INDEX IF NOT EXISTS idx_tasks_future_expense_paid_by ON tasks(future_expense_paid_by_id);

