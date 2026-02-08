# ⚠️ DEPRECATED - Old Docker Compose Setup

## 🚨 This directory contains the old Docker Compose configuration

**Status:** Deprecated as of 2026-02-08

---

## 📦 Old Files (Keep for reference only)

- `docker-compose.dev.yml` - Old dev setup (DO NOT USE)
- `docker-compose.yml` - Old main setup (DO NOT USE)
- `docker-compose.prod.yml` - Old prod setup (DO NOT USE)
- `docker-compose.uat.yml` - Old UAT setup (DO NOT USE)

---

## ✅ New Setup (USE THIS)

The project now uses a **cleaner architecture** with separation of concerns:

```
../docker-compose.infra.yml   ← Infrastructure (PostgreSQL, MinIO, Keycloak, etc.)
../docker-compose.apps.yml    ← Applications (Backend, BFF, Frontend)
```

---

## 🔄 Migration

### Quick Migration
```bash
cd ..
./migrate-to-new-docker-setup.sh
```

### Manual Migration
```bash
# 1. Stop old setup
cd infrastructure
docker-compose -f docker-compose.dev.yml down

# 2. Start new setup
cd ..
make infra-up

# 3. Read documentation
cat DOCKER_SETUP.md
```

---

## 📚 Documentation

- **Setup Guide:** `../DOCKER_SETUP.md`
- **Migration Notes:** `../DOCKER_MIGRATION_NOTES.md`
- **Commands:** Run `make help` from root

---

## 🗑️ Can I Delete These Files?

**Not yet!** Keep them for a few weeks in case you need to reference the old configuration.

After everyone has migrated and is comfortable with the new setup, these files can be safely removed.

---

## ❓ Questions?

Read the migration documentation or run:
```bash
cd ..
make help
```
