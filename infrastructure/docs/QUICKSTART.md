# Quick Start Guide - Tax Dividend AI Infrastructure

## 🎯 TL;DR
```bash
cd infrastructure
./setup.sh      # First time only
./start-dev.sh  # Start development environment
```

**Services will be grouped under stack: `tax-dividend-infra`**

Access your services:
- **PostgreSQL**: `localhost:5432` (postgres / dev_password_123)
- **Keycloak**: http://localhost:8080 (admin / admin)
- **MinIO Console**: http://localhost:9001 (minioadmin / minioadmin123)
- **Mailhog UI**: http://localhost:8025
- **Grafana**: http://localhost:3000 (admin / admin)

## 📋 Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop) installed and running
- At least 4GB of free RAM
- At least 10GB of free disk space

## 🚀 First Time Setup

### 1. Run Setup Script

```bash
cd infrastructure
./setup.sh
```

This will:
- ✅ Check Docker installation
- ✅ Create backup directories
- ✅ Pull all Docker images
- ✅ Set script permissions

### 2. Start Development Environment

```bash
./start-dev.sh
```

Wait for all services to be healthy (~30 seconds).

### 3. Verify Everything Works

Open your browser:

**MinIO Console** - http://localhost:9001
- Username: `minioadmin`
- Password: `minioadmin123`

**PostgreSQL** (using psql):
```bash
docker exec -it tax-dividend-postgres-dev psql -U postgres -d taxdividend_dev
```

## 📊 Environment Cheat Sheet

### Development (Default)

```bash
# Start
./start-dev.sh

# Stop
./stop-dev.sh

# View logs
docker-compose -f docker-compose.dev.yml logs -f

# Backup
./backup-db.sh dev

# Database console
docker exec -it tax-dividend-postgres-dev psql -U postgres -d taxdividend_dev
```

**Ports:**
- PostgreSQL: `5432`
- MinIO API: `9000`
- MinIO Console: `9001`
- Redis: `6379`

### UAT (Testing)

```bash
# Start
./start-uat.sh

# Stop
./stop-uat.sh

# Backup
./backup-db.sh uat

# Database console
docker exec -it tax-dividend-postgres-uat psql -U postgres -d taxdividend_uat
```

**Ports:** (Different to run alongside dev!)
- PostgreSQL: `5433`
- MinIO API: `9002`
- MinIO Console: `9003`
- Redis: `6380`

## 🔧 Common Tasks

### Create Database Backup

```bash
# Backup development
./backup-db.sh dev

# Backup UAT
./backup-db.sh uat

# Backups saved to: backups/{env}/
```

### Restore Database

```bash
# Restore development
./restore-db.sh dev backups/dev/taxdividend_dev_20260128_120000.sql.gz

# Restore UAT
./restore-db.sh uat backups/uat/taxdividend_uat_20260128_120000.sql.gz
```

⚠️ **Warning**: This replaces all data!

### Reset Environment (Delete All Data)

```bash
# Development
docker-compose -f docker-compose.dev.yml down -v
./start-dev.sh

# UAT
docker-compose -f docker-compose.uat.yml down -v
./start-uat.sh
```

### View Logs

```bash
# All services
docker-compose -f docker-compose.dev.yml logs -f

# Specific service
docker-compose -f docker-compose.dev.yml logs -f postgres-dev
docker-compose -f docker-compose.dev.yml logs -f minio-dev
```

### Check Service Status

```bash
# Health check
docker-compose -f docker-compose.dev.yml ps

# Detailed inspection
docker inspect tax-dividend-postgres-dev --format='{{.State.Health.Status}}'
```

## 🗃️ Database Connection Strings

### Development

**JDBC (Spring Boot):**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taxdividend_dev
spring.datasource.username=postgres
spring.datasource.password=dev_password_123
```

**PostgreSQL URL:**
```
postgresql://postgres:dev_password_123@localhost:5432/taxdividend_dev
```

**From Docker Network:**
```
postgresql://postgres:dev_password_123@postgres-dev:5432/taxdividend_dev
```

### UAT

**JDBC (Spring Boot):**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/taxdividend_uat
spring.datasource.username=postgres
spring.datasource.password=uat_secure_password_change_me
```

**PostgreSQL URL:**
```
postgresql://postgres:uat_secure_password_change_me@localhost:5433/taxdividend_uat
```

## 🌐 MinIO Configuration

### Development

**Application Configuration:**
```properties
minio.endpoint=http://localhost:9000
minio.access-key=minioadmin
minio.secret-key=minioadmin123
minio.bucket=tax-dividend-forms-dev
```

**AWS SDK Compatible:**
```java
MinioClient client = MinioClient.builder()
    .endpoint("http://localhost:9000")
    .credentials("minioadmin", "minioadmin123")
    .build();
```

### UAT

**Application Configuration:**
```properties
minio.endpoint=http://localhost:9002
minio.access-key=uat_minio_admin
minio.secret-key=uat_minio_secure_password_change_me
minio.bucket=tax-dividend-forms-uat
```

## 🧪 Testing the Setup

### 1. Create Test Data

```bash
docker exec -it tax-dividend-postgres-dev psql -U postgres -d taxdividend_dev
```

```sql
-- Create a test table (after Flyway migrations)
INSERT INTO users (email, first_name, last_name)
VALUES ('test@example.com', 'Test', 'User');

-- Verify
SELECT * FROM users;
```

### 2. Upload to MinIO

```bash
# Create a test file
echo "Hello Tax Dividend!" > test.txt

# Upload using mc client
docker exec -i tax-dividend-minio-dev mc cp - local/tax-dividend-forms-dev/test.txt < test.txt

# Verify
docker exec -it tax-dividend-minio-dev mc ls local/tax-dividend-forms-dev/
```

### 3. Test Redis

```bash
docker exec -it tax-dividend-redis-dev redis-cli

# In Redis CLI:
SET test "Hello Redis"
GET test
```

## 🐛 Troubleshooting

### Port Already in Use

```bash
# Find what's using the port
lsof -i :5432

# Kill the process (replace PID)
kill -9 <PID>

# Or stop local PostgreSQL
brew services stop postgresql
```

### Services Not Starting

```bash
# Check Docker is running
docker info

# View detailed logs
docker-compose -f docker-compose.dev.yml logs postgres-dev

# Restart specific service
docker-compose -f docker-compose.dev.yml restart postgres-dev
```

### Database Connection Issues

```bash
# Check container is running
docker ps | grep postgres

# Check health status
docker inspect tax-dividend-postgres-dev --format='{{.State.Health.Status}}'

# View PostgreSQL logs
docker logs tax-dividend-postgres-dev --tail 50
```

### Out of Disk Space

```bash
# Remove unused Docker images
docker image prune -a

# Remove old containers
docker container prune

# Check Docker disk usage
docker system df
```

## 📚 Next Steps

1. **Read full documentation**: `README.md`
2. **Understand environments**: `ENVIRONMENTS.md`
3. **Configure backend**: `../backend/src/main/resources/application.yml`
4. **Run Flyway migrations**: Start backend app to create tables
5. **Start developing**: Build amazing features!

## 🆘 Getting Help

- **Infrastructure issues**: Check `README.md` → Troubleshooting section
- **Environment questions**: Read `ENVIRONMENTS.md`
- **Backend connection**: Check `../backend/README.md`
- **Docker issues**: https://docs.docker.com/get-help/

## 🎓 Learning Resources

- [PostgreSQL Tutorial](https://www.postgresqltutorial.com/)
- [MinIO Documentation](https://min.io/docs/minio/linux/index.html)
- [Docker Compose Guide](https://docs.docker.com/compose/gettingstarted/)
- [Redis Tutorial](https://redis.io/learn)

---

**Happy coding!** 🚀
