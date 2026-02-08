# Docker Architecture Migration Notes

## 🔄 What Changed?

### Old Structure ❌
```
infrastructure/
  └── docker-compose.dev.yml        # Everything mixed together
docker-compose.yml                   # Root compose for prod
```

**Problems:**
- Infrastructure + apps in same file
- Duplication between dev/prod configs
- Can't run infra standalone
- Port conflicts (Keycloak on 8080 vs 8180)
- Inconsistent credentials between files

---

### New Structure ✅
```
docker-compose.infra.yml   # Infrastructure only (always running)
docker-compose.apps.yml    # Applications (optional for deployment)
.env.example               # Documented environment variables
DOCKER_SETUP.md           # Complete documentation
```

**Benefits:**
- ✅ Clear separation: infra stays up, apps in IDE
- ✅ No duplication: single source of truth
- ✅ Flexible: run infra only, or infra + apps
- ✅ Consistent ports and credentials
- ✅ Better for dev workflow

---

## 🔧 Key Changes

### 1. Container Names
| Old | New |
|-----|-----|
| `tax-dividend-postgres-dev` | `tax-dividend-postgres` |
| `tax-dividend-minio-dev` | `tax-dividend-minio` |
| `tax-dividend-keycloak` | `tax-dividend-keycloak` (same) |

### 2. Volume Names
| Old | New |
|-----|-----|
| `infrastructure_postgres_dev_data` | `tax-dividend-postgres-data` |
| `infrastructure_minio_dev_data` | `tax-dividend-minio-data` |

### 3. Network
| Old | New |
|-----|-----|
| `tax-dividend-dev-network` | `tax-dividend-network` |
| `tax-dividend-ai_tax-network` | (removed, using single network) |

### 4. Ports (Standardized)
| Service | Port | Notes |
|---------|------|-------|
| PostgreSQL | 5432 | No change |
| Keycloak | 8080 | ✅ Now consistent everywhere |
| MinIO API | 9000 | No change |
| MinIO Console | 9001 | No change |
| Backend | 8081 | No change |
| BFF | 8080 | ⚠️ Conflicts with Keycloak if apps running |
| Frontend | 5173 | No change |

### 5. Credentials (Standardized)
**PostgreSQL:**
- DB: `taxdividend_dev` (consistent)
- User: `taxdividend_user` (consistent)
- Password: `dev_password_123` (consistent)

**Keycloak:**
- Admin: `admin` / `admin` (consistent)
- Port: `8080` (was 8180 in some places)

---

## 📋 Migration Steps

### Automated (Recommended)
```bash
./migrate-to-new-docker-setup.sh
```

### Manual
```bash
# 1. Stop old setup
cd infrastructure
docker-compose -f docker-compose.dev.yml down

# 2. Remove old containers/volumes
docker stop tax-dividend-postgres-dev tax-dividend-keycloak || true
docker rm tax-dividend-postgres-dev tax-dividend-keycloak || true
docker volume rm infrastructure_postgres_dev_data || true

# 3. Start new setup
cd ..
make infra-up

# 4. Reset DB & seed
make reset-db
# (restart backend in VS Code)
make seed-dev
```

---

## 🛠️ New Makefile Commands

### Before
```bash
make seed-dev       # Only command available
make reset-db       # Had issues with wrong container names
```

### After
```bash
# Infrastructure
make infra-up       # Start infra (PostgreSQL, MinIO, etc.)
make infra-down     # Stop infra
make infra-logs     # Follow logs

# Applications (deployment)
make apps-up        # Start apps in Docker
make apps-down      # Stop apps

# All-in-one
make up             # Start everything
make down           # Stop everything
make logs           # Follow all logs

# Database
make reset-db       # Reset PostgreSQL ✅ now works correctly
make seed-dev       # Seed test data

# Monitoring
make status         # Show running containers
make ps             # Show all containers

# Cleanup
make clean          # Remove everything (containers + volumes)
```

---

## 🔍 Configuration Files Updated

### Backend
- ✅ Created `application-docker.yml` (was missing)
- ✅ Fixed database connection for docker profile
- ✅ Standardized Keycloak URLs

### BFF
- ✅ Updated Keycloak port to 8080 (was using 8180)
- ✅ Fixed docker profile configuration

### Seed Script
- ✅ Updated container name: `tax-dividend-postgres-dev` → `tax-dividend-postgres`
- ✅ Updated command: `docker-compose up -d postgres` → `make infra-up`

### PostgreSQL Init
- ✅ Created `infrastructure/postgres/init/01-create-app-user.sh`
- ✅ Auto-creates `taxdividend_user` with correct permissions
- ✅ Creates `taxdividend` schema automatically

---

## ⚠️ Breaking Changes

### 1. Container Names
If you have scripts that reference old container names, update them:
```bash
# Old
docker exec -i tax-dividend-postgres-dev psql ...

# New
docker exec -i tax-dividend-postgres psql ...
```

### 2. Docker Compose Files
Old commands won't work:
```bash
# ❌ Old
cd infrastructure
docker-compose -f docker-compose.dev.yml up

# ✅ New
make infra-up
```

### 3. Volume Paths
If you backup/restore volumes, note the new names.

### 4. Network Names
If you have external services connecting, update to `tax-dividend-network`.

---

## 🎯 Development Workflow

### Old Workflow ❌
```bash
cd infrastructure
docker-compose -f docker-compose.dev.yml up -d
# (wait for everything)
# (start backend in IDE)
# (ports might conflict)
```

### New Workflow ✅
```bash
make infra-up           # Once per day
# (start backend in IDE immediately)
# (frontend: npm run dev)
# (infra keeps running, fast dev cycles)
```

---

## 🐛 Troubleshooting

### Port 8080 already in use
If BFF and Keycloak both use 8080:
- **Dev mode**: Apps run in IDE (no port conflict, BFF uses 8080 in IDE)
- **Docker mode**: Don't run apps + Keycloak on same host, or change BFF port

### Database connection refused
```bash
# Check container is running
docker ps | grep postgres

# Check connection
docker exec tax-dividend-postgres pg_isready -U postgres
```

### Old volumes interfering
```bash
# Clean everything
make clean

# Start fresh
make infra-up
make reset-db
```

### Flyway migration issues
```bash
# If V1 has wrong checksum, reset:
make reset-db
# (restart backend to re-apply migrations)
```

---

## 📚 Documentation Added

- ✅ `DOCKER_SETUP.md` - Complete guide
- ✅ `.env.example` - All variables documented
- ✅ `migrate-to-new-docker-setup.sh` - Automated migration
- ✅ `DOCKER_MIGRATION_NOTES.md` - This file
- ✅ `infrastructure/postgres/init/01-create-app-user.sh` - Auto-creates DB user

---

## ✅ Testing Checklist

After migration, verify:

- [ ] `make infra-up` starts all infrastructure
- [ ] PostgreSQL accessible on localhost:5432
- [ ] Keycloak accessible on localhost:8080
- [ ] Backend starts in VS Code without errors
- [ ] Flyway applies migrations successfully
- [ ] `make seed-dev` works correctly
- [ ] Test user `jdoe@dummy.com` can login
- [ ] API calls return data (not 400 errors)
- [ ] Frontend connects to BFF successfully

---

## 🚀 Next Steps

1. ✅ Run migration: `./migrate-to-new-docker-setup.sh`
2. ✅ Read `DOCKER_SETUP.md` for full documentation
3. ✅ Update any CI/CD pipelines to use new commands
4. ✅ Update team documentation/onboarding guides
5. ✅ Consider removing `infrastructure/docker-compose.dev.yml` (deprecated)

---

## 📞 Support

If you encounter issues:
1. Check `DOCKER_SETUP.md` troubleshooting section
2. Run `make status` to see what's running
3. Check logs: `make infra-logs` or `make logs`
4. Clean slate: `make clean && make infra-up`

---

Date: 2026-02-08
Version: 2.0 (Infrastructure/Apps Separation)
