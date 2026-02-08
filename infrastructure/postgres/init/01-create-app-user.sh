#!/bin/bash
# ============================================================================
# PostgreSQL Initialization Script
# ============================================================================
# Purpose: Create application database user with proper permissions
# Executed automatically when PostgreSQL container starts for the first time
# ============================================================================

set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}🔧 Initializing Tax Dividend AI database...${NC}"

# Create application user if it doesn't exist
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- Create application user
    DO \$\$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = '${TAXDIVIDEND_DB_USER}') THEN
            CREATE USER ${TAXDIVIDEND_DB_USER} WITH PASSWORD '${TAXDIVIDEND_DB_PASSWORD}';
            RAISE NOTICE 'Created user: ${TAXDIVIDEND_DB_USER}';
        ELSE
            RAISE NOTICE 'User ${TAXDIVIDEND_DB_USER} already exists';
        END IF;
    END
    \$\$;

    -- Grant privileges
    GRANT ALL PRIVILEGES ON DATABASE ${POSTGRES_DB} TO ${TAXDIVIDEND_DB_USER};
    GRANT ALL PRIVILEGES ON SCHEMA public TO ${TAXDIVIDEND_DB_USER};

    -- Create taxdividend schema if it doesn't exist
    CREATE SCHEMA IF NOT EXISTS taxdividend;
    GRANT ALL PRIVILEGES ON SCHEMA taxdividend TO ${TAXDIVIDEND_DB_USER};

    -- Grant default privileges for future objects
    ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO ${TAXDIVIDEND_DB_USER};
    ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO ${TAXDIVIDEND_DB_USER};
    ALTER DEFAULT PRIVILEGES IN SCHEMA taxdividend GRANT ALL ON TABLES TO ${TAXDIVIDEND_DB_USER};
    ALTER DEFAULT PRIVILEGES IN SCHEMA taxdividend GRANT ALL ON SEQUENCES TO ${TAXDIVIDEND_DB_USER};

    -- Set search path
    ALTER USER ${TAXDIVIDEND_DB_USER} SET search_path TO taxdividend, public;
EOSQL

echo -e "${GREEN}✅ Database initialization complete!${NC}"
echo -e "${YELLOW}   Database: ${POSTGRES_DB}${NC}"
echo -e "${YELLOW}   User: ${TAXDIVIDEND_DB_USER}${NC}"
echo -e "${YELLOW}   Schema: taxdividend${NC}"
