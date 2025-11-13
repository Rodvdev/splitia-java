-- Splitia Database Schema Migration V24
-- Add user preference fields: theme, notifications, date/time formats

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS theme VARCHAR(10) NOT NULL DEFAULT 'light',
    ADD COLUMN IF NOT EXISTS notifications_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS date_format VARCHAR(20) NOT NULL DEFAULT 'DD/MM/YYYY',
    ADD COLUMN IF NOT EXISTS time_format VARCHAR(10) NOT NULL DEFAULT '24H';

-- Create indexes if useful for querying preferences (optional)
-- No indexes added; preferences are per-row fields typically not filtered by