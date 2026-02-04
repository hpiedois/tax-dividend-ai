#!/bin/bash

# ============================================================================
# Infrastructure Setup - Tax Dividend AI
# ============================================================================
# This script prepares the infrastructure for first-time use
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "🏗️  Tax Dividend AI - Infrastructure Setup"
echo "==========================================="
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Error: Docker is not installed!"
    echo "Please install Docker Desktop from: https://www.docker.com/products/docker-desktop"
    exit 1
fi

# Check if Docker is running
if ! docker info &> /dev/null; then
    echo "❌ Error: Docker is not running!"
    echo "Please start Docker Desktop and try again."
    exit 1
fi

echo "✅ Docker is installed and running"
echo ""

# Check if docker-compose is available
if ! command -v docker-compose &> /dev/null; then
    echo "⚠️  Warning: docker-compose command not found"
    echo "Using 'docker compose' (Docker Compose V2) instead"
    COMPOSE_CMD="docker compose"
else
    COMPOSE_CMD="docker-compose"
fi

echo "✅ Docker Compose is available"
echo ""

# Create backup directories
echo "📁 Creating backup directories..."
mkdir -p backups/dev
mkdir -p backups/uat
mkdir -p backups/prod

echo "✅ Backup directories created"
echo ""

# Check environment files
echo "🔍 Checking environment files..."

if [ ! -f .env.dev ]; then
    echo "❌ Error: .env.dev not found!"
    echo "This file should exist. Please check the repository."
    exit 1
fi
echo "  ✅ .env.dev found"

if [ ! -f .env.uat ]; then
    echo "❌ Error: .env.uat not found!"
    echo "This file should exist. Please check the repository."
    exit 1
fi
echo "  ✅ .env.uat found"

if [ ! -f .env.prod.example ]; then
    echo "❌ Error: .env.prod.example not found!"
    echo "This file should exist. Please check the repository."
    exit 1
fi
echo "  ✅ .env.prod.example found"

if [ -f .env.prod ]; then
    echo "  ⚠️  .env.prod already exists (production configuration)"
else
    echo "  ℹ️  .env.prod not created yet (copy from .env.prod.example when needed)"
fi

echo ""

# Check migration files
echo "🔍 Checking migration files..."
if [ ! -d migrations ]; then
    echo "❌ Error: migrations directory not found!"
    exit 1
fi

MIGRATION_COUNT=$(ls -1 migrations/*.sql 2>/dev/null | wc -l)
if [ "$MIGRATION_COUNT" -lt 2 ]; then
    echo "❌ Error: Missing migration files in migrations/ directory"
    exit 1
fi

echo "  ✅ Found $MIGRATION_COUNT migration files"
echo ""

# Pull Docker images
echo "📥 Pulling Docker images (this may take a few minutes)..."
echo ""

echo "  Pulling PostgreSQL..."
docker pull postgres:16-alpine

echo "  Pulling MinIO..."
docker pull minio/minio:latest
docker pull minio/mc:latest

echo "  Pulling Redis..."
docker pull redis:7-alpine

echo ""
echo "✅ All Docker images pulled successfully"
echo ""

# Make scripts executable
echo "🔧 Setting script permissions..."
chmod +x start-dev.sh stop-dev.sh
chmod +x start-uat.sh stop-uat.sh
chmod +x backup-db.sh restore-db.sh
chmod +x setup.sh
chmod +x migrations/init-keycloak-db.sh

echo "✅ All scripts are executable"
echo ""

# Summary
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ Infrastructure setup complete!"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📚 Next steps:"
echo ""
echo "1️⃣  Start development environment:"
echo "   ./start-dev.sh"
echo ""
echo "2️⃣  Access services:"
echo "   • PostgreSQL:    localhost:5432"
echo "   • MinIO Console: http://localhost:9001"
echo ""
echo "3️⃣  Optional - Start UAT environment (runs in parallel):"
echo "   ./start-uat.sh"
echo ""
echo "4️⃣  Read documentation:"
echo "   • README.md         - Full infrastructure guide"
echo "   • ENVIRONMENTS.md   - Multi-environment architecture"
echo ""
echo "💡 Useful commands:"
echo "   • Stop dev:      ./stop-dev.sh"
echo "   • Backup DB:     ./backup-db.sh dev"
echo "   • View logs:     docker-compose -f docker-compose.dev.yml logs -f"
echo ""
echo "⚠️  For production deployment:"
echo "   Use managed cloud services (AWS RDS, Google Cloud SQL, etc.)"
echo "   See README.md section 'Production Deployment Recommendations'"
echo ""
