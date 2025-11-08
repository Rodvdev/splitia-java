-- Add group association to custom_categories
-- This migration assumes PostgreSQL

-- Step 1: Add group_id column (nullable initially to allow data migration)
ALTER TABLE custom_categories
    ADD COLUMN IF NOT EXISTS group_id UUID REFERENCES groups(id) ON DELETE CASCADE;

-- Step 2: Migrate existing categories to groups
-- For each category, assign it to the first group where the user is a member
-- If user has no groups, assign to a default group or mark for deletion
DO $$
DECLARE
    category_record RECORD;
    group_id_found UUID;
BEGIN
    FOR category_record IN 
        SELECT cc.id, cc.user_id 
        FROM custom_categories cc 
        WHERE cc.group_id IS NULL AND cc.deleted_at IS NULL
    LOOP
        -- Find the first group where the user is a member
        SELECT gu.group_id INTO group_id_found
        FROM group_users gu
        WHERE gu.user_id = category_record.user_id 
          AND gu.deleted_at IS NULL
        LIMIT 1;
        
        -- If user has a group, assign category to it
        IF group_id_found IS NOT NULL THEN
            UPDATE custom_categories
            SET group_id = group_id_found
            WHERE id = category_record.id;
        ELSE
            -- If user has no groups, soft delete the category
            UPDATE custom_categories
            SET deleted_at = CURRENT_TIMESTAMP
            WHERE id = category_record.id;
        END IF;
    END LOOP;
END$$;

-- Step 3: Ensure all non-deleted categories have a group_id
-- For any remaining categories without group_id, assign them to a default group or soft-delete them
DO $$
DECLARE
    category_record RECORD;
    group_id_found UUID;
    default_group_id UUID;
BEGIN
    -- Try to find or create a default group
    SELECT id INTO default_group_id FROM groups WHERE name = 'Default' LIMIT 1;
    
    -- If no default group exists, create one (or use first available group)
    IF default_group_id IS NULL THEN
        SELECT id INTO default_group_id FROM groups LIMIT 1;
    END IF;
    
    -- Process any remaining categories without group_id
    FOR category_record IN 
        SELECT cc.id, cc.user_id 
        FROM custom_categories cc 
        WHERE cc.group_id IS NULL AND cc.deleted_at IS NULL
    LOOP
        -- Try to find a group for the user
        SELECT gu.group_id INTO group_id_found
        FROM group_users gu
        WHERE gu.user_id = category_record.user_id 
          AND gu.deleted_at IS NULL
        LIMIT 1;
        
        -- If user has a group, assign category to it
        IF group_id_found IS NOT NULL THEN
            UPDATE custom_categories
            SET group_id = group_id_found
            WHERE id = category_record.id;
        ELSIF default_group_id IS NOT NULL THEN
            -- Assign to default group if available
            UPDATE custom_categories
            SET group_id = default_group_id
            WHERE id = category_record.id;
        ELSE
            -- Last resort: soft delete the category
            UPDATE custom_categories
            SET deleted_at = CURRENT_TIMESTAMP
            WHERE id = category_record.id;
        END IF;
    END LOOP;
END$$;

-- Step 3b: Note: NOT NULL constraint will be applied in a later migration
-- once all data has been properly migrated. For now, group_id remains nullable
-- to allow the migration to complete successfully.

-- Step 4: Drop the old unique constraint (user_id, name)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'custom_categories_user_id_name_key'
    ) THEN
        ALTER TABLE custom_categories 
        DROP CONSTRAINT custom_categories_user_id_name_key;
    END IF;
END$$;

-- Step 5: Create new unique constraint (group_id, name)
-- This allows same category name in different groups
CREATE UNIQUE INDEX IF NOT EXISTS uq_custom_categories_group_name 
    ON custom_categories (group_id, name) 
    WHERE deleted_at IS NULL;

-- Step 6: Add index for group_id lookups
CREATE INDEX IF NOT EXISTS idx_custom_categories_group ON custom_categories(group_id);

-- Step 7: Rename user_id column to created_by_id for consistency (optional but recommended)
-- First check if created_by_id already exists
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'custom_categories' 
        AND column_name = 'created_by_id'
    ) THEN
        ALTER TABLE custom_categories 
        RENAME COLUMN user_id TO created_by_id;
    END IF;
END$$;

-- Step 8: Update index name if it exists
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_indexes 
        WHERE indexname = 'idx_categories_user'
    ) THEN
        ALTER INDEX idx_categories_user RENAME TO idx_custom_categories_created_by;
    END IF;
END$$;

-- Step 9: Create index for created_by_id if it doesn't exist
CREATE INDEX IF NOT EXISTS idx_custom_categories_created_by ON custom_categories(created_by_id);

