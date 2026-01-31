#!/bin/bash

# ============================================================================
# Database Backup Script - Tax Dividend AI
# ============================================================================
# Usage: ./backup-db.sh [dev|uat|prod]
# ============================================================================

set -e

ENVIRONMENT=${1:-dev}
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKUP_DIR="$SCRIPT_DIR/backups/$ENVIRONMENT"

# Create backup directory if it doesn't exist
mkdir -p "$BACKUP_DIR"

echo "📦 Creating database backup - Environment: $ENVIRONMENT"
echo "=================================================="

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

BACKUP_FILE="$BACKUP_DIR/${DB_NAME}_${TIMESTAMP}.sql"
BACKUP_FILE_GZ="$BACKUP_FILE.gz"

# Check if container is running
if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo "❌ Error: Container $CONTAINER_NAME is not running!"
    echo "Please start the $ENVIRONMENT environment first."
    exit 1
fi

# Create backup
echo "🔄 Creating backup..."
docker exec $CONTAINER_NAME pg_dump -U $DB_USER -d $DB_NAME > "$BACKUP_FILE"

# Compress backup
echo "📦 Compressing backup..."
gzip "$BACKUP_FILE"

# Get file size
SIZE=$(du -h "$BACKUP_FILE_GZ" | cut -f1)

echo ""
echo "✅ Backup completed successfully!"
echo ""
echo "📁 Backup file: $BACKUP_FILE_GZ"
echo "📊 Size: $SIZE"
echo ""
echo "💡 To restore this backup:"
echo "   ./restore-db.sh $ENVIRONMENT $BACKUP_FILE_GZ"
echo ""

# Clean up old backups (keep last 7 days)
echo "🧹 Cleaning old backups (keeping last 7 days)..."
find "$BACKUP_DIR" -name "*.sql.gz" -mtime +7 -delete
echo "✅ Cleanup complete!"
echo ""
