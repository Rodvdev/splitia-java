-- Splitia Database Schema Migration V2
-- Add soft delete support and granular permissions

-- Add deleted_at column to all main tables
ALTER TABLE users ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE groups ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE group_users ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE custom_categories ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE expenses ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE expense_shares ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE budgets ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE conversations ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE conversation_participants ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE messages ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE message_seen ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE settlements ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE subscriptions ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE subscription_payments ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE support_tickets ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE support_messages ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE support_attachments ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE group_invitations ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;

-- Add permissions JSONB column to group_users for granular permissions
ALTER TABLE group_users ADD COLUMN IF NOT EXISTS permissions JSONB DEFAULT '{}'::jsonb;

-- Create indexes for soft delete queries (to improve performance when filtering deleted records)
CREATE INDEX IF NOT EXISTS idx_users_deleted_at ON users(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_groups_deleted_at ON groups(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_group_users_deleted_at ON group_users(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_custom_categories_deleted_at ON custom_categories(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_expenses_deleted_at ON expenses(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_expense_shares_deleted_at ON expense_shares(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_budgets_deleted_at ON budgets(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_conversations_deleted_at ON conversations(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_conversation_participants_deleted_at ON conversation_participants(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_messages_deleted_at ON messages(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_message_seen_deleted_at ON message_seen(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_settlements_deleted_at ON settlements(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_subscriptions_deleted_at ON subscriptions(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_subscription_payments_deleted_at ON subscription_payments(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_support_tickets_deleted_at ON support_tickets(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_support_messages_deleted_at ON support_messages(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_support_attachments_deleted_at ON support_attachments(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_group_invitations_deleted_at ON group_invitations(deleted_at) WHERE deleted_at IS NULL;

-- Create GIN index for efficient JSONB permissions queries
CREATE INDEX IF NOT EXISTS idx_group_users_permissions_gin ON group_users USING GIN (permissions);

