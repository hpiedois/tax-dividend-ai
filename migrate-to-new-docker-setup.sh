#!/bin/bash
# ============================================================================
# Migration Script - New Docker Architecture
# ============================================================================
# Purpose: Migrate from old docker-compose setup to new infra/apps separation
# Usage: ./migrate-to-new-docker-setup.sh
# ============================================================================

set -e

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔═══════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║     Tax Dividend AI - Docker Migration Script            ║${NC}"
echo -e "${BLUE}║     From: infrastructure/docker-compose.dev.yml           ║${NC}"
echo -e "${BLUE}║     To:   docker-compose.infra.yml + apps.yml             ║${NC}"
echo -e "${BLUE}╚═══════════════════════════════════════════════════════════╝${NC}"
echo ""

# ============================================================================
# 1. Stop old containers
# ============================================================================

echo -e "${YELLOW}📦 Step 1/5: Stopping old containers...${NC}"
echo ""

OLD_CONTAINERS=(
    "tax-dividend-postgres-dev"
    "tax-dividend-postgres-1"
    "tax-dividend-ai-postgres-1"
    "tax-dividend-keycloak"
    "tax-dividend-keycloak-db"
    "tax-dividend-minio-dev"
    "tax-dividend-minio-client-dev"
    "tax-dividend-redis-dev"
    "tax-dividend-mailhog"
    "tax-dividend-grafana"
    "tax-dividend-prometheus"
    "tax-dividend-loki"
    "tax-dividend-tempo"
    "tax-dividend-promtail"
    "tax-dividend-blackbox"
)

for container in "${OLD_CONTAINERS[@]}"; do
    if docker ps -a --format '{{.Names}}' | grep -q "^${container}$"; then
        echo -e "   🛑 Stopping ${container}..."
        docker stop "${container}" 2>/dev/null || true
        docker rm "${container}" 2>/dev/null || true
    fi
done

# Stop via old docker-compose files
cd infrastructure 2>/dev/null && docker-compose -f docker-compose.dev.yml down 2>/dev/null || true
cd .. 2>/dev/null

echo -e "${GREEN}   ✅ Old containers stopped${NC}"
echo ""

# ============================================================================
# 2. Remove old networks
# ============================================================================

echo -e "${YELLOW}🌐 Step 2/5: Cleaning old networks...${NC}"
echo ""

OLD_NETWORKS=(
    "tax-dividend-dev-network"
    "tax-dividend-ai_tax-network"
    "infrastructure_tax-dividend-dev-network"
)

for network in "${OLD_NETWORKS[@]}"; do
    if docker network ls --format '{{.Name}}' | grep -q "^${network}$"; then
        echo -e "   🗑️  Removing network ${network}..."
        docker network rm "${network}" 2>/dev/null || true
    fi
done

echo -e "${GREEN}   ✅ Old networks cleaned${NC}"
echo ""

# ============================================================================
# 3. Optional: Backup old volumes
# ============================================================================

echo -e "${YELLOW}💾 Step 3/5: Handling volumes...${NC}"
echo ""
echo -e "${RED}⚠️  Do you want to KEEP your current database data?${NC}"
echo -e "   - ${GREEN}YES${NC}: Volumes will be kept (you can restore manually later)"
echo -e "   - ${RED}NO${NC}:  Volumes will be DELETED (fresh start)"
echo ""
read -p "Keep volumes? (y/N): " -n 1 -r
echo ""

if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${BLUE}   📦 Keeping volumes (no action)${NC}"
    echo -e "${YELLOW}   💡 To restore data, you'll need to manually migrate:${NC}"
    echo -e "      - infrastructure_postgres_dev_data → tax-dividend-postgres-data"
else
    echo -e "${RED}   🗑️  Removing old volumes...${NC}"

    OLD_VOLUMES=(
        "infrastructure_postgres_dev_data"
        "infrastructure_minio_dev_data"
        "infrastructure_redis_dev_data"
        "infrastructure_grafana_data"
        "tax-dividend-ai_postgres_data"
        "tax-dividend-ai_postgres-data"
    )

    for volume in "${OLD_VOLUMES[@]}"; do
        if docker volume ls --format '{{.Name}}' | grep -q "^${volume}$"; then
            docker volume rm "${volume}" 2>/dev/null || true
        fi
    done

    echo -e "${GREEN}   ✅ Old volumes removed${NC}"
fi

echo ""

# ============================================================================
# 4. Create .env file if it doesn't exist
# ============================================================================

echo -e "${YELLOW}⚙️  Step 4/5: Setting up configuration...${NC}"
echo ""

if [ ! -f .env ]; then
    echo -e "   📝 Creating .env from .env.example..."
    cp .env.example .env
    echo -e "${GREEN}   ✅ .env created${NC}"
    echo -e "${YELLOW}   💡 Please review .env and adjust if needed${NC}"
else
    echo -e "${BLUE}   ℹ️  .env already exists (keeping current)${NC}"
fi

echo ""

# ============================================================================
# 5. Start new infrastructure
# ============================================================================

echo -e "${YELLOW}🚀 Step 5/5: Starting new infrastructure...${NC}"
echo ""

make infra-up

echo ""
echo -e "${GREEN}╔═══════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║                 ✅ Migration Complete!                    ║${NC}"
echo -e "${GREEN}╚═══════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${BLUE}📋 Next Steps:${NC}"
echo ""
echo -e "   1️⃣  Start Backend in VS Code (F5)"
echo -e "      ${YELLOW}→ Flyway will apply migrations${NC}"
echo ""
echo -e "   2️⃣  Seed development data:"
echo -e "      ${YELLOW}→ make seed-dev${NC}"
echo ""
echo -e "   3️⃣  Start Frontend:"
echo -e "      ${YELLOW}→ cd frontend && npm run dev${NC}"
echo ""
echo -e "${BLUE}📚 Documentation:${NC}"
echo -e "   - Read ${YELLOW}DOCKER_SETUP.md${NC} for full details"
echo -e "   - Run ${YELLOW}make help${NC} to see all commands"
echo ""
echo -e "${GREEN}✨ Happy coding!${NC}"
echo ""
