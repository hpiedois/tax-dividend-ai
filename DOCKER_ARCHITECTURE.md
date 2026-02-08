# Docker Architecture - Tax Dividend AI

## 📐 Clean Architecture (Option A)

The project uses a **base + overrides** pattern for maximum clarity and zero duplication.

```
docker-compose.infra.yml          # Base infrastructure (all envs)
docker-compose.infra.dev.yml      # Dev overrides (ports exposed, debug)
docker-compose.infra.prod.yml     # Prod overrides (resources, no ports)

docker-compose.apps.yml           # Base applications
docker-compose.apps.dev.yml       # Dev overrides (debug, HMR)
docker-compose.apps.prod.yml      # Prod overrides (replicas, resources)

.env.dev                          # Dev environment variables
.env.prod                         # Prod environment variables (secrets!)
```

---

## 🎯 Design Principles

### 1. Base Files (Portable)
- **No environment-specific config**
- **No ports exposed** (added in overrides)
- **No default values** for sensitive data
- Pure service definitions

### 2. Override Files (Environment-specific)
- **Dev overrides**: Expose all ports, enable debug, anonymous Grafana
- **Prod overrides**: No ports (reverse proxy), resources, replicas, security

### 3. Environment Files
- **`.env.dev`**: Safe defaults, can be committed
- **`.env.prod`**: Secrets, **NEVER commit**

---

## 🚀 Usage

### Development (Default)

```bash
# Start infrastructure only (apps in IDE)
make infra-up

# Or start everything in Docker
make up

# Internally runs:
# docker-compose --env-file .env.dev \
#   -f docker-compose.infra.yml \
#   -f docker-compose.infra.dev.yml up
```

### Production

```bash
# Start infrastructure
make infra-up ENV=prod

# Start applications
make apps-up ENV=prod

# Or start everything
make up ENV=prod

# Internally runs:
# docker-compose --env-file .env.prod \
#   -f docker-compose.infra.yml \
#   -f docker-compose.infra.prod.yml \
#   -f docker-compose.apps.yml \
#   -f docker-compose.apps.prod.yml up
```

---

## 📋 Available Commands

```bash
# Infrastructure
make infra-up              # Dev
make infra-up ENV=prod     # Prod
make infra-down
make infra-logs

# Applications
make apps-up               # Dev
make apps-up ENV=prod      # Prod
make apps-down
make apps-logs

# All-in-one
make up                    # Dev
make up ENV=prod           # Prod
make down
make logs

# Database
make reset-db
make seed-dev

# Monitoring
make status
make ps

# Cleanup
make clean
```

---

## 🔧 What Gets Overridden?

### Dev Overrides

**Infrastructure:**
- ✅ All ports exposed (PostgreSQL, MinIO, Keycloak, etc.)
- ✅ Keycloak: `start-dev` mode with `--import-realm`
- ✅ Grafana: Anonymous access enabled
- ✅ MailHog: Active (for email testing)
- ✅ Less strict healthchecks

**Applications:**
- ✅ All ports exposed (Backend, BFF, Frontend)
- ✅ Debug ports enabled (5005, 5006)
- ✅ Source volumes mounted (HMR)
- ✅ `SPRING_PROFILES_ACTIVE=dev,docker`

### Prod Overrides

**Infrastructure:**
- ❌ No ports exposed (use reverse proxy)
- ✅ Keycloak: `start` production mode
- ✅ Grafana: Authentication required
- ❌ MailHog: Disabled (replicas: 0)
- ✅ Resource limits + reservations
- ✅ Stricter healthchecks

**Applications:**
- ❌ No ports exposed
- ❌ No debug ports
- ❌ No volume mounts
- ✅ `SPRING_PROFILES_ACTIVE=prod,docker`
- ✅ Multiple replicas (backend: 2, bff: 2, frontend: 2)
- ✅ JVM tuning (Xms/Xmx)
- ✅ Reduced tracing sampling (10%)

---

## 🌐 Exposed Ports

### Development

| Service | Port | Access |
|---------|------|--------|
| PostgreSQL | 5432 | localhost |
| MinIO API | 9000 | localhost |
| MinIO Console | 9001 | localhost |
| Keycloak | 8180 | localhost |
| Redis | 6379 | localhost |
| MailHog | 8025 | localhost |
| Grafana | 3000 | localhost |
| Prometheus | 9090 | localhost |
| Loki | 3100 | localhost |
| Tempo UI | 3200 | localhost |
| Tempo OTLP | 4317, 4318 | localhost |
| Backend | 8081 | localhost (if apps in Docker) |
| BFF | 8080 | localhost (if apps in Docker) |
| Frontend | 5173 | localhost (if apps in Docker) |

### Production

| Service | Port | Access |
|---------|------|--------|
| All services | - | **Internal network only** |
| Tempo OTLP | 4317, 4318 | For app tracing |

**Note**: In production, use a reverse proxy (Nginx, Traefik, Caddy) to expose services.

---

## 📝 Environment Variables

### .env.dev (Safe to commit)

```bash
POSTGRES_DB=taxdividend_dev
POSTGRES_USER=postgres
POSTGRES_PASSWORD=dev_password
TAXDIVIDEND_DB_USER=taxdividend_user
TAXDIVIDEND_DB_PASSWORD=dev_password_123

MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=minioadmin123

KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin
KEYCLOAK_DB_PASSWORD=keycloak_dev

INTERNAL_API_KEY=dev-internal-api-key-for-local-testing-only-min-32-chars
ACTUATOR_USERNAME=admin
ACTUATOR_PASSWORD=admin
```

### .env.prod (NEVER commit!)

```bash
# ⚠️ IMPORTANT: Use strong passwords!
POSTGRES_DB=taxdividend_prod
POSTGRES_PASSWORD=CHANGE_ME_STRONG_PASSWORD
TAXDIVIDEND_DB_PASSWORD=CHANGE_ME_STRONG_PASSWORD

MINIO_ROOT_USER=CHANGE_ME
MINIO_ROOT_PASSWORD=CHANGE_ME_STRONG_PASSWORD

KEYCLOAK_ADMIN_PASSWORD=CHANGE_ME_STRONG_PASSWORD
KEYCLOAK_DB_PASSWORD=CHANGE_ME_STRONG_PASSWORD
KEYCLOAK_HOSTNAME=auth.yourdomain.com

INTERNAL_API_KEY=CHANGE_ME_RANDOM_STRING_MIN_32_CHARS
ACTUATOR_PASSWORD=CHANGE_ME_STRONG_PASSWORD

GRAFANA_ADMIN_PASSWORD=CHANGE_ME_STRONG_PASSWORD
GRAFANA_ROOT_URL=https://grafana.yourdomain.com
```

---

## ✅ Benefits of This Architecture

1. **Zero Duplication**: Base config shared across all environments
2. **Clear Separation**: Dev vs Prod differences are explicit
3. **Security**: Production doesn't expose ports by default
4. **Scalability**: Easy to add new environments (UAT, staging)
5. **Official Pattern**: Docker Compose best practice
6. **Maintainability**: Change base once, applies everywhere

---

## 🆚 vs Old Architecture

### Old (Deprecated)
```
docker-compose.yml                    # Mix of everything
infrastructure/docker-compose.dev.yml # Duplicated config
infrastructure/docker-compose.prod.yml # Duplicated config
```

**Problems:**
- Config duplicated across files
- Unclear which file to use
- Hard to maintain
- Port conflicts

### New (Current)
```
docker-compose.infra.yml + infra.dev.yml      # Clear base + override
docker-compose.apps.yml + apps.dev.yml        # Clear base + override
```

**Benefits:**
- DRY (Don't Repeat Yourself)
- Explicit overrides
- Easy to understand
- Production-ready

---

## 📚 Learn More

- [Docker Compose Multiple Files](https://docs.docker.com/compose/multiple-compose-files/)
- [Production Best Practices](https://docs.docker.com/compose/production/)

---

## 🐛 Troubleshooting

### "File not found: .env.dev"
```bash
cp .env.example .env.dev
```

### "Port already in use"
```bash
# Check what's using the port
lsof -i :8080

# Stop conflicting service
make down
```

### "Can't connect to services"
```bash
# Check if running
make status

# Check logs
make logs
```

---

Made with ❤️ by Tax Dividend AI Team
