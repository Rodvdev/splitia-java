-- Splitia Database Schema Migration V5
-- Seed Plans and PRO users with shared group

-- Insert Plans
INSERT INTO plans (id, name, description, price_per_month, currency, max_groups, max_group_members, max_ai_requests_per_month, max_expenses_per_group, max_budgets_per_group, has_kanban, has_advanced_analytics, has_custom_categories, has_export_data, has_priority_support, is_active, created_at)
VALUES 
    (
        '10000000-0000-0000-0000-000000000001',
        'FREE',
        'Plan gratuito con funcionalidades básicas',
        0.00,
        'USD',
        1,
        5,
        10,
        50,
        3,
        FALSE,
        FALSE,
        TRUE,
        FALSE,
        FALSE,
        TRUE,
        CURRENT_TIMESTAMP
    ),
    (
        '10000000-0000-0000-0000-000000000002',
        'PRO',
        'Plan profesional con funcionalidades avanzadas',
        9.99,
        'USD',
        10,
        50,
        500,
        1000,
        50,
        TRUE,
        TRUE,
        TRUE,
        TRUE,
        FALSE,
        TRUE,
        CURRENT_TIMESTAMP
    ),
    (
        '10000000-0000-0000-0000-000000000003',
        'ENTERPRISE',
        'Plan empresarial con funcionalidades ilimitadas y soporte prioritario',
        29.99,
        'USD',
        -1, -- -1 means unlimited
        -1, -- -1 means unlimited
        -1, -- -1 means unlimited
        -1, -- -1 means unlimited
        -1, -- -1 means unlimited
        TRUE,
        TRUE,
        TRUE,
        TRUE,
        TRUE,
        TRUE,
        CURRENT_TIMESTAMP
    )
ON CONFLICT DO NOTHING;

-- Get PRO plan ID (we'll use a subquery)
DO $$
DECLARE
    pro_plan_id UUID := '10000000-0000-0000-0000-000000000002';
    rodrigo_id UUID := '20000000-0000-0000-0000-000000000001';
    luis_id UUID := '20000000-0000-0000-0000-000000000002';
    israel_id UUID := '20000000-0000-0000-0000-000000000003';
    grupo_id UUID := '30000000-0000-0000-0000-000000000001';
BEGIN
    -- Insert PRO users
    -- Password: splitia123 (BCrypt hash)
    INSERT INTO users (id, name, last_name, email, password, currency, language, role, created_at)
    VALUES 
        (
            rodrigo_id,
            'Rodrigo',
            'Splitia',
            'rodrigo@splitia.com',
            '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- splitia123
            'USD',
            'es',
            'USER',
            CURRENT_TIMESTAMP
        ),
        (
            luis_id,
            'Luis',
            'Splitia',
            'luis@splitia.com',
            '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- splitia123
            'USD',
            'es',
            'USER',
            CURRENT_TIMESTAMP
        ),
        (
            israel_id,
            'Israel',
            'Splitia',
            'israel@splitia.com',
            '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- splitia123
            'USD',
            'es',
            'USER',
            CURRENT_TIMESTAMP
        )
    ON CONFLICT (email) DO UPDATE SET 
        password = EXCLUDED.password,
        name = EXCLUDED.name,
        last_name = EXCLUDED.last_name;

    -- Create shared group "Diseño de Software"
    INSERT INTO groups (id, name, description, created_by_id, created_at)
    VALUES (
        grupo_id,
        'Diseño de Software',
        'Grupo compartido para el diseño y desarrollo de software',
        rodrigo_id,
        CURRENT_TIMESTAMP
    )
    ON CONFLICT DO NOTHING;

    -- Add all three users to the group
    -- Rodrigo as ADMIN
    INSERT INTO group_users (id, user_id, group_id, role, created_at)
    VALUES (
        '40000000-0000-0000-0000-000000000001',
        rodrigo_id,
        grupo_id,
        'ADMIN',
        CURRENT_TIMESTAMP
    )
    ON CONFLICT DO NOTHING;

    -- Luis as MEMBER
    INSERT INTO group_users (id, user_id, group_id, role, created_at)
    VALUES (
        '40000000-0000-0000-0000-000000000002',
        luis_id,
        grupo_id,
        'MEMBER',
        CURRENT_TIMESTAMP
    )
    ON CONFLICT DO NOTHING;

    -- Israel as MEMBER
    INSERT INTO group_users (id, user_id, group_id, role, created_at)
    VALUES (
        '40000000-0000-0000-0000-000000000003',
        israel_id,
        grupo_id,
        'MEMBER',
        CURRENT_TIMESTAMP
    )
    ON CONFLICT DO NOTHING;

    -- Create PRO subscriptions for all three users
    INSERT INTO subscriptions (id, plan_type, plan_id, status, start_date, end_date, auto_renew, price_per_month, currency, user_id, created_at)
    VALUES 
        (
            '50000000-0000-0000-0000-000000000001',
            'PRO',
            pro_plan_id,
            'ACTIVE',
            CURRENT_TIMESTAMP,
            CURRENT_TIMESTAMP + INTERVAL '1 year',
            TRUE,
            9.99,
            'USD',
            rodrigo_id,
            CURRENT_TIMESTAMP
        ),
        (
            '50000000-0000-0000-0000-000000000002',
            'PRO',
            pro_plan_id,
            'ACTIVE',
            CURRENT_TIMESTAMP,
            CURRENT_TIMESTAMP + INTERVAL '1 year',
            TRUE,
            9.99,
            'USD',
            luis_id,
            CURRENT_TIMESTAMP
        ),
        (
            '50000000-0000-0000-0000-000000000003',
            'PRO',
            pro_plan_id,
            'ACTIVE',
            CURRENT_TIMESTAMP,
            CURRENT_TIMESTAMP + INTERVAL '1 year',
            TRUE,
            9.99,
            'USD',
            israel_id,
            CURRENT_TIMESTAMP
        )
    ON CONFLICT DO NOTHING;
END $$;

