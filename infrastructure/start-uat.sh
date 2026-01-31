#!/bin/bash

# ============================================================================
# Start UAT Environment - Tax Dividend AI
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "🚀 Starting Tax Dividend AI - UAT Environment"
echo "=================================================="

# Check if .env.uat exists
if [ ! -f .env.uat ]; then
    echo "❌ Error: .env.uat file not found!"
    echo "Please create it with proper credentials"
    exit 1
fi

# Pull latest images
echo "📥 Pulling latest Docker images..."
docker-compose -f docker-compose.uat.yml pull

# Start services
echo "🏗️  Starting services..."
docker-compose -f docker-compose.uat.yml --env-file .env.uat up -d

# Wait for services to be healthy
echo "⏳ Waiting for services to be healthy..."
sleep 5

# Check health
echo "🏥 Health check..."
docker-compose -f docker-compose.uat.yml --env-file .env.uat ps

echo ""
echo "✅ UAT environment started successfully!"
echo ""
echo "📊 Access your services:"
echo "  - PostgreSQL:    localhost:5433"
echo "  - MinIO API:     http://localhost:9002"
echo "  - MinIO Console: http://localhost:9003"
echo "  - Redis:         localhost:6380"
echo ""
echo "🔒 Security note: UAT passwords are defined in .env.uat"
echo ""
echo "💡 Useful commands:"
echo "  - View logs:     docker-compose -f docker-compose.uat.yml logs -f"
echo "  - Stop:          ./stop-uat.sh"
echo "  - DB console:    docker exec -it tax-dividend-postgres-uat psql -U postgres -d taxdividend_uat"
echo ""
