#!/bin/bash
set -e

# ============================================================================
# SCHEMAS AND ROLES INITIALIZATION - Tax Dividend AI
# ============================================================================

echo "==> Creating application schema and roles..."

# Use environment variables if available, otherwise fallback to defaults (safety net)
APP_USER=${TAXDIVIDEND_DB_USER:-taxdividend_user}
APP_PASSWORD=${TAXDIVIDEND_DB_PASSWORD:-dev_password_123}
DB_NAME=${POSTGRES_DB:-taxdividend_dev}

echo "    Target Database: $DB_NAME"
echo "    App User:        $APP_USER"

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$DB_NAME" <<-EOSQL

    -- ----------------------------------------------------------------------------
    -- SCHEMA: Tax Dividend Application
    -- ----------------------------------------------------------------------------
    CREATE SCHEMA IF NOT EXISTS taxdividend;
    COMMENT ON SCHEMA taxdividend IS 'Tax dividend AI application schema - tables managed by Flyway';

    -- ----------------------------------------------------------------------------
    -- ROLE: Application User
    -- ----------------------------------------------------------------------------
    DO \$\$
    BEGIN
      IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = '$APP_USER') THEN
        CREATE ROLE $APP_USER WITH LOGIN PASSWORD '$APP_PASSWORD';
        RAISE NOTICE 'Created role: $APP_USER';
      ELSE
        RAISE NOTICE 'Role $APP_USER already exists';
      END IF;
    END
    \$\$;

    -- Grant permissions on schema
    GRANT USAGE ON SCHEMA public TO $APP_USER;
    GRANT CREATE, USAGE ON SCHEMA taxdividend TO $APP_USER;

    -- Grant permissions on all tables (for Flyway-created tables)
    GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA taxdividend TO $APP_USER;
    GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA taxdividend TO $APP_USER;

    -- Grant default permissions for future tables (created by Flyway)
    ALTER DEFAULT PRIVILEGES IN SCHEMA taxdividend GRANT ALL ON TABLES TO $APP_USER;
    ALTER DEFAULT PRIVILEGES IN SCHEMA taxdividend GRANT ALL ON SEQUENCES TO $APP_USER;
    ALTER DEFAULT PRIVILEGES IN SCHEMA taxdividend GRANT EXECUTE ON FUNCTIONS TO $APP_USER;

    -- Set default search path for this user
    ALTER ROLE $APP_USER IN DATABASE $DB_NAME SET search_path = taxdividend, public;

EOSQL

echo "==> Schema and roles created successfully for user: $APP_USER"
