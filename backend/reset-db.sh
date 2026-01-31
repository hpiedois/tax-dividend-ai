#!/bin/bash

# ============================================================================
# Reset Database - Tax Dividend AI Backend
# ============================================================================
# Drops and recreates the development database with fresh schema
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENVIRONMENT=${1:-dev}

echo "⚠️  WARNING: Database Reset - Environment: $ENVIRONMENT"
echo "======================================================="
echo "This will DELETE ALL DATA in the $ENVIRONMENT database!"
echo ""
read -p "Are you sure you want to continue? (yes/no): " -r
if [[ ! $REPLY =~ ^[Yy][Ee][Ss]$ ]]; then
    echo "❌ Reset cancelled."
    exit 1
fi

case $ENVIRONMENT in
  dev)
    CONTAINER_NAME="tax-dividend-postgres-dev"
    DB_NAME="taxdividend_dev"
    DB_USER="postgres"
    ;;
  uat)
    CONTAINER_NAME="tax-dividend-postgres-uat"
    DB_NAME="taxdividend_uat"
    DB_USER="postgres"
    ;;
  *)
    echo "❌ Error: Invalid environment '$ENVIRONMENT'"
    echo "Usage: ./reset-db.sh [dev|uat]"
    exit 1
    ;;
esac

# Check if container is running
if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo "❌ Error: Container $CONTAINER_NAME is not running!"
    echo "Please start the $ENVIRONMENT environment first."
    exit 1
fi

echo ""
echo "🗑️  Dropping database schema..."

docker exec -i $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME <<EOF
-- Drop all tables in the taxdividend schema
DROP SCHEMA IF EXISTS taxdividend CASCADE;

-- Drop Flyway history
DROP TABLE IF EXISTS flyway_schema_history CASCADE;

-- Recreate empty schema
CREATE SCHEMA taxdividend;
GRANT ALL ON SCHEMA taxdividend TO taxdividend_user;
EOF

echo "✅ Database schema dropped"
echo ""
echo "🔄 Restarting backend to trigger Flyway migrations..."
echo ""
echo "📝 Next steps:"
echo "1. Stop the backend if it's running (Ctrl+C)"
echo "2. Start it again: ./run-dev.sh"
echo "3. Flyway will automatically recreate all tables from V1__init_schema.sql"
echo ""
