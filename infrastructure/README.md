# Infrastructure - Tax Dividend AI

Multi-environment Docker infrastructure for local development, UAT testing, and production deployment.

## 🏗️ Architecture Overview

This infrastructure provides three separate environments:

| **Environment** | **Purpose** | **Database** | **Ports** | **Services** |
|------------|---------|----------|-------|----------|
| **Development** | Local dev with hot-reload | `taxdividend_dev` | 5432, 9000-9001, 6379, 8080, 8025, 3000 | Postgres, MinIO, Redis, Keycloak, Mailhog, Observability Stack |
| **UAT** | User acceptance testing | `taxdividend_uat` | 5433, 9002-9003, 6380 | Postgres, MinIO, Redis (Keycloak/Obs optional) |
| **Production** | Production deployment | `taxdividend` | Localhost only | Managed Services Recommended |

## ⚠️ Important: Infrastructure vs Application

This infrastructure creates **ONLY**:
- ✅ Services (PostgreSQL, MinIO, Redis)
- ✅ PostgreSQL extensions
- ✅ Database schemas and roles
- ✅ MinIO buckets

It **DOES NOT** create:
- ❌ Application tables
- ❌ Business data
- ❌ Application constraints

**Tables are created by Flyway** migrations in `backend/src/main/resources/db/migration/`.

## 🚀 Quick Start

### Development Environment

```bash
cd infrastructure

# Start all dev services
./start-dev.sh

# Stop dev services
./stop-dev.sh

# View logs
docker-compose -f docker-compose.dev.yml logs -f

# Access database console
docker exec -it tax-dividend-postgres-dev psql -U postgres -d taxdividend_dev
```

**Access points:**
- **Stack Name**: `tax-dividend-infra` (in Docker Desktop)
- **PostgreSQL**: `localhost:5432` (postgres / dev_password_123)
- **Keycloak**: http://localhost:8080 (admin / admin)
- **MinIO Console**: http://localhost:9001 (minioadmin / minioadmin123)
- **Mailhog UI**: http://localhost:8025
- **Grafana**: http://localhost:3000 (admin / admin)
- **Redis**: `localhost:6379`

**Database console:**
```bash
docker exec -it tax-dividend-postgres-dev psql -U postgres -d taxdividend_dev
```

### UAT Environment

```bash
cd infrastructure

# Start UAT services
./start-uat.sh

# Stop UAT services
./stop-uat.sh

# Optional: Start with pgAdmin
docker-compose -f docker-compose.uat.yml --env-file .env.uat --profile admin up -d
```

**Access points:**
- PostgreSQL: `localhost:5433` (postgres / [from .env.uat])
- MinIO Console: http://localhost:9003
- Redis: `localhost:6380`

**Database console:**
```bash
docker exec -it tax-dividend-postgres-uat psql -U postgres -d taxdividend_uat
```

### Production Environment

⚠️ **NOT RECOMMENDED** - Use managed services (AWS RDS, Cloud SQL, etc.)

If you must use Docker for production:

```bash
cd infrastructure

# IMPORTANT: For production, use managed cloud services instead!
# AWS RDS, Google Cloud SQL, Azure Database, etc.

# If you must use Docker:
# 1. Configure production secrets
cp .env.prod.example .env.prod
# Edit .env.prod with STRONG passwords (32+ chars)

# 2. Review security checklist in docker-compose.prod.yml

# 3. Start (only after security review!)
docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d
```

## 📦 Services Included

### PostgreSQL 16

**Extensions installed:**
- `uuid-ossp` - UUID generation
- `pgcrypto` - Encryption functions
- `pg_stat_statements` - Query performance monitoring
- `pg_trgm` - Fuzzy text search
- `unaccent` - Accent-insensitive search

**Schema:**
- `taxdividend` - Application schema (tables managed by Flyway)

**Roles:**
- `postgres` - Superuser (migrations, admin)
- `taxdividend_user` - Application user (full access to taxdividend schema)

### MinIO (S3-Compatible Storage)

**Buckets created automatically:**

| Environment | Buckets |
|------------|---------|
| **Dev** | `tax-dividend-forms-dev`, `tax-dividend-exports-dev` |
| **UAT** | `tax-dividend-forms-uat`, `tax-dividend-exports-uat`, `tax-dividend-archives-uat` (versioned) |
| **Prod** | `tax-dividend-forms`, `tax-dividend-exports`, `tax-dividend-archives`, `tax-dividend-backups` (versioned + lifecycle) |

### Redis (Caching)

- **Dev**: No authentication, 6379
- **UAT**: 512MB limit with LRU eviction, 6380
- **Prod**: Password-protected, 2GB limit, 6379 (localhost only)

### Authentication (Keycloak)
- **Dev**: Runs locally on port 8080.
- **Database**: Uses `keycloak` database within the Postgres container.
- **Init**: Realm configuration imported automatically from `keycloak/realm-export.json`.

### Email Testing (Mailhog)
- **Image**: `anatomicjc/mailhog` (Native ARM64/AMD64 support).
- **SMTP**: `localhost:1025`
- **UI**: `http://localhost:8025`

### Observability Stack
Full stack configured in `infrastructure/observability/`:
- **Loki**: Log aggregation (Port 3100).
- **Promtail**: Scrapes Docker logs and sends to Loki.
- **Tempo**: Distributed tracing (Zipkin compatible on Port 9411).
- **Prometheus**: Metrics collection (Port 9090).
- **Grafana**: Visualization dashboard (Port 3000). Data sources pre-linked (Logs <-> Traces).

### Database Management

**Dynamic Initialization**: The application user is created via `migrations/02_init_roles.sh` using `TAXDIVIDEND_DB_USER` and `TAXDIVIDEND_DB_PASSWORD` from `.env` files.

Use `psql` directly via Docker exec:

```bash
# Development
docker exec -it tax-dividend-postgres-dev psql -U postgres -d taxdividend_dev

# UAT
docker exec -it tax-dividend-postgres-uat psql -U postgres -d taxdividend_uat

# Production (if using Docker)
docker exec -it tax-dividend-postgres-prod psql -U postgres -d taxdividend
```

Or use a local client (DBeaver, DataGrip, etc.) to connect to `localhost:5432` (dev) or `localhost:5433` (UAT).

## 🔧 Database Management

### Create Backup

```bash
# Backup development database
./backup-db.sh dev

# Backup UAT database
./backup-db.sh uat

# Backup production database
./backup-db.sh prod
```

Backups are stored in `backups/{env}/` and automatically compressed. Old backups (>7 days) are auto-deleted.

### Restore Backup

```bash
# Restore dev database
./restore-db.sh dev backups/dev/taxdividend_dev_20240128_143000.sql.gz

# Restore UAT database
./restore-db.sh uat backups/uat/taxdividend_uat_20240128_143000.sql.gz
```

⚠️ **Warning**: This will REPLACE ALL DATA in the target database!

### Manual Database Operations

```bash
# Connect to database
docker exec -it tax-dividend-postgres-dev psql -U postgres -d taxdividend_dev

# Run SQL file
docker exec -i tax-dividend-postgres-dev psql -U postgres -d taxdividend_dev < script.sql

# Export specific table
docker exec tax-dividend-postgres-dev pg_dump -U postgres -d taxdividend_dev -t users > users.sql
```

## 📁 Directory Structure

```
infrastructure/
├── docker-compose.dev.yml       # Development environment
├── docker-compose.uat.yml       # UAT environment
├── docker-compose.prod.yml      # Production template
├── .env.dev                     # Dev configuration
├── .env.uat                     # UAT configuration
├── .env.prod.example            # Prod template (copy to .env.prod)
├── migrations/                  # Infrastructure SQL scripts
│   ├── 01_extensions.sql        # PostgreSQL extensions
│   └── 02_schemas_and_roles.sql # Schema and roles setup
├── backups/                     # Database backups (auto-created)
│   ├── dev/
│   ├── uat/
│   └── prod/
├── start-dev.sh                 # Start development
├── stop-dev.sh                  # Stop development
├── start-uat.sh                 # Start UAT
├── stop-uat.sh                  # Stop UAT
├── backup-db.sh                 # Create database backup
└── restore-db.sh                # Restore database backup
```

## 🔌 Connection Strings

### Development

```bash
# PostgreSQL (from host)
postgresql://postgres:dev_password_123@localhost:5432/taxdividend_dev

# PostgreSQL (from Docker network)
postgresql://postgres:dev_password_123@postgres-dev:5432/taxdividend_dev

# MinIO (from host)
Endpoint: http://localhost:9000
Access Key: minioadmin
Secret Key: minioadmin123
```

### UAT

```bash
# PostgreSQL (from host)
postgresql://postgres:uat_secure_password@localhost:5433/taxdividend_uat

# PostgreSQL (from Docker network)
postgresql://postgres:uat_secure_password@postgres-uat:5432/taxdividend_uat

# MinIO (from host)
Endpoint: http://localhost:9002
Access Key: uat_minio_admin
Secret Key: [from .env.uat]
```

### Production

```bash
# PostgreSQL (localhost only!)
postgresql://postgres:STRONG_PASSWORD@localhost:5432/taxdividend

# MinIO (localhost only!)
Endpoint: http://localhost:9000
Access Key: [from .env.prod]
Secret Key: [from .env.prod]
```

## 🐛 Troubleshooting

### Port Already in Use

```bash
# Check what's using the port
lsof -i :5432

# Stop conflicting service
brew services stop postgresql

# Or use different port in .env file
```

### Database Connection Refused

```bash
# Check container status
docker ps -a | grep postgres

# View logs
docker logs tax-dividend-postgres-dev

# Restart service
docker-compose -f docker-compose.dev.yml restart postgres-dev
```

### Reset Everything

```bash
# Development (⚠️ deletes all data!)
docker-compose -f docker-compose.dev.yml down -v
./start-dev.sh

# UAT (⚠️ deletes all data!)
docker-compose -f docker-compose.uat.yml down -v
./start-uat.sh
```

### Check Service Health

```bash
# All services
docker-compose -f docker-compose.dev.yml ps

# Specific service
docker inspect tax-dividend-postgres-dev --format='{{.State.Health.Status}}'
```

## 🔒 Security Best Practices

### Development
- ✅ Use default passwords (already in .env.dev)
- ✅ Access via localhost only
- ✅ No SSL required

### UAT
- ⚠️ Change default passwords in .env.uat
- ⚠️ Different passwords from production
- ⚠️ Consider enabling SSL for external access
- ⚠️ Restrict network access

### Production
- 🔴 **NEVER use default passwords**
- 🔴 **Use secrets management** (Vault, AWS Secrets Manager)
- 🔴 **Enable SSL/TLS** for all connections
- 🔴 **Bind to localhost only** (or use firewall)
- 🔴 **Regular security updates**
- 🔴 **Automated backups**
- 🔴 **Monitoring and alerting**
- 🔴 **Prefer managed services** (RDS, Cloud SQL, S3)

## 📊 Monitoring

### Database Performance

```bash
# Active queries
docker exec -it tax-dividend-postgres-dev psql -U postgres -d taxdividend_dev -c "SELECT pid, query, state FROM pg_stat_activity WHERE state != 'idle';"

# Database size
docker exec -it tax-dividend-postgres-dev psql -U postgres -d taxdividend_dev -c "SELECT pg_size_pretty(pg_database_size('taxdividend_dev'));"

# Table sizes
docker exec -it tax-dividend-postgres-dev psql -U postgres -d taxdividend_dev -c "SELECT schemaname, tablename, pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size FROM pg_tables WHERE schemaname = 'taxdividend' ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;"
```

### MinIO Storage

```bash
# List buckets
docker exec -it tax-dividend-minio-dev mc ls local/

# Bucket size
docker exec -it tax-dividend-minio-dev mc du local/tax-dividend-forms-dev
```

### Redis Cache

```bash
# Info
docker exec -it tax-dividend-redis-dev redis-cli INFO

# Memory usage
docker exec -it tax-dividend-redis-dev redis-cli INFO memory
```

## 🚀 Production Deployment Recommendations

For production, **DO NOT use docker-compose**. Instead:

### Database
- **AWS**: Amazon RDS for PostgreSQL
- **Google Cloud**: Cloud SQL for PostgreSQL
- **Azure**: Azure Database for PostgreSQL
- **Benefits**: Automated backups, point-in-time recovery, auto-scaling, security patches

### Object Storage
- **AWS**: Amazon S3
- **Google Cloud**: Cloud Storage
- **Azure**: Azure Blob Storage
- **Benefits**: 99.999999999% durability, lifecycle policies, versioning, CDN integration

### Cache
- **AWS**: Amazon ElastiCache (Redis)
- **Google Cloud**: Cloud Memorystore
- **Azure**: Azure Cache for Redis
- **Benefits**: High availability, auto-failover, cluster mode

### Orchestration
- **Kubernetes** (EKS, GKE, AKS) for container orchestration
- **Benefits**: Auto-scaling, self-healing, rolling updates, service mesh

## 📝 Environment Variables Reference

See the respective .env files for all available configuration options:

- `.env.dev` - Development (committed to git)
- `.env.uat` - UAT (committed to git, but change passwords!)
- `.env.prod.example` - Production template (copy to .env.prod, **NEVER commit .env.prod**)

## 🤝 Contributing

When adding new infrastructure components:

1. Add to all three docker-compose files (dev, uat, prod)
2. Update environment variable files
3. Document in this README
4. Add health checks
5. Configure logging
6. Test in all environments

## 📚 Additional Resources

- [PostgreSQL Documentation](https://www.postgresql.org/docs/16/)
- [MinIO Documentation](https://min.io/docs/minio/linux/index.html)
- [Redis Documentation](https://redis.io/documentation)
- [Docker Compose Reference](https://docs.docker.com/compose/)
- [Flyway Migrations](https://flywaydb.org/documentation/)
