# Changelog - Backend

## [0.2.0] - 2026-01-28

### 🎉 Major Upgrade - Spring Boot 4.0.2 + Observability

#### Added
- **Spring Boot 4.0.2** (upgraded from 3.5.8)
- **Spring Boot Actuator** - Health checks, metrics, info endpoints
- **Prometheus metrics** - Full observability with `/actuator/prometheus`
- **OpenTelemetry** - Distributed tracing support
  - OTLP exporter
  - Micrometer tracing bridge
  - Service name, version, environment tags
- **Lombok** - Reduce boilerplate code
  - `@Slf4j` for logging
  - Annotation processing configured
- **Enhanced logging**
  - DEBUG levels for app, security, Hibernate
  - TRACE for SQL parameter binding
  - Full stack traces
  - Formatted console output
- **API Documentation** - SpringDoc OpenAPI
  - Swagger UI at `/swagger-ui.html`
  - OpenAPI schema at `/v3/api-docs`

#### Changed
- **InternalSecurityConfig**
  - Now uses `@Slf4j` for logging
  - Improved public endpoint detection
  - Clear documentation of security model
  - Health and Prometheus are **public** (no auth required)
  - All other actuator endpoints require authentication
  - Added `@EnableMethodSecurity` for method-level security
  - Stateless session management
- **application.yml**
  - Complete actuator configuration
  - Prometheus metrics with tags
  - HikariCP connection pool monitoring
  - HTTP metrics with percentiles
  - OpenTelemetry tracing config
  - Structured logging configuration
  - Application-specific properties (PDF, tax rules, forms)
- **pom.xml**
  - Upgraded Spring Boot to 4.0.2
  - Added actuator dependency
  - Added Prometheus registry
  - Added OpenTelemetry dependencies
  - Added Lombok with annotation processing
  - Added SpringDoc OpenAPI
  - Added TestContainers for Postgres

#### Database
- **Consolidated migrations** - Single `V1__init_schema.sql`
  - Removed duplicate V1 and V2 migration files
  - All 6 tables in one file
  - Includes initial tax rules data
  - Clean, well-documented schema

#### Scripts
- **dev-setup.sh** - First-time setup (infrastructure + Maven)
- **run-dev.sh** - Start backend with auto-infra check
- **run-tests.sh** - Run unit/integration/all tests
- **reset-db.sh** - Reset database (dev/uat)

#### Documentation
- Updated README with new scripts
- Added Flyway migration strategy
- Documented monitoring endpoints
- Added PROJECT_STATUS.md tracking

### Security Notes
**Public Endpoints** (no authentication):
- `/actuator/health` and `/actuator/health/**` - Health checks
- `/actuator/prometheus` - Prometheus metrics scraping
- `/swagger-ui/**` - API documentation
- `/v3/api-docs/**` - OpenAPI schema
- `/error` - Error handling

**Protected Endpoints** (require X-User-Id header):
- All `/actuator/**` (except health and prometheus)
- All application endpoints

### Breaking Changes
None - This is an upgrade, not a rewrite.

---

## [0.1.0] - 2026-01-27

### Initial Release

#### Added
- Spring Boot 3.5.8 project structure
- PostgreSQL with Flyway migrations
- Spring Security basic configuration
- MinIO S3 storage integration
- Apache PDFBox for PDF processing
- Basic JPA entities (User, Form, TaxCalculationResultDtoDto)

#### Database
- 9 migration files (later consolidated to 1)
- Users, forms, dividends, submissions, audit_logs, tax_rules tables

---

## Migration Guide

### From 0.1.0 to 0.2.0

**No breaking changes!** Just pull latest code and run:

```bash
cd backend
./mvnw clean install  # Download new dependencies
./run-dev.sh          # Start backend
```

**New Endpoints:**
- Health check: `http://localhost:8081/actuator/health`
- Prometheus: `http://localhost:8081/actuator/prometheus`
- Swagger UI: `http://localhost:8081/swagger-ui.html`

**Logging:**
- Now uses SLF4J via Lombok `@Slf4j`
- DEBUG level for all application code
- SQL queries and parameters visible

---

## Version History

| Version | Date | Spring Boot | Key Features |
|---------|------|-------------|--------------|
| 0.2.0 | 2026-01-28 | **4.0.2** | Actuator, Prometheus, OpenTelemetry, Lombok |
| 0.1.0 | 2026-01-27 | 3.5.8 | Initial project structure |

---

## Next Steps

### Planned for 0.3.0
- [ ] PDF parsing service implementation
- [ ] Tax calculation engine
- [ ] PDF generation service (Forms 5000/5001)
- [ ] S3/MinIO storage service
- [ ] REST controllers for internal API
- [ ] Unit tests (80% coverage target)

### Planned for 0.4.0
- [ ] Integration tests with TestContainers
- [ ] Performance optimization
- [ ] Caching layer (Redis)
- [ ] Error handling improvements

---

**Maintained by**: Tax TaxCalculationResultDtoDto AI Team
**Last Updated**: 2026-01-28
