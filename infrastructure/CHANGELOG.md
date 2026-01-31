# Changelog - Infrastructure

All notable changes to the Tax Dividend AI infrastructure.

## [2.0.1] - 2026-01-28

### Removed
- **pgAdmin** service from all environments (dev, UAT, prod)
  - Use `psql` via Docker exec instead: `docker exec -it tax-dividend-postgres-dev psql -U postgres -d taxdividend_dev`
  - Or use local database clients (DBeaver, DataGrip, TablePlus, etc.)
- Removed pgAdmin configuration from `.env.dev`, `.env.uat`, `.env.example`
- Removed pgAdmin documentation from all guides

## [2.0.0] - 2026-01-28

### 🎉 Major Release - Multi-Environment Support

#### Added
- **Multi-environment architecture** supporting Dev, UAT, and Production
- **docker-compose.dev.yml** - Development environment with full admin tools
- **docker-compose.uat.yml** - UAT/staging environment with production-like config
- **docker-compose.prod.yml** - Production template (recommends managed services)
- **Environment files:**
  - `.env.dev` - Development configuration (committed)
  - `.env.uat` - UAT configuration (committed, change passwords!)
  - `.env.prod.example` - Production template (never commit .env.prod!)
- **Helper scripts:**
  - `setup.sh` - First-time infrastructure setup
  - `start-dev.sh` / `stop-dev.sh` - Development lifecycle
  - `start-uat.sh` / `stop-uat.sh` - UAT lifecycle
  - `backup-db.sh` - Database backup (all environments)
  - `restore-db.sh` - Database restore (all environments)
- **Documentation:**
  - `README.md` - Complete infrastructure guide (updated)
  - `QUICKSTART.md` - Fast onboarding guide
  - `ENVIRONMENTS.md` - Multi-environment architecture explained
  - `PORTS.md` - Port allocation reference
  - `CHANGELOG.md` - This file
- **Infrastructure:**
  - Redis service (dev, UAT, prod)
  - Automated backup directories
  - Health checks for all services
  - Proper logging configuration
  - Resource limits (production)
  - Docker profiles for optional services (UAT pgAdmin)

#### Changed
- **PostgreSQL**: Different databases per environment (`taxdividend_dev`, `taxdividend_uat`, `taxdividend`)
- **MinIO**: Environment-specific buckets with versioning (UAT/prod)
- **Ports**: UAT uses different ports to allow parallel dev+UAT operation
- **Security**: Production ports bound to localhost only
- **Credentials**: Environment-specific passwords

#### Deprecated
- `docker-compose.yml` (now points to dev by default with deprecation warning)
- Single-environment setup (use environment-specific files)

#### Migration Guide

**From v1.x to v2.0:**

```bash
# 1. Backup your current database (if any)
docker exec tax-dividend-postgres pg_dump -U postgres -d taxdividend > backup.sql

# 2. Stop old containers
docker-compose down

# 3. Run new setup
./setup.sh

# 4. Start development environment
./start-dev.sh

# 5. Restore data (if needed)
gunzip -c backup.sql | docker exec -i tax-dividend-postgres-dev psql -U postgres -d taxdividend_dev
```

---

## [1.0.0] - 2026-01-27

### Initial Release

#### Added
- Single `docker-compose.yml` for development
- PostgreSQL 16 with extensions
- MinIO S3-compatible storage
- pgAdmin for database management
- Infrastructure migrations (extensions, schemas, roles)
- Basic documentation

#### Services
- PostgreSQL on port 5432
- MinIO on ports 9000-9001
- pgAdmin on port 5050

---

## Future Plans

### [2.1.0] - Planned
- [ ] Automated database backup cron jobs
- [ ] Monitoring stack (Prometheus + Grafana)
- [ ] Log aggregation (ELK stack)
- [ ] Health check dashboard
- [ ] Performance tuning profiles

### [3.0.0] - Planned
- [ ] Kubernetes deployment manifests
- [ ] Helm charts for cloud deployment
- [ ] GitOps integration (ArgoCD/Flux)
- [ ] Multi-region support
- [ ] Disaster recovery automation

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 2.0.0 | 2026-01-28 | Multi-environment support, Redis, helper scripts |
| 1.0.0 | 2026-01-27 | Initial release with basic Docker Compose setup |

---

## Breaking Changes

### v2.0.0

**Container Names Changed:**
- `tax-dividend-postgres` → `tax-dividend-postgres-dev` (dev)
- `tax-dividend-minio` → `tax-dividend-minio-dev` (dev)
- `tax-dividend-pgadmin` → `tax-dividend-pgadmin-dev` (dev)

**Database Names Changed:**
- `taxdividend` → `taxdividend_dev` (development)
- New: `taxdividend_uat` (UAT)
- Kept: `taxdividend` (production)

**Command Changes:**
- `docker-compose up` → `./start-dev.sh` (recommended)
- `docker-compose down` → `./stop-dev.sh` (recommended)

**Network Names Changed:**
- `tax-dividend-network` → `tax-dividend-dev-network` (dev)
- New: `tax-dividend-uat-network` (UAT)
- New: `tax-dividend-prod-network` (production)

---

## Support

For issues or questions about this infrastructure:
1. Check `README.md` - Troubleshooting section
2. Review `ENVIRONMENTS.md` - Architecture guide
3. See `QUICKSTART.md` - Common tasks
