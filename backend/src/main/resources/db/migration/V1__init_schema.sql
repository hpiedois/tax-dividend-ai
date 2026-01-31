-- ============================================================================
-- V1: Initialize Tax Dividend AI Database Schema
-- ============================================================================
-- Complete database initialization for Tax Dividend AI application
-- Includes: users, forms, dividends, submissions, audit, tax rules
-- ============================================================================

-- ============================================================================
-- FUNCTIONS
-- ============================================================================

-- Auto-update trigger function for updated_at columns
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- ============================================================================
-- TABLES
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Users Table
-- ----------------------------------------------------------------------------
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    tax_id VARCHAR(50),
    address TEXT,
    canton VARCHAR(2),
    country VARCHAR(2) DEFAULT 'CH',
    status VARCHAR(50) DEFAULT 'ACTIVE',
    is_active BOOLEAN DEFAULT true,
    is_verified BOOLEAN DEFAULT false,
    verification_token VARCHAR(255),
    token_expiry TIMESTAMP,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_is_active ON users(is_active);
CREATE INDEX idx_users_status ON users(status);

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE users IS 'Application users with authentication and tax information';
COMMENT ON COLUMN users.tax_id IS 'Swiss NIF (Numéro d''Identification Fiscale)';
COMMENT ON COLUMN users.canton IS 'Swiss canton code for tax residence (VD, GE, ZH, etc.)';
COMMENT ON COLUMN users.status IS 'User status: ACTIVE, PENDING, SUSPENDED, DELETED';

-- ----------------------------------------------------------------------------
-- Generated Forms Table
-- ----------------------------------------------------------------------------
CREATE TABLE generated_forms (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    s3_key VARCHAR(500) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    tax_year INTEGER NOT NULL,
    form_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'GENERATED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    metadata JSONB
);

CREATE INDEX idx_generated_forms_user_id ON generated_forms(user_id);
CREATE INDEX idx_generated_forms_tax_year ON generated_forms(tax_year);
CREATE INDEX idx_generated_forms_status ON generated_forms(status);
CREATE INDEX idx_generated_forms_created_at ON generated_forms(created_at);

COMMENT ON TABLE generated_forms IS 'Metadata for generated tax forms (5000, 5001, bundles)';
COMMENT ON COLUMN generated_forms.s3_key IS 'S3/MinIO object key for the generated PDF/ZIP';
COMMENT ON COLUMN generated_forms.form_type IS 'Type: 5000 (residence), 5001 (dividends), BUNDLE (both)';
COMMENT ON COLUMN generated_forms.status IS 'Status: GENERATED, SIGNED, SUBMITTED, APPROVED, REJECTED';
COMMENT ON COLUMN generated_forms.metadata IS 'Additional metadata (JSON): generation params, validation results, etc.';

-- ----------------------------------------------------------------------------
-- Dividends Table
-- ----------------------------------------------------------------------------
CREATE TABLE dividends (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    form_id UUID REFERENCES generated_forms(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    security_name VARCHAR(255) NOT NULL,
    isin VARCHAR(12) NOT NULL,
    payment_date DATE NOT NULL,
    gross_amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    withholding_tax DECIMAL(12,2) NOT NULL,
    withholding_rate DECIMAL(5,2) NOT NULL,
    reclaimable_amount DECIMAL(12,2) NOT NULL,
    treaty_rate DECIMAL(5,2),
    source_country VARCHAR(2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT positive_amounts CHECK (gross_amount >= 0 AND withholding_tax >= 0 AND reclaimable_amount >= 0),
    CONSTRAINT valid_rates CHECK (withholding_rate >= 0 AND withholding_rate <= 100 AND (treaty_rate IS NULL OR (treaty_rate >= 0 AND treaty_rate <= 100)))
);

CREATE INDEX idx_dividends_form_id ON dividends(form_id);
CREATE INDEX idx_dividends_user_id ON dividends(user_id);
CREATE INDEX idx_dividends_isin ON dividends(isin);
CREATE INDEX idx_dividends_payment_date ON dividends(payment_date);
CREATE INDEX idx_dividends_source_country ON dividends(source_country);

COMMENT ON TABLE dividends IS 'Dividend data extracted from bank statements or manually entered';
COMMENT ON COLUMN dividends.isin IS 'International Securities Identification Number (12 chars)';
COMMENT ON COLUMN dividends.withholding_rate IS 'Actual withholding rate applied (%)';
COMMENT ON COLUMN dividends.treaty_rate IS 'Tax treaty reduced rate (%)';
COMMENT ON COLUMN dividends.reclaimable_amount IS 'Amount that can be reclaimed (withheld - treaty)';
COMMENT ON COLUMN dividends.source_country IS 'Country where dividend was paid (ISO 2-letter code)';

-- ----------------------------------------------------------------------------
-- Form Submissions Table
-- ----------------------------------------------------------------------------
CREATE TABLE form_submissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    form_id UUID NOT NULL REFERENCES generated_forms(id) ON DELETE CASCADE,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    submission_method VARCHAR(50),
    tracking_number VARCHAR(100),
    status VARCHAR(50) DEFAULT 'PENDING',
    status_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    metadata JSONB
);

CREATE INDEX idx_form_submissions_form_id ON form_submissions(form_id);
CREATE INDEX idx_form_submissions_status ON form_submissions(status);
CREATE INDEX idx_form_submissions_submitted_at ON form_submissions(submitted_at);

COMMENT ON TABLE form_submissions IS 'Tracking of form submissions to tax authorities';
COMMENT ON COLUMN form_submissions.submission_method IS 'How the form was submitted: EMAIL, POSTAL, API';
COMMENT ON COLUMN form_submissions.status IS 'Status: PENDING, RECEIVED, PROCESSING, APPROVED, REJECTED';
COMMENT ON COLUMN form_submissions.tracking_number IS 'Official tracking number from tax authority';
COMMENT ON COLUMN form_submissions.metadata IS 'Additional submission data (JSON): API response, email confirmation, etc.';

-- ----------------------------------------------------------------------------
-- Audit Logs Table
-- ----------------------------------------------------------------------------
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id UUID,
    details JSONB,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_entity_type ON audit_logs(entity_type);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);

COMMENT ON TABLE audit_logs IS 'Audit trail for compliance and security monitoring';
COMMENT ON COLUMN audit_logs.action IS 'Action performed: LOGIN, LOGOUT, FORM_GENERATED, PDF_PARSED, etc.';
COMMENT ON COLUMN audit_logs.entity_type IS 'Type of entity: USER, FORM, DIVIDEND, SUBMISSION, etc.';
COMMENT ON COLUMN audit_logs.details IS 'Additional context about the action (JSON)';
COMMENT ON COLUMN audit_logs.ip_address IS 'Client IP address (for security tracking)';

-- ----------------------------------------------------------------------------
-- Tax Rules Table
-- ----------------------------------------------------------------------------
CREATE TABLE tax_rules (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    source_country VARCHAR(2) NOT NULL,
    residence_country VARCHAR(2) NOT NULL,
    security_type VARCHAR(50) DEFAULT 'EQUITY',
    standard_withholding_rate DECIMAL(5,2) NOT NULL,
    treaty_rate DECIMAL(5,2),
    relief_at_source_available BOOLEAN DEFAULT false,
    refund_procedure_available BOOLEAN DEFAULT true,
    effective_from DATE NOT NULL,
    effective_to DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_rates CHECK (
        standard_withholding_rate >= 0 AND
        standard_withholding_rate <= 100 AND
        (treaty_rate IS NULL OR (treaty_rate >= 0 AND treaty_rate <= 100))
    ),
    CONSTRAINT valid_date_range CHECK (effective_to IS NULL OR effective_to >= effective_from),
    UNIQUE(source_country, residence_country, security_type, effective_from)
);

CREATE INDEX idx_tax_rules_countries ON tax_rules(source_country, residence_country);
CREATE INDEX idx_tax_rules_effective_dates ON tax_rules(effective_from, effective_to);
CREATE INDEX idx_tax_rules_security_type ON tax_rules(security_type);

CREATE TRIGGER update_tax_rules_updated_at
    BEFORE UPDATE ON tax_rules
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE tax_rules IS 'Tax treaty rules for cross-border dividend taxation';
COMMENT ON COLUMN tax_rules.source_country IS 'Country where dividend is paid (ISO 2-letter code)';
COMMENT ON COLUMN tax_rules.residence_country IS 'Country of tax residence (ISO 2-letter code)';
COMMENT ON COLUMN tax_rules.security_type IS 'Type of security: EQUITY, BOND, REIT, etc.';
COMMENT ON COLUMN tax_rules.standard_withholding_rate IS 'Default withholding rate without treaty (%)';
COMMENT ON COLUMN tax_rules.treaty_rate IS 'Reduced rate under tax treaty (%)';
COMMENT ON COLUMN tax_rules.relief_at_source_available IS 'Can apply treaty rate at payment time (Amont)';
COMMENT ON COLUMN tax_rules.refund_procedure_available IS 'Can reclaim excess after payment (Aval)';
COMMENT ON COLUMN tax_rules.effective_from IS 'Treaty/rule effective start date';
COMMENT ON COLUMN tax_rules.effective_to IS 'Treaty/rule end date (NULL if still active)';

-- ============================================================================
-- INITIAL DATA
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Default Tax Rules: France → Switzerland
-- ----------------------------------------------------------------------------

-- France → Switzerland (Equity) - Primary use case
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
    true,
    true,
    '2023-01-01',
    'France-Switzerland tax treaty. Relief at Source (Amont) available since January 1, 2023. Refund (Aval) procedure still available for dividends paid before 2023 or if relief at source was not applied.'
);

-- France → Switzerland (Bonds/Interest)
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
    'BOND',
    30.00,
    0.00,
    false,
    true,
    '2023-01-01',
    'Interest from bonds may be exempt from French withholding tax under the treaty, but verify specific bond type and issuer.'
);

-- ============================================================================
-- COMPLETION
-- ============================================================================

-- Log successful initialization
DO $$
BEGIN
    RAISE NOTICE '✅ Tax Dividend AI database schema initialized successfully';
    RAISE NOTICE '   - 6 tables created (users, generated_forms, dividends, form_submissions, audit_logs, tax_rules)';
    RAISE NOTICE '   - 2 default tax rules inserted (FR→CH for EQUITY and BOND)';
    RAISE NOTICE '   - All indexes and triggers configured';
END
$$;
