-- Splitia Database Schema Migration V3
-- Add Plans table and update subscriptions to reference plans

-- Create Plans table
CREATE TABLE IF NOT EXISTS plans (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    price_per_month DECIMAL(19, 2) NOT NULL DEFAULT 0,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    max_groups INTEGER NOT NULL DEFAULT 1,
    max_group_members INTEGER NOT NULL DEFAULT 5,
    max_ai_requests_per_month INTEGER NOT NULL DEFAULT 0,
    max_expenses_per_group INTEGER NOT NULL DEFAULT 50,
    max_budgets_per_group INTEGER NOT NULL DEFAULT 5,
    has_kanban BOOLEAN NOT NULL DEFAULT FALSE,
    has_advanced_analytics BOOLEAN NOT NULL DEFAULT FALSE,
    has_custom_categories BOOLEAN NOT NULL DEFAULT TRUE,
    has_export_data BOOLEAN NOT NULL DEFAULT FALSE,
    has_priority_support BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_plans_name ON plans(name);
CREATE INDEX IF NOT EXISTS idx_plans_is_active ON plans(is_active);
CREATE INDEX IF NOT EXISTS idx_plans_deleted_at ON plans(deleted_at) WHERE deleted_at IS NULL;

-- Add plan_id column to subscriptions table
ALTER TABLE subscriptions ADD COLUMN IF NOT EXISTS plan_id UUID REFERENCES plans(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_subscriptions_plan ON subscriptions(plan_id);

