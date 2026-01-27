-- Users
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    tax_id VARCHAR(50),
    city VARCHAR(100),
    postal_code VARCHAR(20),
    address TEXT,
    country VARCHAR(100) DEFAULT 'Suisse',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Generated Forms
CREATE TABLE generated_forms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    s3_key VARCHAR(500) NOT NULL,
    file_name VARCHAR(255),
    tax_year INTEGER NOT NULL,
    status VARCHAR(50) DEFAULT 'GENERATED',
    created_at TIMESTAMP DEFAULT NOW(),
    expires_at TIMESTAMP
);

-- Dividends
CREATE TABLE dividends (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    form_id UUID REFERENCES generated_forms(id) ON DELETE CASCADE,
    security_name VARCHAR(255) NOT NULL,
    isin VARCHAR(12),
    payment_date DATE NOT NULL,
    gross_amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'EUR',
    withholding_tax DECIMAL(10,2) NOT NULL,
    treaty_amount DECIMAL(10,2),
    reclaimable_amount DECIMAL(10,2) NOT NULL,
    french_rate DECIMAL(5,2)
);

-- Form Submissions (tracking)
CREATE TABLE form_submissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    form_id UUID REFERENCES generated_forms(id) ON DELETE CASCADE,
    submitted_at TIMESTAMP DEFAULT NOW(),
    submission_method VARCHAR(50), -- EMAIL, API, POSTAL
    tracking_number VARCHAR(100),
    status VARCHAR(50) DEFAULT 'PENDING' -- PENDING, APPROVED, REJECTED
);

-- Indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_forms_user_id ON generated_forms(user_id);
CREATE INDEX idx_forms_tax_year ON generated_forms(tax_year);
CREATE INDEX idx_dividends_form_id ON dividends(form_id);
