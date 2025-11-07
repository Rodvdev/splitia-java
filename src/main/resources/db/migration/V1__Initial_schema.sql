-- Splitia Database Schema Migration V1
-- Initial schema creation

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50),
    image VARCHAR(500),
    currency VARCHAR(10) NOT NULL DEFAULT 'PEN',
    language VARCHAR(10) NOT NULL DEFAULT 'es',
    external_id VARCHAR(255),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_external_id ON users(external_id);
CREATE INDEX idx_users_role ON users(role);

-- Groups table
CREATE TABLE groups (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image VARCHAR(500),
    created_by_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_groups_created_by ON groups(created_by_id);

-- Group Users (members) table
CREATE TABLE group_users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    group_id UUID NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(user_id, group_id)
);

CREATE INDEX idx_group_users_user ON group_users(user_id);
CREATE INDEX idx_group_users_group ON group_users(group_id);
CREATE INDEX idx_group_users_role ON group_users(role);

-- Custom Categories table
CREATE TABLE custom_categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    icon VARCHAR(50),
    color VARCHAR(20),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(user_id, name)
);

CREATE INDEX idx_categories_user ON custom_categories(user_id);

-- Expenses table
CREATE TABLE expenses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    amount DECIMAL(19, 2) NOT NULL,
    description VARCHAR(255) NOT NULL,
    date TIMESTAMP NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    location VARCHAR(255),
    notes TEXT,
    is_settlement BOOLEAN NOT NULL DEFAULT FALSE,
    paid_by_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    group_id UUID REFERENCES groups(id) ON DELETE SET NULL,
    category_id UUID REFERENCES custom_categories(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_expenses_paid_by ON expenses(paid_by_id);
CREATE INDEX idx_expenses_group ON expenses(group_id);
CREATE INDEX idx_expenses_category ON expenses(category_id);
CREATE INDEX idx_expenses_date ON expenses(date);

-- Expense Shares table
CREATE TABLE expense_shares (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    amount DECIMAL(19, 2) NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'EQUAL',
    expense_id UUID NOT NULL REFERENCES expenses(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(expense_id, user_id)
);

CREATE INDEX idx_expense_shares_expense ON expense_shares(expense_id);
CREATE INDEX idx_expense_shares_user ON expense_shares(user_id);

-- Budgets table
CREATE TABLE budgets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    amount DECIMAL(19, 2) NOT NULL,
    month INTEGER NOT NULL CHECK (month >= 1 AND month <= 12),
    year INTEGER NOT NULL CHECK (year >= 2000),
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id UUID REFERENCES custom_categories(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(user_id, month, year, category_id)
);

CREATE INDEX idx_budgets_user ON budgets(user_id);
CREATE INDEX idx_budgets_month_year ON budgets(month, year);
CREATE INDEX idx_budgets_category ON budgets(category_id);

-- Conversations table
CREATE TABLE conversations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    is_group_chat BOOLEAN NOT NULL DEFAULT FALSE,
    name VARCHAR(100),
    group_id UUID REFERENCES groups(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_conversations_group ON conversations(group_id);

-- Conversation Participants table
CREATE TABLE conversation_participants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(conversation_id, user_id)
);

CREATE INDEX idx_conversation_participants_conversation ON conversation_participants(conversation_id);
CREATE INDEX idx_conversation_participants_user ON conversation_participants(user_id);

-- Messages table
CREATE TABLE messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    content TEXT NOT NULL,
    is_ai BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    updated_at TIMESTAMP
);

CREATE INDEX idx_messages_conversation ON messages(conversation_id);
CREATE INDEX idx_messages_sender ON messages(sender_id);
CREATE INDEX idx_messages_created_at ON messages(created_at);

-- Message Seen table
CREATE TABLE message_seen (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    message_id UUID NOT NULL REFERENCES messages(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    seen_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(message_id, user_id)
);

CREATE INDEX idx_message_seen_message ON message_seen(message_id);
CREATE INDEX idx_message_seen_user ON message_seen(user_id);

-- Settlements table
CREATE TABLE settlements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    amount DECIMAL(19, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    description TEXT,
    date TIMESTAMP NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    type VARCHAR(20) NOT NULL DEFAULT 'PAYMENT',
    initiated_by_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    settled_with_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    group_id UUID NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    expense_id UUID REFERENCES expenses(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_settlements_group ON settlements(group_id);
CREATE INDEX idx_settlements_initiated_by ON settlements(initiated_by_id);
CREATE INDEX idx_settlements_settled_with ON settlements(settled_with_user_id);
CREATE INDEX idx_settlements_status ON settlements(status);

-- Subscriptions table
CREATE TABLE subscriptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    plan_type VARCHAR(20) NOT NULL DEFAULT 'FREE',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    auto_renew BOOLEAN NOT NULL DEFAULT FALSE,
    payment_method VARCHAR(50),
    stripe_subscription_id VARCHAR(255),
    stripe_customer_id VARCHAR(255),
    price_per_month DECIMAL(19, 2),
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_subscriptions_user ON subscriptions(user_id);
CREATE INDEX idx_subscriptions_status ON subscriptions(status);
CREATE INDEX idx_subscriptions_stripe_subscription ON subscriptions(stripe_subscription_id);

-- Subscription Payments table
CREATE TABLE subscription_payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    amount DECIMAL(19, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_date TIMESTAMP NOT NULL,
    stripe_payment_intent_id VARCHAR(255),
    invoice_url VARCHAR(500),
    subscription_id UUID NOT NULL REFERENCES subscriptions(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_subscription_payments_subscription ON subscription_payments(subscription_id);
CREATE INDEX idx_subscription_payments_status ON subscription_payments(status);

-- Support Tickets table
CREATE TABLE support_tickets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(30) NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    resolution TEXT,
    resolved_at TIMESTAMP,
    created_by_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    assigned_to_id UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_support_tickets_created_by ON support_tickets(created_by_id);
CREATE INDEX idx_support_tickets_assigned_to ON support_tickets(assigned_to_id);
CREATE INDEX idx_support_tickets_status ON support_tickets(status);
CREATE INDEX idx_support_tickets_category ON support_tickets(category);

-- Support Messages table
CREATE TABLE support_messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    content TEXT NOT NULL,
    is_from_support BOOLEAN NOT NULL DEFAULT FALSE,
    ticket_id UUID NOT NULL REFERENCES support_tickets(id) ON DELETE CASCADE,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_support_messages_ticket ON support_messages(ticket_id);
CREATE INDEX idx_support_messages_user ON support_messages(user_id);

-- Support Attachments table
CREATE TABLE support_attachments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_size BIGINT,
    mime_type VARCHAR(100),
    ticket_id UUID NOT NULL REFERENCES support_tickets(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_support_attachments_ticket ON support_attachments(ticket_id);

-- Group Invitations table
CREATE TABLE group_invitations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    token VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMP,
    max_uses INTEGER,
    current_uses INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    group_id UUID NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    created_by_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_group_invitations_token ON group_invitations(token);
CREATE INDEX idx_group_invitations_group ON group_invitations(group_id);

-- Create trigger function for updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at on all tables
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_groups_updated_at BEFORE UPDATE ON groups FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_group_users_updated_at BEFORE UPDATE ON group_users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_custom_categories_updated_at BEFORE UPDATE ON custom_categories FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_expenses_updated_at BEFORE UPDATE ON expenses FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_expense_shares_updated_at BEFORE UPDATE ON expense_shares FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_budgets_updated_at BEFORE UPDATE ON budgets FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_conversations_updated_at BEFORE UPDATE ON conversations FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_conversation_participants_updated_at BEFORE UPDATE ON conversation_participants FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_messages_updated_at BEFORE UPDATE ON messages FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_message_seen_updated_at BEFORE UPDATE ON message_seen FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_settlements_updated_at BEFORE UPDATE ON settlements FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_subscriptions_updated_at BEFORE UPDATE ON subscriptions FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_subscription_payments_updated_at BEFORE UPDATE ON subscription_payments FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_support_tickets_updated_at BEFORE UPDATE ON support_tickets FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_support_messages_updated_at BEFORE UPDATE ON support_messages FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_support_attachments_updated_at BEFORE UPDATE ON support_attachments FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_group_invitations_updated_at BEFORE UPDATE ON group_invitations FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Seed demo users
-- Password: demo123 (BCrypt hash with cost factor 10)
-- Demo User (ROLE_USER)
INSERT INTO users (id, name, last_name, email, password, currency, language, role, created_at)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'Demo',
    'User',
    'user@demo.com',
    '$2a$10$5sN7WJSxXLI9tIa9BmaUyeYiYyPoe.5a7yA.2/nuig.h7X1HY2dYy', -- demo123
    'PEN',
    'es',
    'USER',
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO UPDATE SET 
    password = EXCLUDED.password,
    role = EXCLUDED.role;

-- Demo Admin (ROLE_ADMIN)
INSERT INTO users (id, name, last_name, email, password, currency, language, role, created_at)
VALUES (
    '00000000-0000-0000-0000-000000000002',
    'Demo',
    'Admin',
    'admin@demo.com',
    '$2a$10$5sN7WJSxXLI9tIa9BmaUyeYiYyPoe.5a7yA.2/nuig.h7X1HY2dYy', -- demo123
    'PEN',
    'es',
    'ADMIN',
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO UPDATE SET 
    password = EXCLUDED.password,
    role = EXCLUDED.role;

