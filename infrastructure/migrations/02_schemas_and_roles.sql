-- ============================================================================
-- SCHEMAS AND ROLES INITIALIZATION - Tax Dividend AI
-- ============================================================================
-- Création du schéma applicatif et des rôles
-- Les tables seront créées par Flyway dans le backend
-- ============================================================================

\echo '==> Creating application schema and roles...'

-- ----------------------------------------------------------------------------
-- SCHEMA: Tax Dividend Application
-- ----------------------------------------------------------------------------
CREATE SCHEMA IF NOT EXISTS taxdividend;
COMMENT ON SCHEMA taxdividend IS 'Tax dividend AI application schema - tables managed by Flyway';

-- ----------------------------------------------------------------------------
-- ROLE: Application User
-- ----------------------------------------------------------------------------
DO $$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'taxdividend_user') THEN
    CREATE ROLE taxdividend_user WITH LOGIN PASSWORD 'dev_password_123';
    RAISE NOTICE 'Created role: taxdividend_user';
  ELSE
    RAISE NOTICE 'Role taxdividend_user already exists';
  END IF;
END
$$;

-- Grant permissions on schema
GRANT USAGE ON SCHEMA public TO taxdividend_user;
GRANT CREATE, USAGE ON SCHEMA taxdividend TO taxdividend_user;

-- Grant permissions on all tables (for Flyway-created tables)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA taxdividend TO taxdividend_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA taxdividend TO taxdividend_user;

-- Grant default permissions for future tables (created by Flyway)
ALTER DEFAULT PRIVILEGES IN SCHEMA taxdividend GRANT ALL ON TABLES TO taxdividend_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA taxdividend GRANT ALL ON SEQUENCES TO taxdividend_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA taxdividend GRANT EXECUTE ON FUNCTIONS TO taxdividend_user;

-- Set default search path for this user
ALTER ROLE taxdividend_user IN DATABASE taxdividend SET search_path = taxdividend, public;

\echo '==> Schema and roles created successfully!'
\echo '==> IMPORTANT: Tables will be created by Flyway migrations in the backend'
\echo ''
