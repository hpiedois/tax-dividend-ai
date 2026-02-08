-- ============================================================================
-- Seed Data for Development Environment
-- User: John Doe (matches Keycloak realm-export.json)
-- ============================================================================

-- 0. Cleanup existing data (Cascades to dividends, statements, forms)
DELETE FROM users WHERE email = 'jdoe@dummy.com';

-- 1. Insert User
INSERT INTO users (
    id,
    email,
    password_hash, -- Placeholder, auth handled by Keycloak
    full_name,
    tax_id,
    canton,
    country,
    registration_source,
    status,
    is_active,
    is_verified
)
VALUES (
    '4e012f70-3846-4941-b95f-5b98c70a235d',
    'jdoe@dummy.com',
    'keycloak_managed',
    'John Doe',
    '756.1234.5678.90',
    'VD',
    'CH',
    'CLASSIC',  -- Classic registration for test user
    'ACTIVE',
    true,
    true
);

-- Get User ID for subsequent inserts
DO $$
DECLARE
    v_user_id UUID;
    v_stmt_id UUID;
    v_form_id UUID;
BEGIN
    v_user_id := '4e012f70-3846-4941-b95f-5b98c70a235d';

    -- 2. Insert Dividend Statement (Interactive Brokers)
    INSERT INTO dividend_statements (
        id,
        user_id,
        source_file_name,
        source_file_s3_key,
        broker,
        status,
        parsed_at,
        dividend_count,
        total_gross_amount,
        total_reclaimable
    )
    VALUES (
        gen_random_uuid(),
        v_user_id,
        'activity_2023.pdf',
        'raw/activity_2023.pdf',
        'InteractiveBrokers',
        'PARSED',
        NOW(),
        1,
        150.00,
        22.50
    )
    RETURNING id INTO v_stmt_id;

    -- 3. Insert Dividend (TotalEnergies) linked to statement
    INSERT INTO dividends (
        user_id,
        statement_id,
        security_name,
        isin,
        payment_date,
        gross_amount,
        currency,
        withholding_tax,
        withholding_rate,
        reclaimable_amount,
        treaty_rate,
        source_country
    )
    VALUES (
        v_user_id,
        v_stmt_id,
        'TotalEnergies SE',
        'FR0000120271',
        '2023-04-03',
        150.00,
        'EUR',
        19.20,
        12.80, -- Effective rate observed
        22.50, -- 15% of 150
        15.00,
        'FR'
    );
    
    RAISE NOTICE '✅ Seed data inserted for user: %', v_user_id;
END $$;
