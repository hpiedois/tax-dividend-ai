#!/bin/bash

# ============================================================================
# Backend Development Setup - Tax TaxCalculationResultDto AI
# ============================================================================
# Sets up backend for development (infrastructure + dependencies)
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "🏗️  Tax TaxCalculationResultDto AI - Backend Setup"
echo "======================================"
echo ""

# Check Java
echo "🔍 Checking Java installation..."
if ! command -v java &> /dev/null; then
    echo "❌ Error: Java is not installed!"
    echo "Please install Java 21+ from: https://adoptium.net/"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "❌ Error: Java 21+ is required (found: Java $JAVA_VERSION)"
    exit 1
fi

echo "✅ Java $JAVA_VERSION found"
echo ""

# Check Maven
echo "🔍 Checking Maven installation..."
if [ ! -f "$SCRIPT_DIR/mvnw" ]; then
    echo "❌ Error: Maven wrapper not found!"
    exit 1
fi

echo "✅ Maven wrapper found"
echo ""

# Start infrastructure
echo "🐳 Starting infrastructure (PostgreSQL, MinIO, Redis)..."
cd "$PROJECT_ROOT/infrastructure"

if [ ! -f "docker-compose.dev.yml" ]; then
    echo "❌ Error: Infrastructure not found!"
    echo "Please check ../infrastructure directory"
    exit 1
fi

# Check if infrastructure is already running
if docker-compose -f docker-compose.dev.yml ps | grep -q "Up"; then
    echo "ℹ️  Infrastructure already running"
else
    ./start-dev.sh
fi

echo ""

# Setup environment variables for development
echo "🔐 Setting up environment configuration..."
cd "$SCRIPT_DIR"

if [ ! -f ".env" ]; then
    echo "ℹ️  Creating .env from .env.example..."
    cp .env.example .env
    # Set dev profile
    sed -i '' 's/SPRING_PROFILES_ACTIVE=dev/SPRING_PROFILES_ACTIVE=dev/' .env
    echo "✅ .env file created - using 'dev' profile with safe defaults"
    echo "   To customize, edit .env and set your own values"
else
    echo "✅ .env file already exists"
fi

echo ""

# Install dependencies
echo "📦 Installing backend dependencies..."
./mvnw clean install -DskipTests

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ Backend setup complete!"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📚 Next steps:"
echo ""
echo "1️⃣  Start backend (dev profile automatically active via .env):"
echo "   ./mvnw spring-boot:run"
echo ""
echo "2️⃣  Access backend:"
echo "   http://localhost:8081/internal/health"
echo ""
echo "3️⃣  Check database:"
echo "   docker exec -it tax-dividend-postgres-dev psql -U postgres -d taxdividend_dev"
echo "   # Run: SELECT * FROM flyway_schema_history;"
echo ""
echo "4️⃣  Run tests:"
echo "   ./mvnw test"
echo ""
echo "💡 Useful commands:"
echo "   - Clean build:   ./mvnw clean package"
echo "   - Run tests:     ./mvnw test"
echo "   - Integration:   ./mvnw verify"
echo "   - View logs:     tail -f logs/application.log"
echo ""
