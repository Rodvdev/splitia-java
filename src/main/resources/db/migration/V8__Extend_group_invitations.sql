-- Extend group_invitations with targeted invite fields and status tracking
-- This migration assumes PostgreSQL

-- Add new columns
ALTER TABLE group_invitations
    ADD COLUMN IF NOT EXISTS email TEXT,
    ADD COLUMN IF NOT EXISTS invited_user_id UUID REFERENCES users(id),
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'PENDING';

-- Indexes to support lookups and prevent duplicate pending invites
CREATE INDEX IF NOT EXISTS idx_group_invitations_status ON group_invitations(status);

-- Unique partial index to prevent duplicate pending invites by email for the same group
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes WHERE indexname = 'uq_group_invites_pending_email'
    ) THEN
        EXECUTE 'CREATE UNIQUE INDEX uq_group_invites_pending_email
                 ON group_invitations (group_id, lower(email))
                 WHERE deleted_at IS NULL AND status = ''PENDING'' AND email IS NOT NULL';
    END IF;
END$$;

-- Unique partial index to prevent duplicate pending invites by user for the same group
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes WHERE indexname = 'uq_group_invites_pending_user'
    ) THEN
        EXECUTE 'CREATE UNIQUE INDEX uq_group_invites_pending_user
                 ON group_invitations (group_id, invited_user_id)
                 WHERE deleted_at IS NULL AND status = ''PENDING'' AND invited_user_id IS NOT NULL';
    END IF;
END$$;

-- Helpful index for invited_user lookups
CREATE INDEX IF NOT EXISTS idx_group_invitations_invited_user ON group_invitations(invited_user_id);
