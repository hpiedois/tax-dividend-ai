#!/bin/bash

# ============================================================================
# Start Development Environment - Tax Dividend AI
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "🚀 Starting Tax Dividend AI - Development Environment"
echo "=================================================="

# Check if .env.dev exists
if [ ! -f .env.dev ]; then
    echo "❌ Error: .env.dev file not found!"
    echo "Please create it from .env.example or .env.dev.example"
    exit 1
fi

# Pull latest images
echo "📥 Pulling latest Docker images..."
docker-compose -p tax-dividend-infra -f docker-compose.dev.yml pull

# Start services
echo "🏗️  Starting services..."
docker-compose -p tax-dividend-infra -f docker-compose.dev.yml --env-file .env.dev up -d

# Wait for services to be healthy
echo "⏳ Waiting for services to be healthy..."
sleep 5

# Check health
echo "🏥 Health check..."
docker-compose -p tax-dividend-infra -f docker-compose.dev.yml --env-file .env.dev ps

echo ""
echo "✅ Development environment started successfully!"
echo ""
echo "📊 Access your services:"
echo "  - PostgreSQL:    localhost:5432"
echo "  - MinIO API:     http://localhost:9000"
echo "  - MinIO Console: http://localhost:9001"
echo "  - Redis:         localhost:6379"
echo ""
echo "📝 Database credentials:"
echo "  - Database: taxdividend_dev"
echo "  - User:     postgres"
echo "  - Password: dev_password_123"
echo ""
echo "💡 Useful commands:"
echo "  - View logs:     docker-compose -f docker-compose.dev.yml logs -f"
echo "  - Stop:          ./stop-dev.sh"
echo "  - Restart:       ./restart-dev.sh"
echo "  - DB console:    docker exec -it tax-dividend-postgres-dev psql -U postgres -d taxdividend_dev"
echo ""
