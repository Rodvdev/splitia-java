-- Splitia Database Schema Migration V23
-- Add stock_id to stock_movements and update relationships

-- Step 1: Add stock_id column (nullable initially)
ALTER TABLE stock_movements
    ADD COLUMN IF NOT EXISTS stock_id UUID REFERENCES stock(id) ON DELETE CASCADE;

-- Step 2: Migrate existing data - assign stock_id based on product_id
UPDATE stock_movements sm
SET stock_id = s.id
FROM stock s
WHERE sm.product_id = s.product_id
  AND sm.stock_id IS NULL
  AND s.deleted_at IS NULL;

-- Step 3: For any movements without stock, create stock records or soft-delete movements
DO $$
DECLARE
    movement_record RECORD;
    stock_record RECORD;
BEGIN
    FOR movement_record IN 
        SELECT sm.id, sm.product_id
        FROM stock_movements sm
        WHERE sm.stock_id IS NULL AND sm.deleted_at IS NULL
    LOOP
        -- Try to find or create stock for this product
        SELECT id INTO stock_record
        FROM stock
        WHERE product_id = movement_record.product_id
          AND deleted_at IS NULL
        LIMIT 1;
        
        IF stock_record IS NULL THEN
            -- Create stock record if it doesn't exist
            INSERT INTO stock (product_id, quantity, min_quantity, created_at)
            VALUES (movement_record.product_id, 0, 0, CURRENT_TIMESTAMP)
            RETURNING id INTO stock_record;
        END IF;
        
        -- Update movement with stock_id
        UPDATE stock_movements
        SET stock_id = stock_record.id
        WHERE id = movement_record.id;
    END LOOP;
END$$;

-- Step 4: Make stock_id NOT NULL (after data migration)
ALTER TABLE stock_movements
    ALTER COLUMN stock_id SET NOT NULL;

-- Step 5: Update trigger to use stock_id
DROP TRIGGER IF EXISTS trigger_update_stock_on_movement ON stock_movements;

CREATE OR REPLACE FUNCTION update_stock_on_movement() RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE stock
        SET quantity = CASE
            WHEN NEW.type = 'IN' OR NEW.type = 'RETURN' THEN quantity + NEW.quantity
            WHEN NEW.type = 'OUT' THEN quantity - NEW.quantity
            WHEN NEW.type = 'ADJUSTMENT' THEN NEW.quantity
            ELSE quantity
        END
        WHERE id = NEW.stock_id AND deleted_at IS NULL;
        
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_stock_on_movement
    AFTER INSERT ON stock_movements
    FOR EACH ROW
    EXECUTE FUNCTION update_stock_on_movement();

-- Step 6: Add index for stock_id
CREATE INDEX IF NOT EXISTS idx_stock_movements_stock ON stock_movements(stock_id);

-- Step 7: Keep product_id for backward compatibility (can be removed later if not needed)
-- Note: product_id remains in the table but is now redundant since stock already has product_id

