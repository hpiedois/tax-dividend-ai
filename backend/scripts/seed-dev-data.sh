#!/bin/bash

# ============================================================================
# Seed Dev Data - Tax Dividend AI Backend
# ============================================================================
# Purpose: Load test user and sample dividends into PostgreSQL
# Usage: ./backend/scripts/seed-dev-data.sh
# ============================================================================

set -e  # Exit on error

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${GREEN}🌱 Seeding dev data into PostgreSQL...${NC}"
echo ""

# Check if PostgreSQL container is running
if ! docker ps | grep -q tax-dividend-postgres; then
    echo -e "${RED}❌ PostgreSQL container not running${NC}"
    echo -e "${YELLOW}   Run: make infra-up${NC}"
    exit 1
fi

# Execute seed script
docker exec -i tax-dividend-postgres psql -U taxdividend_user -d taxdividend_dev < backend/scripts/sql/seed_dev_data.sql

echo ""
echo -e "${GREEN}✅ Dev data seeded successfully!${NC}"
echo ""
echo -e "${YELLOW}📧 Test User:${NC}"
echo -e "   Email: jdoe@dummy.com"
echo -e "   Password: Test1234! (in Keycloak)"
echo -e "   User ID: 4e012f70-3846-4941-b95f-5b98c70a235d"
echo ""
