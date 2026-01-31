#!/bin/bash

# ============================================================================
# Run Backend - Development Mode
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "🚀 Starting Tax Dividend AI Backend - Development"
echo "=================================================="
echo ""

# Check if infrastructure is running
if ! docker ps | grep -q "tax-dividend-postgres-dev"; then
    echo "⚠️  Warning: Development infrastructure not running"
    echo "Starting infrastructure..."
    cd ../infrastructure
    ./start-dev.sh
    cd "$SCRIPT_DIR"
    echo ""
fi

echo "▶️  Starting Spring Boot application..."
echo ""

./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Note: This will block until you press Ctrl+C
