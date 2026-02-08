#!/bin/bash

# ============================================================================
# Apply Keycloak Theme - Tax Dividend AI
# ============================================================================
# This script ensures the custom theme is applied to the realm
# Usage: ./apply-theme.sh
# ============================================================================

set -e

REALM_NAME="tax-dividend"
THEME_NAME="tax-dividend"
KEYCLOAK_CONTAINER="tax-dividend-keycloak"
KEYCLOAK_DB_CONTAINER="tax-dividend-keycloak-db"

echo "🎨 Applying Keycloak Theme..."
echo ""

# Check if Keycloak container is running
if ! docker ps | grep -q "$KEYCLOAK_CONTAINER"; then
    echo "❌ Keycloak container is not running"
    echo "   Start infrastructure with: make infra-up"
    exit 1
fi

# Wait for Keycloak to be ready
echo "⏳ Waiting for Keycloak to be ready..."
for i in {1..30}; do
    if docker exec "$KEYCLOAK_CONTAINER" curl -sf http://localhost:8080/health/ready > /dev/null 2>&1; then
        echo "✅ Keycloak is ready"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "⚠️  Keycloak not ready after 30s, continuing anyway..."
    fi
    sleep 1
done

echo ""

# Check current theme configuration
echo "🔍 Checking current theme configuration..."
CURRENT_THEME=$(docker exec "$KEYCLOAK_DB_CONTAINER" psql -U keycloak -d keycloak -tAc \
    "SELECT login_theme FROM realm WHERE name = '$REALM_NAME';" 2>/dev/null || echo "")

if [ "$CURRENT_THEME" == "$THEME_NAME" ]; then
    echo "✅ Theme '$THEME_NAME' is already applied"
    echo ""
    echo "📋 Theme Details:"
    echo "   Realm: $REALM_NAME"
    echo "   Theme: $THEME_NAME"
    echo "   Status: Active ✓"
    echo ""
    exit 0
fi

# Apply theme via SQL
echo "🔧 Applying theme to realm..."
docker exec "$KEYCLOAK_DB_CONTAINER" psql -U keycloak -d keycloak -c \
    "UPDATE realm SET login_theme = '$THEME_NAME' WHERE name = '$REALM_NAME';" > /dev/null

# Verify
UPDATED_THEME=$(docker exec "$KEYCLOAK_DB_CONTAINER" psql -U keycloak -d keycloak -tAc \
    "SELECT login_theme FROM realm WHERE name = '$REALM_NAME';")

if [ "$UPDATED_THEME" == "$THEME_NAME" ]; then
    echo "✅ Theme applied successfully!"
    echo ""
    echo "📋 Theme Details:"
    echo "   Realm: $REALM_NAME"
    echo "   Theme: $THEME_NAME"
    echo "   Status: Applied ✓"
    echo ""
    echo "💡 Next steps:"
    echo "   1. Clear your browser cache (Ctrl+Shift+R)"
    echo "   2. Go to http://localhost:5173"
    echo "   3. The custom theme should now be visible"
    echo ""
else
    echo "❌ Failed to apply theme"
    echo "   Current theme: $UPDATED_THEME"
    echo "   Expected: $THEME_NAME"
    exit 1
fi
