-- Tax Dividend AI - Database Schema Initialization
-- This script runs automatically when PostgreSQL container starts for the first time

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    tax_id VARCHAR(50),
    address TEXT,
    canton VARCHAR(2),  -- Swiss canton code (e.g., VD, GE, ZH)
    country VARCHAR(2) DEFAULT 'CH',  -- ISO country code
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    is_active BOOLEAN DEFAULT true,
    is_verified BOOLEAN DEFAULT false
);

-- Generated Forms
CREATE TABLE IF NOT EXISTS generated_forms (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    s3_key VARCHAR(500) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    tax_year INTEGER NOT NULL,
    form_type VARCHAR(50) NOT NULL,  -- '5000', '5001', 'BUNDLE'
    status VARCHAR(50) DEFAULT 'GENERATED',  -- GENERATED, SIGNED, SUBMITTED, APPROVED, REJECTED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    metadata JSONB  -- Additional flexible data
);

-- Dividends (linked to forms)
CREATE TABLE IF NOT EXISTS dividends (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    form_id UUID REFERENCES generated_forms(id) ON DELETE CASCADE,
    security_name VARCHAR(255) NOT NULL,
    isin VARCHAR(12) NOT NULL,  -- International Securities Identification Number
    payment_date DATE NOT NULL,
    gross_amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,  -- ISO currency code (e.g., EUR, USD, CHF)
    withholding_tax DECIMAL(12,2) NOT NULL,
    withholding_rate DECIMAL(5,2) NOT NULL,  -- Percentage (e.g., 30.00 for 30%)
    reclaimable_amount DECIMAL(12,2) NOT NULL,
    treaty_rate DECIMAL(5,2),  -- Treaty tax rate (e.g., 15.00 for 15%)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Form Submissions (tracking submission status)
CREATE TABLE IF NOT EXISTS form_submissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    form_id UUID REFERENCES generated_forms(id) ON DELETE CASCADE,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    submission_method VARCHAR(50),  -- EMAIL, POSTAL, API
    tracking_number VARCHAR(100),
    status VARCHAR(50) DEFAULT 'PENDING',  -- PENDING, RECEIVED, PROCESSING, APPROVED, REJECTED
    status_updated_at TIMESTAMP,
    notes TEXT,
    metadata JSONB
);

-- Audit Log (for compliance)
CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL,  -- LOGIN, LOGOUT, FORM_GENERATED, FORM_SUBMITTED, etc.
    entity_type VARCHAR(50),  -- USER, FORM, DIVIDEND, etc.
    entity_id UUID,
    details JSONB,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tax Rules (for storing tax treaty rules)
CREATE TABLE IF NOT EXISTS tax_rules (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    source_country VARCHAR(2) NOT NULL,  -- Country where dividend is paid (e.g., FR)
    residence_country VARCHAR(2) NOT NULL,  -- Country of tax residence (e.g., CH)
    security_type VARCHAR(50) DEFAULT 'EQUITY',  -- EQUITY, BOND, etc.
    standard_withholding_rate DECIMAL(5,2) NOT NULL,  -- Standard rate (e.g., 30.00)
    treaty_rate DECIMAL(5,2),  -- Treaty reduced rate (e.g., 15.00)
    relief_at_source_available BOOLEAN DEFAULT false,
    refund_procedure_available BOOLEAN DEFAULT true,
    effective_from DATE NOT NULL,
    effective_to DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(source_country, residence_country, security_type, effective_from)
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_generated_forms_user_id ON generated_forms(user_id);
CREATE INDEX IF NOT EXISTS idx_generated_forms_tax_year ON generated_forms(tax_year);
CREATE INDEX IF NOT EXISTS idx_dividends_form_id ON dividends(form_id);
CREATE INDEX IF NOT EXISTS idx_dividends_isin ON dividends(isin);
CREATE INDEX IF NOT EXISTS idx_dividends_payment_date ON dividends(payment_date);
CREATE INDEX IF NOT EXISTS idx_form_submissions_form_id ON form_submissions(form_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at ON audit_logs(created_at);
CREATE INDEX IF NOT EXISTS idx_tax_rules_countries ON tax_rules(source_country, residence_country);

-- Insert default tax rule for France → Switzerland
INSERT INTO tax_rules (
    source_country,
    residence_country,
    security_type,
    standard_withholding_rate,
    treaty_rate,
    relief_at_source_available,
    refund_procedure_available,
    effective_from,
    notes
) VALUES (
    'FR',
    'CH',
    'EQUITY',
    30.00,
    15.00,
    true,  -- Relief at source available since 2023
    true,
    '2023-01-01',
    'France-Switzerland tax treaty. Relief at Source (Amont) available since 2023. Refund (Aval) still possible.'
) ON CONFLICT DO NOTHING;

-- Create a function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger to auto-update updated_at
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tax_rules_updated_at BEFORE UPDATE ON tax_rules
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Success message
DO $$
BEGIN
    RAISE NOTICE 'Tax Dividend AI database schema initialized successfully';
END $$;
