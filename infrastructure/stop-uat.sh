#!/bin/bash

# ============================================================================
# Stop UAT Environment - Tax Dividend AI
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "🛑 Stopping Tax Dividend AI - UAT Environment"
echo "=================================================="

docker-compose -p tax-dividend-infra -f docker-compose.uat.yml --env-file .env.uat down

echo ""
echo "✅ UAT environment stopped!"
echo ""
echo "💡 Note: Data volumes are preserved."
echo "   To remove all data: docker-compose -p tax-dividend-infra -f docker-compose.uat.yml down -v"
echo ""
