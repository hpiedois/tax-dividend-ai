# Multi-Environment Architecture - Tax Dividend AI

## 🎯 Overview

This infrastructure supports three isolated environments that can run **simultaneously** on the same machine.

```
┌─────────────────────────────────────────────────────────────────┐
│                         LOCAL MACHINE                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌────────────────────────────────────────────────────────┐    │
│  │ DEVELOPMENT ENVIRONMENT (taxdividend_dev)              │    │
│  ├────────────────────────────────────────────────────────┤    │
│  │ • PostgreSQL:    localhost:5432                        │    │
│  │ • MinIO API:     localhost:9000                        │    │
│  │ • MinIO Console: localhost:9001                        │    │
│  │ • Redis:         localhost:6379                        │    │
│  │                                                         │    │
│  │ Purpose: Local development, hot-reload, debugging      │    │
│  │ Data:    Mock data, test users                         │    │
│  │ Admin:   All UIs accessible                            │    │
│  └────────────────────────────────────────────────────────┘    │
│                                                                 │
│  ┌────────────────────────────────────────────────────────┐    │
│  │ UAT ENVIRONMENT (taxdividend_uat)                      │    │
│  ├────────────────────────────────────────────────────────┤    │
│  │ • PostgreSQL:    localhost:5433 ← Different port!      │    │
│  │ • MinIO API:     localhost:9002 ← Different port!      │    │
│  │ • MinIO Console: localhost:9003 ← Different port!      │    │
│  │ • Redis:         localhost:6380 ← Different port!      │    │
│  │                                                         │    │
│  │ Purpose: User acceptance testing, pre-production       │    │
│  │ Data:    Realistic test data, client demos             │    │
│  │ Admin:   Restricted access                             │    │
│  └────────────────────────────────────────────────────────┘    │
│                                                                 │
│  ┌────────────────────────────────────────────────────────┐    │
│  │ PRODUCTION ENVIRONMENT (taxdividend) - NOT RECOMMENDED │    │
│  ├────────────────────────────────────────────────────────┤    │
│  │ ⚠️  Use managed cloud services instead!               │    │
│  │                                                         │    │
│  │ If you must use Docker:                                │    │
│  │ • PostgreSQL:    localhost:5432 (localhost only!)      │    │
│  │ • MinIO:         localhost:9000 (localhost only!)      │    │
│  │ • Redis:         localhost:6379 (password-protected)   │    │
│  │                                                         │    │
│  │ Purpose: Production deployment (use cloud instead!)    │    │
│  │ Data:    Real customer data                            │    │
│  │ Admin:   No web UIs, SSH tunnel only                   │    │
│  └────────────────────────────────────────────────────────┘    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## 🔄 Environment Comparison

| Feature | Development | UAT | Production |
|---------|------------|-----|------------|
| **Database Name** | `taxdividend_dev` | `taxdividend_uat` | `taxdividend` |
| **PostgreSQL Port** | 5432 | 5433 | 5432 (localhost) |
| **MinIO Port** | 9000-9001 | 9002-9003 | 9000-9001 (localhost) |
| **Redis Port** | 6379 | 6380 | 6379 (localhost) |
| **Passwords** | Simple defaults | Strong but different | **Very strong + secrets mgmt** |
| **Database UI** | psql / local client | psql / local client | psql only (SSH tunnel) |
| **SSL/TLS** | ❌ Not needed | ⚠️ Optional | ✅ **Required** |
| **Backups** | Manual | Automated daily | **Automated + offsite** |
| **Data** | Mock/test data | Realistic samples | **Real customer data** |
| **Network** | Localhost | Localhost | **Firewall-protected** |
| **Logging** | Basic | Structured | **Centralized (ELK/Splunk)** |
| **Monitoring** | Manual checks | Health checks | **Full APM (Datadog/New Relic)** |
| **Can Run Together?** | ✅ Yes | ✅ Yes | ⚠️ Different machine |

## 🚦 Workflow Scenarios

### Scenario 1: Local Development Only

```bash
cd infrastructure
./start-dev.sh

# Develop your app...

./stop-dev.sh
```

### Scenario 2: Development + UAT Testing (Parallel)

```bash
cd infrastructure

# Start dev environment
./start-dev.sh

# Start UAT environment (different ports)
./start-uat.sh

# Now you have:
# - Dev DB:  localhost:5432
# - UAT DB:  localhost:5433
# Both running simultaneously!

# Stop when done
./stop-dev.sh
./stop-uat.sh
```

### Scenario 3: UAT Testing Before Production

```bash
cd infrastructure

# 1. Start UAT
./start-uat.sh

# 2. Deploy backend to UAT
cd ../backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=uat

# 3. Test with real users

# 4. Create backup before prod
./backup-db.sh uat

# 5. If tests pass, deploy to production
# (Use managed services, not Docker!)
```

### Scenario 4: Data Migration Dev → UAT

```bash
cd infrastructure

# 1. Backup dev database
./backup-db.sh dev

# 2. Restore to UAT (⚠️ replaces all UAT data)
./restore-db.sh uat backups/dev/taxdividend_dev_YYYYMMDD_HHMMSS.sql.gz

# 3. Sanitize sensitive data in UAT
docker exec -it tax-dividend-postgres-uat psql -U postgres -d taxdividend_uat
# UPDATE users SET email = CONCAT('test+', id, '@example.com');
```

## 🗂️ Data Management Strategy

### Development Environment
- **Purpose**: Rapid iteration, schema changes
- **Data**: Minimal test data, frequently reset
- **Backups**: Before major schema changes only
- **Cleanup**: Can be wiped anytime

### UAT Environment
- **Purpose**: Realistic testing, client demos
- **Data**: Production-like structure, sanitized content
- **Backups**: Daily automated backups
- **Cleanup**: Scheduled weekly resets with fresh data

### Production Environment
- **Purpose**: Live customer operations
- **Data**: Real sensitive customer data
- **Backups**: Hourly (incremental) + daily (full)
- **Cleanup**: NEVER! Archive only

## 🔐 Credentials Strategy

### Development (.env.dev)
```bash
# Simple, memorizable (committed to git)
POSTGRES_PASSWORD=dev_password_123
MINIO_ROOT_PASSWORD=minioadmin123
```

### UAT (.env.uat)
```bash
# Strong but not production (committed to git)
POSTGRES_PASSWORD=uat_secure_password_change_me
MINIO_ROOT_PASSWORD=uat_minio_secure_password_change_me
```

### Production (.env.prod - NEVER COMMIT!)
```bash
# Generated secrets (use secrets manager!)
POSTGRES_PASSWORD=$(openssl rand -base64 32)
MINIO_ROOT_PASSWORD=$(openssl rand -base64 32)
REDIS_PASSWORD=$(openssl rand -base64 32)
```

## 📊 Database Schema Evolution

```
Development → UAT → Production
    ↓           ↓        ↓
  Flyway    Flyway   Flyway
Migration  Migration Migration
   (V1)      (V1)     (V1)
   (V2)      (V2)     (V2)
   (V3)      ←────────┘
  Testing    Review   Deploy
```

**Process:**
1. Create migration in `backend/src/main/resources/db/migration/V{n}__description.sql`
2. Test in **Development** first
3. If successful, deploy to **UAT** for validation
4. After UAT approval, deploy to **Production** during maintenance window

## 🚨 Common Mistakes to Avoid

### ❌ DON'T: Use production passwords in UAT
```bash
# Bad: Copying .env.prod to .env.uat
cp .env.prod .env.uat
```

### ✅ DO: Use different passwords per environment
```bash
# Good: Each environment has unique credentials
.env.dev  → dev_password_123
.env.uat  → uat_secure_password
.env.prod → [32+ char random password]
```

### ❌ DON'T: Connect dev app to UAT database
```bash
# Bad: Mixing environments
DATABASE_URL=postgresql://localhost:5433/taxdividend_uat  # UAT DB
APP_ENV=development  # Dev app
```

### ✅ DO: Keep environments isolated
```bash
# Good: Match app to environment
Dev app  → localhost:5432 (dev DB)
UAT app  → localhost:5433 (UAT DB)
Prod app → RDS endpoint (managed DB)
```

### ❌ DON'T: Commit .env.prod to git
```bash
# Bad: Exposes production secrets
git add .env.prod
git commit -m "Add prod config"
```

### ✅ DO: Use .gitignore and examples
```bash
# Good: Template only, real file excluded
.env.prod.example → Committed (template)
.env.prod         → .gitignored (real secrets)
```

## 🎓 Learning Path

If you're new to multi-environment setups:

1. **Week 1**: Work in **Development** only
   - Get comfortable with Docker Compose
   - Practice migrations with Flyway
   - Learn database operations

2. **Week 2**: Add **UAT** environment
   - Run dev and UAT in parallel
   - Practice data migration
   - Test backup/restore

3. **Week 3**: Plan **Production** strategy
   - Research managed services (RDS, Cloud SQL)
   - Set up monitoring and alerting
   - Create disaster recovery plan

## 📚 Further Reading

- [The Twelve-Factor App - Config](https://12factor.net/config)
- [Environment-Specific Configuration Best Practices](https://aws.amazon.com/blogs/devops/best-practices-for-managing-environment-specific-configurations/)
- [Database Migration Strategies](https://flywaydb.org/documentation/)
- [Docker Compose Networking](https://docs.docker.com/compose/networking/)
