-- Splitia Database Schema Migration V14
-- Create Procurement module tables (Vendors, Purchase Orders, Contracts)

-- Vendors table
CREATE TABLE IF NOT EXISTS vendors (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    contact_name VARCHAR(255),
    email VARCHAR(255),
    phone_number VARCHAR(50),
    tax_id VARCHAR(50),
    address TEXT,
    city VARCHAR(100),
    country VARCHAR(100),
    payment_terms VARCHAR(20),
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    notes TEXT,
    created_by_id UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_vendors_name ON vendors(name);
CREATE INDEX IF NOT EXISTS idx_vendors_email ON vendors(email);
CREATE INDEX IF NOT EXISTS idx_vendors_deleted ON vendors(deleted_at) WHERE deleted_at IS NULL;

-- Purchase Orders table
CREATE TABLE IF NOT EXISTS purchase_orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_number VARCHAR(50) NOT NULL UNIQUE,
    vendor_id UUID NOT NULL REFERENCES vendors(id) ON DELETE CASCADE,
    order_date DATE NOT NULL,
    expected_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    total DECIMAL(19, 2) NOT NULL DEFAULT 0,
    created_by_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_purchase_orders_number ON purchase_orders(order_number);
CREATE INDEX IF NOT EXISTS idx_purchase_orders_vendor ON purchase_orders(vendor_id);
CREATE INDEX IF NOT EXISTS idx_purchase_orders_status ON purchase_orders(status);
CREATE INDEX IF NOT EXISTS idx_purchase_orders_date ON purchase_orders(order_date);
CREATE INDEX IF NOT EXISTS idx_purchase_orders_deleted ON purchase_orders(deleted_at) WHERE deleted_at IS NULL;

-- Purchase Order Items table
CREATE TABLE IF NOT EXISTS purchase_order_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    purchase_order_id UUID NOT NULL REFERENCES purchase_orders(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(19, 2) NOT NULL,
    total DECIMAL(19, 2) NOT NULL,
    received_quantity INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_purchase_order_items_order ON purchase_order_items(purchase_order_id);
CREATE INDEX IF NOT EXISTS idx_purchase_order_items_product ON purchase_order_items(product_id);
CREATE INDEX IF NOT EXISTS idx_purchase_order_items_deleted ON purchase_order_items(deleted_at) WHERE deleted_at IS NULL;

-- Vendor Contracts table
CREATE TABLE IF NOT EXISTS vendor_contracts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    vendor_id UUID NOT NULL REFERENCES vendors(id) ON DELETE CASCADE,
    contract_number VARCHAR(50) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE,
    terms TEXT,
    document_path VARCHAR(500),
    created_by_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_vendor_contracts_vendor ON vendor_contracts(vendor_id);
CREATE INDEX IF NOT EXISTS idx_vendor_contracts_number ON vendor_contracts(contract_number);
CREATE INDEX IF NOT EXISTS idx_vendor_contracts_dates ON vendor_contracts(start_date, end_date);
CREATE INDEX IF NOT EXISTS idx_vendor_contracts_deleted ON vendor_contracts(deleted_at) WHERE deleted_at IS NULL;

-- Function to generate purchase order number
CREATE OR REPLACE FUNCTION generate_purchase_order_number() RETURNS TRIGGER AS $$
DECLARE
    new_number VARCHAR(50);
    year_part VARCHAR(4);
    seq_num INTEGER;
BEGIN
    year_part := TO_CHAR(NEW.order_date, 'YYYY');
    
    -- Get next sequence number for this year
    SELECT COALESCE(MAX(CAST(SUBSTRING(order_number FROM '[0-9]+$') AS INTEGER)), 0) + 1
    INTO seq_num
    FROM purchase_orders
    WHERE order_number LIKE 'PO-' || year_part || '-%'
      AND deleted_at IS NULL;
    
    new_number := 'PO-' || year_part || '-' || LPAD(seq_num::TEXT, 6, '0');
    NEW.order_number := new_number;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to auto-generate purchase order number if not provided
CREATE TRIGGER trigger_generate_purchase_order_number
    BEFORE INSERT ON purchase_orders
    FOR EACH ROW
    WHEN (NEW.order_number IS NULL OR NEW.order_number = '')
    EXECUTE FUNCTION generate_purchase_order_number();

-- Function to generate contract number
CREATE OR REPLACE FUNCTION generate_contract_number() RETURNS TRIGGER AS $$
DECLARE
    new_number VARCHAR(50);
    year_part VARCHAR(4);
    seq_num INTEGER;
BEGIN
    year_part := TO_CHAR(NEW.start_date, 'YYYY');
    
    -- Get next sequence number for this year
    SELECT COALESCE(MAX(CAST(SUBSTRING(contract_number FROM '[0-9]+$') AS INTEGER)), 0) + 1
    INTO seq_num
    FROM vendor_contracts
    WHERE contract_number LIKE 'CONTRACT-' || year_part || '-%'
      AND deleted_at IS NULL;
    
    new_number := 'CONTRACT-' || year_part || '-' || LPAD(seq_num::TEXT, 6, '0');
    NEW.contract_number := new_number;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to auto-generate contract number if not provided
CREATE TRIGGER trigger_generate_contract_number
    BEFORE INSERT ON vendor_contracts
    FOR EACH ROW
    WHEN (NEW.contract_number IS NULL OR NEW.contract_number = '')
    EXECUTE FUNCTION generate_contract_number();

