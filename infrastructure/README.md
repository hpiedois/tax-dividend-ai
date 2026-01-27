# Infrastructure - Tax Dividend AI

Docker Compose setup for local development.

## Services Included

| Service | Port(s) | Description | Credentials |
|---------|---------|-------------|-------------|
| **PostgreSQL** | 5432 | Main database | User: `taxdividend`<br>Password: `dev_password_change_in_prod`<br>Database: `taxdividend` |
| **MinIO** | 9000 (API)<br>9001 (Console) | S3-compatible object storage | User: `minioadmin`<br>Password: `minioadmin123`<br>Bucket: `tax-dividend-forms` |
| **pgAdmin** | 5050 | PostgreSQL web UI (optional) | Email: `admin@taxdividend.local`<br>Password: `admin123` |

## Quick Start

### 1. Start All Services

```bash
cd infrastructure
docker-compose up -d
```

### 2. Check Services Health

```bash
docker-compose ps
```

All services should show `healthy` or `running`.

### 3. Access Web UIs

- **MinIO Console**: http://localhost:9001
  - Login: `minioadmin` / `minioadmin123`
  - Pre-created bucket: `tax-dividend-forms`

- **pgAdmin**: http://localhost:5050
  - Login: `admin@taxdividend.local` / `admin123`
  - Add server:
    - Host: `postgres`
    - Port: `5432`
    - Database: `taxdividend`
    - Username: `taxdividend`
    - Password: `dev_password_change_in_prod`

### 4. View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f postgres
docker-compose logs -f minio
```

### 5. Stop Services

```bash
# Stop but keep data
docker-compose stop

# Stop and remove containers (data persists in volumes)
docker-compose down

# Stop and remove everything including data (⚠️ WARNING)
docker-compose down -v
```

## Database Schema

The PostgreSQL database is automatically initialized with:

- **Tables**: `users`, `generated_forms`, `dividends`, `form_submissions`, `audit_logs`, `tax_rules`
- **Default data**: France → Switzerland tax rule (30% → 15%)
- **Extensions**: UUID support

See `init-db/01-create-schema.sql` for full schema.

## Connection Strings

### PostgreSQL (from host machine)

```
jdbc:postgresql://localhost:5432/taxdividend
postgresql://taxdividend:dev_password_change_in_prod@localhost:5432/taxdividend
```

### PostgreSQL (from Docker network)

```
jdbc:postgresql://postgres:5432/taxdividend
postgresql://taxdividend:dev_password_change_in_prod@postgres:5432/taxdividend
```

### MinIO (from host machine)

```
Endpoint: http://localhost:9000
Access Key: minioadmin
Secret Key: minioadmin123
Bucket: tax-dividend-forms
```

### MinIO (from Docker network)

```
Endpoint: http://minio:9000
Access Key: minioadmin
Secret Key: minioadmin123
Bucket: tax-dividend-forms
```

## Troubleshooting

### Reset Database

```bash
docker-compose down -v
docker-compose up -d postgres
```

### Reset MinIO Bucket

```bash
docker-compose down minio minio-client
docker volume rm infrastructure_minio_data
docker-compose up -d minio minio-client
```

### Check Database Connection

```bash
docker exec -it tax-dividend-postgres psql -U taxdividend -d taxdividend
```

### List MinIO Buckets

```bash
docker exec -it tax-dividend-minio mc ls local/
```

## Production Notes

⚠️ **This configuration is for LOCAL DEVELOPMENT only!**

For production:
1. Change all default passwords
2. Use managed services (Cloud SQL, Cloud Storage)
3. Enable SSL/TLS
4. Configure proper network isolation
5. Set up backups
6. Enable audit logging
7. Use secrets management (not env vars)

## Optional Services

### Redis (Caching)

Uncomment the Redis service in `docker-compose.yml` if needed for caching.

```yaml
redis:
  image: redis:7-alpine
  ports:
    - "6379:6379"
```

Then restart:

```bash
docker-compose up -d redis
```

## Health Checks

All services have health checks configured:

- **PostgreSQL**: `pg_isready`
- **MinIO**: HTTP health endpoint

Services won't start dependent containers until health checks pass.

## Data Persistence

All data is stored in Docker volumes:

- `postgres_data` - Database files
- `minio_data` - Uploaded PDFs
- `pgadmin_data` - pgAdmin settings

These volumes persist even after `docker-compose down`.
