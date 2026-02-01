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

Configuration is split across multiple files:
- `src/main/resources/application.yml` - Base configuration with environment variable placeholders
- `src/main/resources/application-dev.yml` - Development profile (verbose logging, full stack traces)
- `src/main/resources/application-prod.yml` - Production profile (minimal logging, security hardened)

### Environment Variables

**All sensitive credentials MUST be provided via environment variables.** Never commit credentials to the repository.

#### Creating your .env file

Copy the example file and customize it:

```bash
cp .env.example .env
# Edit .env with your actual credentials
```

**⚠️ IMPORTANT:** The `.env` file is in `.gitignore` and should NEVER be committed.

#### Complete Environment Variables Reference

| Variable | Default (dev) | Required | Description |
|----------|---------------|----------|-------------|
| **Database** | | | |
| `DB_HOST` | `localhost` | Yes | PostgreSQL hostname |
| `DB_PORT` | `5432` | Yes | PostgreSQL port |
| `DB_NAME` | `taxdividend_dev` | Yes | Database name |
| `DB_USERNAME` | `taxdividend_user` | Yes | Database user |
| `DB_PASSWORD` | `changeme` | Yes | Database password ⚠️ CHANGE IN PRODUCTION |
| **Storage (MinIO/S3)** | | | |
| `MINIO_ENDPOINT` | `http://localhost:9000` | Yes | MinIO/S3 endpoint URL |
| `MINIO_ACCESS_KEY` | `changeme` | Yes | S3 access key ⚠️ CHANGE IN PRODUCTION |
| `MINIO_SECRET_KEY` | `changeme` | Yes | S3 secret key ⚠️ CHANGE IN PRODUCTION |
| `MINIO_BUCKET` | `tax-dividend-forms` | Yes | S3 bucket name |
| **Security** | | | |
| `INTERNAL_API_KEY` | `changeme-internal-api-key-min-32-chars` | Yes | Internal API authentication key (min 32 chars) ⚠️ CHANGE IN PRODUCTION |
| `JWT_SECRET_KEY` | *(must be 256+ bits)* | Yes | JWT signing key (HMAC-SHA256) ⚠️ CHANGE IN PRODUCTION |
| **Email (SMTP)** | | | |
| `SMTP_HOST` | `localhost` | Yes | SMTP server hostname |
| `SMTP_PORT` | `1025` | Yes | SMTP server port (587 for TLS, 465 for SSL) |
| `SMTP_USERNAME` | *(empty)* | No | SMTP authentication username |
| `SMTP_PASSWORD` | *(empty)* | No | SMTP authentication password |
| `SMTP_FROM` | `noreply@taxdividend.com` | Yes | Email sender address |
| `SMTP_AUTH` | `false` | No | Enable SMTP authentication (true/false) |
| `SMTP_STARTTLS` | `false` | No | Enable STARTTLS (true/false) |
| **Actuator Security** | | | |
| `ACTUATOR_USERNAME` | `admin` | Yes | Actuator endpoints username ⚠️ CHANGE IN PRODUCTION |
| `ACTUATOR_PASSWORD` | `changeme-strong-password` | Yes | Actuator endpoints password ⚠️ CHANGE IN PRODUCTION |
| **Redis (optional)** | | | |
| `REDIS_HOST` | `localhost` | No | Redis hostname |
| `REDIS_PORT` | `6379` | No | Redis port |
| `REDIS_PASSWORD` | *(empty)* | No | Redis password |
| **Observability** | | | |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | `http://localhost:4317` | No | OpenTelemetry OTLP endpoint |
| `OTEL_TRACES_SAMPLER_PROBABILITY` | `1.0` (dev), `0.1` (prod) | No | Trace sampling rate (0.0 to 1.0) |
| **Application** | | | |
| `APP_FRONTEND_URL` | `http://localhost:5173` | Yes | Frontend URL for CORS |

### Spring Profiles

Activate profiles using `-Dspring.profiles.active`:

```bash
# Development (verbose logging, full stack traces)
java -jar backend.jar -Dspring.profiles.active=dev

# Production (minimal logging, security hardened)
java -jar backend.jar -Dspring.profiles.active=prod
```

**Default profile**: If no profile is specified, base `application.yml` configuration is used (not recommended for production).

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
