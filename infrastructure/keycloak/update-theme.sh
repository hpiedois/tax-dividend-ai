#!/bin/bash

# Script to update Keycloak theme
# This script stops Keycloak, removes old data, and restarts with the updated theme

set -e

echo "🎨 Updating Keycloak theme..."
echo ""

# Check if we're in the right directory
if [ ! -f "docker-compose.yml" ]; then
    echo "❌ Error: docker-compose.yml not found. Please run this script from the project root."
    exit 1
fi

# Stop Keycloak
echo "⏸️  Stopping Keycloak..."
docker compose stop keycloak

# Remove Keycloak container
echo "🗑️  Removing Keycloak container..."
docker compose rm -f keycloak

# Ask if user wants to reset database (optional)
read -p "🔄 Do you want to reset Keycloak database? This will reimport the realm. (y/N): " -n 1 -r
echo ""
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "🗑️  Removing Keycloak database..."
    docker compose rm -f keycloak-db
    docker volume rm -f tax-dividend-ai_keycloak-db-data 2>/dev/null || true
    echo "✅ Database removed"
fi

# Restart Keycloak
echo "🚀 Starting Keycloak..."
docker compose up -d keycloak

echo ""
echo "✅ Keycloak is starting up..."
echo "⏳ Please wait ~30 seconds for Keycloak to fully start"
echo ""
echo "📍 URLs:"
echo "   - Admin Console: http://localhost:8180/admin (admin/admin)"
echo "   - Login Page: http://localhost:8180/realms/tax-dividend/account"
echo ""
echo "💡 To view logs: docker compose logs -f keycloak"
