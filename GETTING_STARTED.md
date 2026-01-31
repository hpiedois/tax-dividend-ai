# Getting Started - Tax Dividend AI

Quick start guide to run the full stack locally.

## Prerequisites

- **Node.js** 20+ (for frontend)
- **Java** 21+ (for backend services)
- **Maven** 3.8+ (or use `./mvnw` wrapper)
- **Docker** & **Docker Compose** (for infrastructure)

## Project Structure

```
tax-dividend-ai/
├── frontend/          # React + Vite (port 5173)
├── bff-gateway/       # Spring Boot WebFlux (port 8080)
├── backend/           # Spring Boot + JPA (port 8081)
├── infrastructure/    # Docker Compose (PostgreSQL, MinIO)
└── docs/              # Documentation
```

## Quick Start (Full Stack)

### 1. Start Infrastructure (PostgreSQL + MinIO)

```bash
cd infrastructure
docker-compose up -d
```

**Wait 10-15 seconds** for health checks to pass.

Verify services are running:

```bash
docker-compose ps
```

All services should show `Up` or `healthy`.

**What happens on first startup**:
- PostgreSQL container starts
- Infrastructure migrations run automatically:
  - `01_extensions.sql` - Install PostgreSQL extensions
  - `02_schemas_and_roles.sql` - Create schema `taxdividend` and user `taxdividend_user`
- ⚠️ **Tables are NOT created yet** - Flyway will create them when backend starts

### 2. Start Backend Services

```bash
cd backend
./mvnw spring-boot:run
```

**Wait for** `Started TaxDividendBackendApplication in X seconds`.

**What happens on first startup**:
- Backend connects to PostgreSQL using `taxdividend_user`
- **Flyway runs automatically**:
  - V1: Create `users` table
  - V2: Create `generated_forms` and `dividends` tables
  - V3: Create `form_submissions` and `audit_logs` tables
  - V4: Create `tax_rules` table
  - V5: Insert default tax rules (France → Switzerland)
- Application starts normally

Check Flyway execution in logs:
```
Flyway Community Edition X.X.X
Database: jdbc:postgresql://localhost:5432/taxdividend
Successfully validated 5 migrations
Current version of schema "taxdividend": << Empty Schema >>
Migrating schema "taxdividend" to version "1 - create users table"
Migrating schema "taxdividend" to version "2 - create forms tables"
...
Successfully applied 5 migrations
```

Test backend health:

```bash
curl http://localhost:8081/internal/health
```

Should return `{"status":"UP","database":"connected"}`.

### 3. Start BFF Gateway

Open a **new terminal**:

```bash
cd bff-gateway
./mvnw spring-boot:run
```

**Wait for** `Started TaxDividendBffGatewayApplication in X seconds`.

Test BFF health:

```bash
curl http://localhost:8080/actuator/health
```

Should return `{"status":"UP"}`.

### 4. Start Frontend

Open a **new terminal**:

```bash
cd frontend
npm install  # First time only
npm run dev
```

Open browser: **http://localhost:5173**

## Ports Summary

| Service | Port | URL | Description |
|---------|------|-----|-------------|
| **Frontend** | 5173 | http://localhost:5173 | React UI |
| **BFF Gateway** | 8080 | http://localhost:8080 | API Gateway (public) |
| **Backend** | 8081 | http://localhost:8081 | Services (internal) |
| **PostgreSQL** | 5432 | - | Database |
| **MinIO API** | 9000 | http://localhost:9000 | S3 storage |
| **MinIO Console** | 9001 | http://localhost:9001 | MinIO web UI |
| **pgAdmin** | 5050 | http://localhost:5050 | PostgreSQL UI |

## Data Flow

```
┌─────────────┐
│   Browser   │
│  :5173      │
└──────┬──────┘
       │ HTTP/REST
       ↓
┌─────────────┐
│ BFF Gateway │  Public API
│  :8080      │  CORS enabled
└──────┬──────┘
       │ HTTP/REST (internal)
       ↓
┌─────────────┐
│   Backend   │  Business Logic
│  :8081      │  (NOT exposed)
└──────┬──────┘
       │
       ├──→ PostgreSQL :5432
       └──→ MinIO :9000
```

## Web UIs

### Frontend Application

**URL**: http://localhost:5173

- Login with mock credentials
- Upload PDF bank statements
- Generate Forms 5000/5001
- View history

### MinIO Console

**URL**: http://localhost:9001

**Credentials**:
- Username: `minioadmin`
- Password: `minioadmin123`

Browse uploaded PDFs in bucket `tax-dividend-forms`.

### pgAdmin (Database UI)

**URL**: http://localhost:5050

**Credentials**:
- Email: `admin@taxdividend.local`
- Password: `admin123`

**Add Server**:
1. Right-click "Servers" → "Register" → "Server"
2. **General** tab: Name = `Tax Dividend DB`
3. **Connection** tab:
   - Host: `postgres` (or `localhost` if outside Docker)
   - Port: `5432`
   - Database: `taxdividend`
   - Username: `taxdividend`
   - Password: `dev_password_change_in_prod`
4. Click "Save"

## Testing the API

### 1. Register a User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Jean Dupont"
  }'
```

### 2. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJSUzI1NiIs...",
  "expiresIn": 3600
}
```

**Save the token**:
```bash
export TOKEN="eyJhbGciOiJSUzI1NiIs..."
```

### 3. Upload PDF (Parse Dividends)

```bash
curl -X POST http://localhost:8080/api/dividends/parse \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/bank-statement.pdf"
```

### 4. Generate Forms

```bash
curl -X POST http://localhost:8080/api/forms/generate \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "taxYear": 2024,
    "dividendIds": ["uuid-1", "uuid-2"]
  }'
```

### 5. Download Form

```bash
curl -X GET http://localhost:8080/api/forms/{formId}/download \
  -H "Authorization: Bearer $TOKEN"
```

Returns a pre-signed S3 URL (valid for 1 hour).

## Stopping Services

### Stop Frontend
Press `Ctrl+C` in frontend terminal.

### Stop BFF Gateway
Press `Ctrl+C` in BFF terminal.

### Stop Backend
Press `Ctrl+C` in backend terminal.

### Stop Infrastructure
```bash
cd infrastructure
docker-compose stop
```

**Keep data**:
```bash
docker-compose down  # Volumes persist
```

**Delete all data** (⚠️ WARNING):
```bash
docker-compose down -v  # Deletes volumes
```

## Troubleshooting

### "Connection refused" errors

**Problem**: Backend can't connect to PostgreSQL

**Solution**:
```bash
cd infrastructure
docker-compose ps postgres
# Should show "Up (healthy)"

# If not, restart:
docker-compose restart postgres
```

### CORS errors in browser

**Problem**: Frontend can't call BFF Gateway

**Solution**:
1. Check BFF is running on port 8080
2. Verify `CORS_ALLOWED_ORIGINS` in `bff-gateway/application.yml`:
   ```yaml
   cors:
     allowed-origins: http://localhost:5173
   ```

### "Invalid JWT" errors

**Problem**: Token expired or invalid

**Solution**:
1. Login again to get a new token
2. Check token hasn't expired (default: 1 hour)

### Port already in use

**Problem**: Port 8080, 8081, or 5173 already taken

**Solution**:
```bash
# Find process using port
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Kill process or change port in application.yml
```

### Database schema not created

**Problem**: Tables don't exist in `taxdividend` schema

**Solution**:

1. Check if infrastructure migrations ran:
```bash
cd infrastructure
docker exec -it tax-dividend-postgres psql -U postgres -d taxdividend -c "\dn"
# Should show schema "taxdividend"

docker exec -it tax-dividend-postgres psql -U postgres -d taxdividend -c "SELECT rolname FROM pg_roles WHERE rolname LIKE 'taxdividend%';"
# Should show "taxdividend_user"
```

2. If schema is missing, recreate infrastructure:
```bash
docker-compose down -v
docker-compose up -d postgres
# Wait 10 seconds for initialization
```

3. Check if Flyway migrations ran in backend:
```bash
docker exec -it tax-dividend-postgres psql -U taxdividend_user -d taxdividend
\dt taxdividend.*
# Should show: users, dividends, generated_forms, form_submissions, audit_logs, tax_rules
```

4. If tables are missing but schema exists, restart backend (Flyway will run):
```bash
cd backend
./mvnw spring-boot:run
```

### MinIO bucket not found

**Problem**: `tax-dividend-forms` bucket doesn't exist

**Solution**:
```bash
cd infrastructure
docker-compose up -d minio-client
# This creates the bucket
```

## Development Workflow

### Hot Reload

- **Frontend**: ✅ Auto-reload on save (Vite HMR)
- **BFF/Backend**: ✅ Auto-reload with Spring DevTools (if enabled)

### Rebuild After Changes

**Frontend**:
```bash
# No rebuild needed - Vite handles it
```

**Backend/BFF**:
```bash
# Maven automatically recompiles on save if using ./mvnw spring-boot:run
# Or restart the application
```

## Next Steps

1. ✅ Read `DECISIONS.md` for architecture decisions
2. ✅ Check `docs/ARCHITECTURE.md` for detailed design
3. ✅ See `docs/ACTIONS_COMPLETED.md` for roadmap
4. ✅ Explore API with Postman or curl
5. ✅ Run tests: `./mvnw test` in backend/bff-gateway

## Additional Resources

- **Frontend**: See `frontend/README.md`
- **BFF Gateway**: See `bff-gateway/README.md`
- **Backend**: See `backend/README.md`
- **Infrastructure**: See `infrastructure/README.md`
- **Claude Code Guide**: See `CLAUDE.md`

## Production Deployment

⚠️ **This setup is for LOCAL DEVELOPMENT only!**

For production deployment:
- See `docs/ARCHITECTURE.md` for Cloud Run deployment
- Use managed PostgreSQL (Cloud SQL)
- Use managed storage (Cloud Storage)
- Enable HTTPS/SSL
- Change all default passwords
- Configure proper secrets management

---

**Need help?** Open an issue in the GitHub repository.
