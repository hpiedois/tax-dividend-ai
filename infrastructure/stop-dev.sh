#!/bin/bash

# ============================================================================
# Stop Development Environment - Tax Dividend AI
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "🛑 Stopping Tax Dividend AI - Development Environment"
echo "=================================================="

docker-compose -f docker-compose.dev.yml --env-file .env.dev down

echo ""
echo "✅ Development environment stopped!"
echo ""
echo "💡 Note: Data volumes are preserved."
echo "   To remove all data: docker-compose -f docker-compose.dev.yml down -v"
echo ""
