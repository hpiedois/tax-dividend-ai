# Backend Services - Tax Dividend AI

Spring Boot backend services handling business logic, PDF processing, and data persistence.

## Purpose

The Backend Services handle all business logic and **must NOT be exposed publicly**. Only the BFF Gateway should call these services.

Responsibilities:

- ✅ Tax calculations (Tax Engine)
- ✅ PDF parsing (bank statements → dividends)
- ✅ PDF generation (Forms 5000/5001 with Apache PDFBox)
- ✅ Database access (PostgreSQL)
- ✅ File storage (MinIO/S3)
- ✅ Business rule validation

## Architecture

```
BFF Gateway :8080
    ↓ HTTP/REST (internal network only)
Backend Services (this project) :8081
    ↓
PostgreSQL :5432
MinIO/S3 :9000
```

## Tech Stack

- **Framework**: Spring Boot 3.5+ with Spring Data JPA
- **Database**: PostgreSQL 16+ with Hibernate
- **PDF**: Apache PDFBox 3.0+ (parsing & generation)
- **Storage**: AWS S3 SDK (compatible with MinIO)
- **Build**: Maven
- **Java**: 21

## Prerequisites

- Java 21+
- Maven 3.8+
- Docker & Docker Compose (for PostgreSQL & MinIO)

## Development Setup

### Quick Start

```bash
cd backend

# 1. First time setup (installs dependencies + starts infrastructure)
./dev-setup.sh

# 2. Run backend
./run-dev.sh
```

**Default port**: `8081`

### 3. Check Health

```bash
curl http://localhost:8081/internal/health
```

Should return `{"status":"UP","database":"connected","storage":"connected"}`.

## Configuration

### Configuration Files

The backend uses a layered configuration approach for security:

1. **`application.yml`** - Base configuration with environment variable references (NO default secrets)
2. **`application-dev.yml`** - Development profile with safe local defaults
3. **`.env.example`** - Template documenting all required environment variables

### Security Model

🔐 **All sensitive credentials are externalized:**

- **Production**: Environment variables MUST be set (no defaults provided)
- **Development**: Use `dev` profile for safe local defaults
- **Startup Validation**: `EnvironmentValidator` fails fast if required vars are missing

### Environment Variables

#### Creating your .env file

For local development, the `./dev-setup.sh` script automatically creates `.env` from `.env.example`:

```bash
# Automatic setup (recommended)
./dev-setup.sh  # Creates .env with dev profile

# Manual setup
cp .env.example .env
# Edit .env with your actual credentials
```

**⚠️ CRITICAL SECURITY:**
- The `.env` file is in `.gitignore` and **NEVER committed**
- Production deployments MUST set environment variables explicitly
- The `dev` profile provides safe defaults for local development only
- Production profile requires ALL secrets to be provided (fails on startup if missing)

#### Complete Environment Variables Reference

| Variable | Profile: dev | Profile: prod | Description |
|----------|--------------|---------------|-------------|
| **Profile Selection** | | | |
| `SPRING_PROFILES_ACTIVE` | `dev` | `prod` | Active Spring profile |
| **Database** | | | |
| `DB_HOST` | `localhost` | **Required** | PostgreSQL hostname |
| `DB_PORT` | `5432` | **Required** | PostgreSQL port |
| `DB_NAME` | `taxdividend_dev` | **Required** | Database name |
| `DB_USERNAME` | `taxdividend_user` | **Required** | Database user |
| `DB_PASSWORD` | `dev_password_123` | **Required** ⚠️ | Strong password (16+ chars) |
| **Storage (MinIO/S3)** | | | |
| `MINIO_ENDPOINT` | `http://localhost:9000` | **Required** | MinIO/S3 endpoint URL |
| `MINIO_BUCKET` | `tax-dividend-forms-dev` | **Required** | S3 bucket name |
| `MINIO_ACCESS_KEY` | `minioadmin` | **Required** ⚠️ | S3 access key |
| `MINIO_SECRET_KEY` | `minioadmin123` | **Required** ⚠️ | S3 secret key (32+ chars) |
| **Security** | | | |
| `INTERNAL_API_KEY` | `dev-internal-api-key-...` | **Required** ⚠️ | Internal API key (min 32 chars, cryptographically random) |
| **Keycloak** | | | |
| `KEYCLOAK_SERVER_URL` | `http://localhost:8180` | **Required** | Keycloak server URL |
| `KEYCLOAK_REALM` | `tax-dividend` | **Required** | Keycloak realm name |
| `KEYCLOAK_ADMIN_USERNAME` | `admin` | **Required** ⚠️ | Keycloak admin username |
| `KEYCLOAK_ADMIN_PASSWORD` | `admin` | **Required** ⚠️ | Strong password (16+ chars) |
| `KEYCLOAK_CLIENT_ID` | `backend-service` | **Required** | Keycloak client ID |
| **Email (SMTP)** | | | |
| `SMTP_HOST` | `localhost` | **Required** | SMTP server hostname |
| `SMTP_PORT` | `1025` | **Required** | SMTP port (587 for TLS) |
| `SMTP_USERNAME` | *(empty)* | Optional | SMTP auth username |
| `SMTP_PASSWORD` | *(empty)* | Optional | SMTP auth password |
| `SMTP_FROM` | `noreply@taxdividend.com` | **Required** | Email sender address |
| `SMTP_AUTH` | `false` | Optional | Enable SMTP auth |
| `SMTP_STARTTLS` | `false` | Optional | Enable STARTTLS |
| **Actuator Security** | | | |
| `ACTUATOR_USERNAME` | `admin` | **Required** ⚠️ | Actuator username |
| `ACTUATOR_PASSWORD` | `dev-admin-password` | **Required** ⚠️ | Strong password (16+ chars) |
| **Observability** | | | |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | `http://localhost:4317` | Optional | OpenTelemetry endpoint |
| `OTEL_TRACES_SAMPLER_PROBABILITY` | `1.0` | Optional | Trace sampling (0.0-1.0) |
| **Metadata** | | | |
| `ENVIRONMENT` | `development` | **Required** | Environment name |
| `PROJECT_VERSION` | `0.0.1-SNAPSHOT` | Optional | Application version |
| `HOSTNAME` | `localhost` | Optional | Host identifier |

**Legend:**
- ⚠️ **Security Critical**: Must use strong, unique values in production
- **Profile: dev**: Value provided by `application-dev.yml` (safe for local development)
- **Profile: prod**: **Required** means `EnvironmentValidator` will fail startup if not set

### Spring Profiles

#### Development Profile (`dev`)

**Safe for local development** - provides default values for all secrets:

```bash
# Set in .env
SPRING_PROFILES_ACTIVE=dev

# Or via command line
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Features:
- ✅ Verbose logging (`DEBUG` level)
- ✅ Full stack traces in responses
- ✅ Safe default credentials (e.g., `dev_password_123`)
- ✅ No environment validation (convenience)

#### Production Profile (`prod`)

**Security-hardened** - requires all secrets via environment variables:

```bash
# All secrets must be set
export DB_PASSWORD="strong_password_here"
export MINIO_SECRET_KEY="cryptographically_random_secret"
export INTERNAL_API_KEY="min_32_chars_random_key_here"
# ... (see .env.example for complete list)

java -jar backend.jar -Dspring.profiles.active=prod
```

Features:
- ✅ Minimal logging (`INFO` level)
- ✅ No stack traces in API responses
- ✅ **`EnvironmentValidator` fails startup if critical vars missing**
- ✅ Password strength validation
- ✅ Security hardening enabled

**⚠️ NEVER use default credentials in production!**

If the `EnvironmentValidator` detects missing or weak credentials, it will:
1. Log detailed error messages
2. **Fail application startup immediately**
3. Prevent insecure deployment

## API Endpoints

⚠️ **All endpoints are INTERNAL** (not exposed publicly, only BFF Gateway should call them).

### Health & Monitoring

| Method | Path | Description |
|--------|------|-------------|
| GET | `/internal/health` | Health check |

### Authentication (internal)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/internal/auth/validate` | Validate user credentials |
| POST | `/internal/auth/create-user` | Create new user |

### PDF Processing

| Method | Path | Description |
|--------|------|-------------|
| POST | `/internal/pdf/parse` | Parse PDF bank statement |
| POST | `/internal/pdf/generate-5000` | Generate Form 5000 PDF |
| POST | `/internal/pdf/generate-5001` | Generate Form 5001 PDF |
| POST | `/internal/pdf/generate-bundle` | Generate ZIP with both forms |

### Data Access

| Method | Path | Description |
|--------|------|-------------|
| GET | `/internal/users/{id}` | Get user details |
| GET | `/internal/dividends?userId={id}` | List user dividends |
| POST | `/internal/dividends` | Save parsed dividends |
| GET | `/internal/forms?userId={id}` | List user forms |
| POST | `/internal/forms` | Save generated form metadata |

## Database Migrations (Flyway)

This project uses **Flyway** for database schema management.

### Migration Strategy

**Infrastructure Layer** (Docker):
- ✅ Extensions PostgreSQL (`uuid-ossp`, `pgcrypto`, etc.)
- ✅ Schema `taxdividend`
- ✅ User `taxdividend_user`

Managed by Docker Compose via `../infrastructure/migrations/`

**Application Layer** (Flyway):
- ✅ **All application tables** (users, dividends, forms, etc.)
- ✅ Indexes and constraints
- ✅ Initial data (tax rules)

Managed by Flyway via `src/main/resources/db/migration/`

### Database Schema

**Single initialization script:**
- `V1__init_schema.sql` - Complete database schema initialization
  - Creates all tables: users, generated_forms, dividends, form_submissions, audit_logs, tax_rules
  - Inserts default tax rules (France → Switzerland)
  - Sets up all indexes and triggers

**On first startup**, Flyway automatically:
1. Creates `flyway_schema_history` table to track migrations
2. Runs V1__init_schema.sql to create all tables
3. Backend is ready to use

### Verify Schema

```bash
# Connect to database
docker exec -it tax-dividend-postgres-dev psql -U postgres -d taxdividend_dev

# Check Flyway history
SELECT * FROM flyway_schema_history;

# List tables
\dt

# Describe users table
\d users
```

### Reset Database (Development Only)

```bash
# ⚠️ Deletes all data and recreates schema
./reset-db.sh dev
```

### Adding New Migrations

For schema changes AFTER initialization:

```bash
# Naming convention: V{version}__{description}.sql
touch src/main/resources/db/migration/V2__add_user_preferences.sql
```

**Important**:
- Version numbers must be sequential (V2, V3, etc.)
- Use double underscore `__` after version
- Use snake_case for description
- **Never modify V1__init_schema.sql after it has run**

**Tables**:
- `users` - User accounts
- `generated_forms` - Form metadata (PDF references)
- `dividends` - Dividend data (linked to forms)
- `form_submissions` - Submission tracking
- `audit_logs` - Audit trail
- `tax_rules` - Tax treaty rules (France → Switzerland, etc.)

## Testing

### Run Tests

```bash
# All tests (unit + integration)
./run-tests.sh all

# Unit tests only
./run-tests.sh unit

# Integration tests only (with Testcontainers)
./run-tests.sh integration
```

Integration tests automatically start PostgreSQL in Docker via Testcontainers.

### Manual Testing

#### 1. Parse PDF

```bash
curl -X POST http://localhost:8081/internal/pdf/parse \
  -F "file=@/path/to/bank-statement.pdf" \
  -F "userId=123e4567-e89b-12d3-a456-426614174000"
```

Response:
```json
{
  "dividends": [
    {
      "securityName": "TotalEnergies SE",
      "isin": "FR0000120271",
      "grossAmount": 1000.00,
      "currency": "EUR",
      "withholdingTax": 300.00,
      "reclaimableAmount": 150.00
    }
  ]
}
```

#### 2. Generate Form 5000

```bash
curl -X POST http://localhost:8081/internal/pdf/generate-5000 \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "taxYear": 2024,
    "fullName": "Jean Dupont",
    "address": "Rue de la Paix 10, 1003 Lausanne",
    "taxId": "CHE-123.456.789"
  }'
```

Response:
```json
{
  "formId": "789e4567-e89b-12d3-a456-426614174000",
  "s3Key": "forms/2024/123e4567.../form-5000.pdf",
  "downloadUrl": "https://minio:9000/tax-dividend-forms/forms/2024/..."
}
```

## Building for Production

```bash
./mvnw clean package -DskipTests
```

Generates `target/backend-0.0.1-SNAPSHOT.jar`.

### Run JAR

```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### Docker Build

```bash
docker build -t tax-dividend-backend:latest .
docker run -p 8081:8081 \
  -e DB_HOST=postgres \
  -e S3_ENDPOINT=http://minio:9000 \
  tax-dividend-backend:latest
```

## Code Structure

```
src/main/java/com/taxdividend/backend/
├── config/
│   ├── DatabaseConfig.java           # JPA/Hibernate config
│   ├── S3Config.java                 # MinIO/S3 client config
│   └── InternalSecurityConfig.java   # Internal API security
├── controller/
│   ├── InternalAuthController.java   # /internal/auth/*
│   ├── PdfController.java            # /internal/pdf/*
│   └── HealthController.java         # /internal/health
├── service/
│   ├── PdfParserService.java         # Parse bank statement PDFs
│   ├── PdfGenerationService.java     # Generate Forms 5000/5001
│   ├── TaxCalculationService.java    # Tax Engine (calculate reclaimable amounts)
│   ├── StorageService.java           # S3/MinIO upload/download
│   └── ZipService.java               # Create ZIP bundles
├── repository/
│   ├── UserRepository.java           # JPA repository for users
│   ├── DividendRepository.java       # JPA repository for dividends
│   └── FormRepository.java           # JPA repository for forms
├── model/
│   ├── User.java                     # JPA entity
│   ├── Dividend.java                 # JPA entity
│   ├── GeneratedForm.java            # JPA entity
│   └── TaxRule.java                  # JPA entity
└── TaxDividendBackendApplication.java  # Main class
```

## Tax Calculation Logic

### France → Switzerland Example

**Input** (from parsed PDF):
- Gross dividend: €1000
- French withholding: €300 (30%)

**Tax Engine calculates**:
1. Look up treaty rate: France → Switzerland = 15%
2. Treaty amount: €1000 × 15% = €150
3. Reclaimable: €300 - €150 = €150

**Output**:
```json
{
  "grossAmount": 1000.00,
  "withholdingTax": 300.00,
  "treatyRate": 15.00,
  "reclaimableAmount": 150.00
}
```

Tax rules are stored in `tax_rules` table and can be updated without code changes.

## PDF Generation (Apache PDFBox)

### Form 5000 (Attestation of Residence)

Template: `src/main/resources/templates/form-5000-template.pdf`

Fields filled:
- User name, address, tax ID
- Canton, country
- Tax year
- Signature (optional)

### Form 5001 (Dividend Liquidation)

Template: `src/main/resources/templates/form-5001-template.pdf`

Fields filled:
- List of dividends (security name, ISIN, date, amounts)
- Total gross amount
- Total withholding tax
- Total reclaimable amount

### Bundle (ZIP)

Creates a ZIP file containing:
- `form-5000.pdf`
- `form-5001.pdf`
- `summary.json` (metadata)

## Storage (S3/MinIO)

### File Organization

```
tax-dividend-forms/
├── forms/
│   └── 2024/
│       └── {userId}/
│           ├── form-5000-{formId}.pdf
│           ├── form-5001-{formId}.pdf
│           └── bundle-{formId}.zip
└── uploads/
    └── {userId}/
        └── bank-statement-{uploadId}.pdf
```

### Pre-signed URLs

Generated forms use pre-signed URLs (expire in 1 hour):

```java
String downloadUrl = storageService.generatePresignedUrl(s3Key, 3600);
```

This allows frontend to download without exposing S3 credentials.

## Security Notes

### Configuration Security

🔐 **Environment-based secrets management:**

1. **No secrets in code/config files**
   - `application.yml` uses environment variable references only
   - No default values for production credentials
   - `.env.example` documents required variables (never committed)

2. **Automatic validation on startup**
   - `EnvironmentValidator` checks all critical environment variables
   - Validates minimum lengths (e.g., API keys must be 32+ chars)
   - Warns about weak passwords in production profile
   - **Fails fast** if requirements not met

3. **Profile-based security**
   - `dev` profile: Safe defaults for local development
   - `prod` profile: All secrets required, strict validation

4. **Production recommendations**
   - Use secret management systems (AWS Secrets Manager, Vault)
   - Rotate credentials regularly
   - Use strong passwords (16+ chars, mixed case, numbers, symbols)
   - Generate API keys cryptographically random (32+ chars)

### Internal API Only

⚠️ **This service should NOT be exposed to the internet!**

- No CORS configuration
- No public authentication endpoints
- Assumes trusted callers (BFF Gateway only)

For production:
1. Deploy in private network/VPC
2. Use network security groups to restrict access
3. Consider mutual TLS (mTLS) between BFF and Backend

### Database Security

- Passwords hashed with BCrypt
- SQL injection prevented by JPA/Hibernate
- Audit logs for compliance

## Monitoring

### Actuator Endpoints

Add to `application.yml` for production:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info
```

## Troubleshooting

### "Connection refused" to PostgreSQL

```bash
docker-compose ps postgres
# Should show "Up (healthy)"

docker-compose logs postgres
```

### "Access Denied" for MinIO

```bash
# Check bucket exists
docker exec -it tax-dividend-minio mc ls local/tax-dividend-forms

# Recreate bucket
docker-compose up -d minio-client
```

### PDF Generation Fails

Check logs for PDFBox errors. Common issues:
- Template PDF not found in `resources/templates/`
- Invalid font paths
- Missing fields in template

## Related Projects

- **BFF Gateway**: `../bff-gateway/` - API gateway
- **Frontend**: `../frontend/` - React UI
- **Infrastructure**: `../infrastructure/` - Docker Compose

## Further Reading

- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Apache PDFBox](https://pdfbox.apache.org/)
- [AWS SDK for Java](https://aws.amazon.com/sdk-for-java/)
- [MinIO Java Client](https://min.io/docs/minio/linux/developers/java/minio-java.html)
