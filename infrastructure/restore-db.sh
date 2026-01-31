#!/bin/bash

# ============================================================================
# Database Restore Script - Tax Dividend AI
# ============================================================================
# Usage: ./restore-db.sh [dev|uat|prod] [backup_file.sql.gz]
# ============================================================================

set -e

ENVIRONMENT=${1:-dev}
BACKUP_FILE=$2
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [ -z "$BACKUP_FILE" ]; then
    echo "❌ Error: Backup file not specified!"
    echo "Usage: ./restore-db.sh [dev|uat|prod] [backup_file.sql.gz]"
    exit 1
fi

if [ ! -f "$BACKUP_FILE" ]; then
    echo "❌ Error: Backup file not found: $BACKUP_FILE"
    exit 1
fi

echo "⚠️  WARNING: Database Restore - Environment: $ENVIRONMENT"
echo "=================================================="
echo "This will REPLACE ALL DATA in the $ENVIRONMENT database!"
echo "Backup file: $BACKUP_FILE"
echo ""
read -p "Are you sure you want to continue? (yes/no): " -r
if [[ ! $REPLY =~ ^[Yy][Ee][Ss]$ ]]; then
    echo "❌ Restore cancelled."
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
  prod)
    CONTAINER_NAME="tax-dividend-postgres-prod"
    DB_NAME="taxdividend"
    DB_USER="postgres"
    ;;
  *)
    echo "❌ Error: Invalid environment. Use 'dev', 'uat', or 'prod'"
    exit 1
    ;;
esac

# Check if container is running
if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo "❌ Error: Container $CONTAINER_NAME is not running!"
    echo "Please start the $ENVIRONMENT environment first."
    exit 1
fi

# Decompress and restore
echo "🔄 Restoring database..."
gunzip -c "$BACKUP_FILE" | docker exec -i $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME

echo ""
echo "✅ Database restored successfully!"
echo ""
echo "⚠️  Remember to restart your application to pick up the changes."
echo ""
