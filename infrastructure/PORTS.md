# Port Reference - Tax Dividend AI Infrastructure

## 🔌 Port Allocation Matrix

Quick reference for all exposed ports across environments.

```
┌──────────────┬──────────────────┬──────────────────┬──────────────────┐
│   Service    │   Development    │       UAT        │   Production     │
├──────────────┼──────────────────┼──────────────────┼──────────────────┤
│ PostgreSQL   │ 5432             │ 5433             │ 5432 (localhost) │
│ MinIO API    │ 9000             │ 9002             │ 9000 (localhost) │
│ MinIO UI     │ 9001             │ 9003             │ 9001 (localhost) │
│ Redis        │ 6379             │ 6380             │ 6379 (localhost) │
└──────────────┴──────────────────┴──────────────────┴──────────────────┘
```

## 📊 Development Environment (Default)

| Service | Port | Access URL | Credentials |
|---------|------|------------|-------------|
| **PostgreSQL** | `5432` | `localhost:5432` | User: `postgres`<br>Pass: `dev_password_123`<br>DB: `taxdividend_dev` |
| **MinIO API** | `9000` | http://localhost:9000 | Access: `minioadmin`<br>Secret: `minioadmin123` |
| **MinIO Console** | `9001` | http://localhost:9001 | User: `minioadmin`<br>Pass: `minioadmin123` |
| **Redis** | `6379` | `localhost:6379` | No password |

**Start:** `./start-dev.sh`

## 🧪 UAT Environment

| Service | Port | Access URL | Credentials |
|---------|------|------------|-------------|
| **PostgreSQL** | `5433` | `localhost:5433` | User: `postgres`<br>Pass: `[from .env.uat]`<br>DB: `taxdividend_uat` |
| **MinIO API** | `9002` | http://localhost:9002 | Access: `[from .env.uat]`<br>Secret: `[from .env.uat]` |
| **MinIO Console** | `9003` | http://localhost:9003 | User: `[from .env.uat]`<br>Pass: `[from .env.uat]` |
| **Redis** | `6380` | `localhost:6380` | No password |

**Start:** `./start-uat.sh`

## 🔒 Production Environment

| Service | Port | Access URL | Credentials |
|---------|------|------------|-------------|
| **PostgreSQL** | `5432` | `127.0.0.1:5432` ⚠️ | User: `[from .env.prod]`<br>Pass: `[from .env.prod]`<br>DB: `taxdividend` |
| **MinIO API** | `9000` | `127.0.0.1:9000` ⚠️ | Access: `[from .env.prod]`<br>Secret: `[from .env.prod]` |
| **MinIO Console** | `9001` | `127.0.0.1:9001` ⚠️ | Disabled (`MINIO_BROWSER=off`) |
| **Redis** | `6379` | `127.0.0.1:6379` ⚠️ | **Password required!** |

⚠️ **Security:** All ports bound to localhost only (127.0.0.1) - not accessible from network!

**Recommendation:** Use managed cloud services instead of Docker

## 🔀 Running Multiple Environments

You can run Dev + UAT simultaneously because they use different ports:

```bash
# Terminal 1: Start dev
./start-dev.sh

# Terminal 2: Start UAT
./start-uat.sh

# Now you have:
# - Dev DB:  localhost:5432
# - UAT DB:  localhost:5433
# - Dev MinIO: localhost:9000-9001
# - UAT MinIO: localhost:9002-9003
```

## 🌐 Network Configuration

### Development Network
- **Name:** `tax-dividend-dev-network`
- **Driver:** bridge
- **Containers:** postgres-dev, minio-dev, redis-dev

### UAT Network
- **Name:** `tax-dividend-uat-network`
- **Driver:** bridge
- **Containers:** postgres-uat, minio-uat, redis-uat

### Production Network
- **Name:** `tax-dividend-prod-network`
- **Driver:** bridge
- **Containers:** postgres-prod, minio-prod, redis-prod

**Note:** Networks are isolated - containers in dev cannot access UAT/prod containers.

## 🔍 Port Conflict Resolution

### PostgreSQL Port 5432 Conflict

If you have PostgreSQL installed locally:

```bash
# Option 1: Stop local PostgreSQL
brew services stop postgresql

# Option 2: Change dev port in .env.dev
POSTGRES_PORT=5434

# Option 3: Use UAT environment (port 5433)
./start-uat.sh
```

### MinIO Port 9000 Conflict

```bash
# Check what's using port 9000
lsof -i :9000

# Change MinIO port in .env.dev
MINIO_API_PORT=9010
MINIO_CONSOLE_PORT=9011
```

### Redis Port 6379 Conflict

```bash
# Stop local Redis
brew services stop redis

# Or change port in .env.dev
REDIS_PORT=6380
```

## 🔐 Firewall Configuration (Production)

If deploying to a server (not recommended, use managed services instead):

```bash
# Allow PostgreSQL only from localhost
sudo ufw allow from 127.0.0.1 to any port 5432

# Allow MinIO only from localhost
sudo ufw allow from 127.0.0.1 to any port 9000

# Block external access
sudo ufw deny 5432
sudo ufw deny 9000
sudo ufw deny 6379
```

## 📱 Mobile/Remote Access

### Development (Safe)
Use SSH tunnel from remote machine:

```bash
# From your laptop, connect to dev server
ssh -L 5432:localhost:5432 user@dev-server.com
ssh -L 9001:localhost:9001 user@dev-server.com

# Now access as localhost on your laptop
psql -h localhost -p 5432 -U postgres -d taxdividend_dev
```

### Production (Critical)
**Never expose production ports to the internet!**

Use bastion host + SSH tunnel:
```bash
# Connect through bastion
ssh -J bastion@bastion-server.com -L 5432:prod-db:5432 user@private-server

# Or use VPN + internal network access
```

## 🎯 Quick Port Check

```bash
# Check all open ports
docker-compose -f docker-compose.dev.yml ps

# Check specific port
lsof -i :5432

# Check all Docker exposed ports
docker ps --format "table {{.Names}}\t{{.Ports}}"
```

## 📋 Connection String Templates

### Development

```bash
# PostgreSQL
postgresql://postgres:dev_password_123@localhost:5432/taxdividend_dev

# MinIO
http://minioadmin:minioadmin123@localhost:9000

# Redis
redis://localhost:6379
```

### UAT

```bash
# PostgreSQL
postgresql://postgres:${UAT_PASSWORD}@localhost:5433/taxdividend_uat

# MinIO
http://${UAT_ACCESS_KEY}:${UAT_SECRET_KEY}@localhost:9002

# Redis
redis://localhost:6380
```

### Production

```bash
# PostgreSQL (localhost only!)
postgresql://postgres:${PROD_PASSWORD}@127.0.0.1:5432/taxdividend

# MinIO (localhost only!)
http://${PROD_ACCESS_KEY}:${PROD_SECRET_KEY}@127.0.0.1:9000

# Redis (password required!)
redis://:${REDIS_PASSWORD}@127.0.0.1:6379
```

## 🚨 Security Checklist

- [ ] Development: Accessible on localhost only ✅
- [ ] UAT: Change default passwords in .env.uat ⚠️
- [ ] Production: Use 127.0.0.1 binding (not 0.0.0.0) ✅
- [ ] Production: Strong passwords (32+ chars) ⚠️
- [ ] Production: Redis password enabled ⚠️
- [ ] Production: MinIO browser UI disabled ⚠️
- [ ] Production: pgAdmin not exposed ✅
- [ ] Production: Firewall rules configured ⚠️
- [ ] Production: SSH tunnels for admin access ⚠️
- [ ] Production: SSL/TLS certificates installed ⚠️

---

**Remember:** For production, use managed services (AWS RDS, Cloud SQL, S3, etc.) instead of exposing ports!
