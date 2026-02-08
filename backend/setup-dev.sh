#!/bin/bash
set -e

# Configuration
DB_CONTAINER="tax-dividend-postgres-dev"
DB_USER="taxdividend_user"
DB_NAME="taxdividend_dev"
SEED_FILE="scripts/sql/seed_dev_data.sql"

# Check if seed file exists
if [ ! -f "$SEED_FILE" ]; then
    echo "❌ Error: Seed file not found at $SEED_FILE"
    exit 1
fi

# Check if container is running
if ! docker ps --format '{{.Names}}' | grep -q "^${DB_CONTAINER}$"; then
    echo "❌ Error: Database container '$DB_CONTAINER' is not running."
    echo "   Please run 'docker compose up -d' first."
    exit 1
fi

echo "🚀 Seeding development data..."
echo "   Container: $DB_CONTAINER"
echo "   Database:  $DB_NAME"
echo "   User:      $DB_USER"
echo ""

# Execute SQL via docker exec
cat "$SEED_FILE" | docker exec -i "$DB_CONTAINER" psql -U "$DB_USER" -d "$DB_NAME"

echo ""
echo "✅ Seeding complete!"
