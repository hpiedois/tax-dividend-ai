.PHONY: help infra-up infra-down apps-up apps-down up down reset-db seed-dev logs status clean

# ============================================================================
# Tax Dividend AI - Makefile
# ============================================================================
# Docker Compose orchestration with environment support
# Usage:
#   make infra-up             # Dev (default)
#   make infra-up ENV=prod    # Production
# ============================================================================

# Default environment
ENV ?= dev

# Environment-specific files
ifeq ($(ENV),prod)
    ENV_FILE = .env.prod
    INFRA_FILES = -f docker-compose.infra.yml -f docker-compose.infra.prod.yml
    APPS_FILES = -f docker-compose.apps.yml -f docker-compose.apps.prod.yml
    ENV_LABEL = Production
else
    ENV_FILE = .env.dev
    INFRA_FILES = -f docker-compose.infra.yml -f docker-compose.infra.dev.yml
    APPS_FILES = -f docker-compose.apps.yml -f docker-compose.apps.dev.yml
    ENV_LABEL = Development
endif

# Docker Compose command with env file
COMPOSE_INFRA = docker-compose --env-file $(ENV_FILE) $(INFRA_FILES)
COMPOSE_APPS = docker-compose --env-file $(ENV_FILE) $(APPS_FILES)
COMPOSE_ALL = docker-compose --env-file $(ENV_FILE) $(INFRA_FILES) $(APPS_FILES)

# ============================================================================
# HELP
# ============================================================================

help:
	@echo "═══════════════════════════════════════════════════════════════"
	@echo "Tax Dividend AI - Development Commands"
	@echo "═══════════════════════════════════════════════════════════════"
	@echo ""
	@echo "🏗️  INFRASTRUCTURE:"
	@echo "  make infra-up          - Start infrastructure (dev)"
	@echo "  make infra-up ENV=prod - Start infrastructure (production)"
	@echo "  make infra-down        - Stop infrastructure"
	@echo "  make infra-logs        - Follow infrastructure logs"
	@echo "  make apply-theme       - Apply Keycloak custom theme"
	@echo ""
	@echo "🚀 APPLICATIONS:"
	@echo "  make apps-up           - Start applications (dev)"
	@echo "  make apps-up ENV=prod  - Start applications (production)"
	@echo "  make apps-down         - Stop applications"
	@echo "  make apps-logs         - Follow application logs"
	@echo ""
	@echo "🔄 ALL-IN-ONE:"
	@echo "  make up                - Start everything (dev)"
	@echo "  make up ENV=prod       - Start everything (production)"
	@echo "  make down              - Stop everything"
	@echo "  make logs              - Follow all logs"
	@echo ""
	@echo "🗃️  DATABASE:"
	@echo "  make reset-db          - Reset PostgreSQL (⚠️  destroys data!)"
	@echo "  make seed-dev          - Seed development data"
	@echo ""
	@echo "📊 MONITORING:"
	@echo "  make status            - Show running containers"
	@echo "  make ps                - Show all containers"
	@echo ""
	@echo "🧹 CLEANUP:"
	@echo "  make clean             - Remove all containers and volumes (⚠️)"
	@echo ""
	@echo "═══════════════════════════════════════════════════════════════"
	@echo "💡 Typical Dev Workflow:"
	@echo "   1. make infra-up             # Start infrastructure"
	@echo "   2. Start Backend in VS Code (F5)"
	@echo "   3. make seed-dev             # Seed test data"
	@echo "   4. cd frontend && npm run dev"
	@echo "═══════════════════════════════════════════════════════════════"
	@echo ""
	@echo "📦 Current Environment: $(ENV_LABEL)"
	@echo "📄 Using: $(ENV_FILE)"
	@echo ""

# ============================================================================
# INFRASTRUCTURE
# ============================================================================

infra-up:
	@echo "🚀 Starting infrastructure ($(ENV_LABEL))..."
	@if [ ! -f $(ENV_FILE) ]; then \
		echo "❌ Error: $(ENV_FILE) not found!"; \
		echo "   Copy from .env.example: cp .env.example $(ENV_FILE)"; \
		exit 1; \
	fi
	@$(COMPOSE_INFRA) up -d
	@echo ""
	@echo "✅ Infrastructure started ($(ENV_LABEL))!"
	@echo ""
	@if [ "$(ENV)" = "dev" ]; then \
		echo "📋 Services Available:"; \
		echo "   🗄️  PostgreSQL:       localhost:5432"; \
		echo "   📦 MinIO API:         localhost:9000"; \
		echo "   🎛️  MinIO Console:    localhost:9001"; \
		echo "   🔐 Keycloak:          localhost:8180"; \
		echo "   📧 MailHog:           localhost:8025"; \
		echo "   🗂️  Redis:            localhost:6379"; \
		echo "   📊 Grafana:           localhost:3000"; \
		echo "   📈 Prometheus:        localhost:9090"; \
		echo ""; \
		echo "💡 Next: Start backend in VS Code, then 'make seed-dev'"; \
	else \
		echo "📋 Infrastructure running in production mode"; \
		echo "   No ports exposed directly (use reverse proxy)"; \
	fi

infra-down:
	@echo "🛑 Stopping infrastructure..."
	@$(COMPOSE_INFRA) down
	@echo "✅ Infrastructure stopped"

infra-logs:
	@$(COMPOSE_INFRA) logs -f

# Apply Keycloak theme
apply-theme:
	@echo "🎨 Applying Keycloak theme..."
	@./infrastructure/keycloak/apply-theme.sh

# ============================================================================
# APPLICATIONS
# ============================================================================

apps-up:
	@echo "🚀 Starting applications ($(ENV_LABEL))..."
	@if [ ! -f $(ENV_FILE) ]; then \
		echo "❌ Error: $(ENV_FILE) not found!"; \
		exit 1; \
	fi
	@$(COMPOSE_APPS) up -d
	@echo ""
	@echo "✅ Applications started ($(ENV_LABEL))!"
	@echo ""
	@if [ "$(ENV)" = "dev" ]; then \
		echo "📋 Services Available:"; \
		echo "   🔹 Backend:    localhost:8081"; \
		echo "   🔹 BFF:        localhost:8080"; \
		echo "   🔹 Frontend:   localhost:5173"; \
	fi

apps-down:
	@echo "🛑 Stopping applications..."
	@$(COMPOSE_APPS) down
	@echo "✅ Applications stopped"

apps-logs:
	@$(COMPOSE_APPS) logs -f

# ============================================================================
# ALL-IN-ONE
# ============================================================================

up:
	@echo "🚀 Starting everything ($(ENV_LABEL))..."
	@if [ ! -f $(ENV_FILE) ]; then \
		echo "❌ Error: $(ENV_FILE) not found!"; \
		exit 1; \
	fi
	@$(COMPOSE_ALL) up -d
	@echo ""
	@echo "✅ All services started ($(ENV_LABEL))!"

down:
	@echo "🛑 Stopping everything..."
	@$(COMPOSE_ALL) down
	@echo "✅ All services stopped"

logs:
	@$(COMPOSE_ALL) logs -f

# ============================================================================
# DATABASE
# ============================================================================

reset-db:
	@echo "⚠️  Resetting PostgreSQL ($(ENV_LABEL))..."
	@echo ""
	@$(COMPOSE_INFRA) stop postgres
	@$(COMPOSE_INFRA) rm -f postgres
	@docker volume rm tax-dividend-postgres-data || true
	@$(COMPOSE_INFRA) up -d postgres
	@echo "⏳ Waiting for PostgreSQL..."
	@sleep 10
	@echo ""
	@echo "✅ Database reset complete!"
	@echo ""
	@echo "📋 Next steps:"
	@echo "   1. Restart backend in VS Code (Flyway migrations)"
	@echo "   2. make seed-dev"

seed-dev:
	@echo "🌱 Seeding development data..."
	@./backend/scripts/seed-dev-data.sh

# ============================================================================
# MONITORING
# ============================================================================

status:
	@echo "📊 Running containers ($(ENV_LABEL)):"
	@$(COMPOSE_ALL) ps

ps:
	@echo "📊 All containers ($(ENV_LABEL)):"
	@$(COMPOSE_ALL) ps -a

# ============================================================================
# CLEANUP
# ============================================================================

clean:
	@echo "⚠️  WARNING: Remove ALL containers and volumes?"
	@echo "⚠️  Press Ctrl+C to cancel, or wait 5 seconds..."
	@sleep 5
	@echo ""
	@echo "🧹 Cleaning up..."
	@$(COMPOSE_ALL) down -v
	@docker network rm tax-dividend-network 2>/dev/null || true
	@echo "✅ Cleanup complete!"
