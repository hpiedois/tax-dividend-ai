# BFF Gateway - Tax Dividend AI

**Backend For Frontend** gateway using Spring Boot WebFlux (reactive).

## Purpose

The BFF Gateway is the **single entry point** for the frontend application. It handles:

- ✅ CORS configuration for frontend
- ✅ JWT token validation
- ✅ Rate limiting & throttling
- ✅ Request orchestration (calling multiple backend services)
- ✅ Response transformation (Backend → Frontend DTOs)
- ✅ Caching frequently accessed data
- ✅ API aggregation

**Security**: The backend services are NOT exposed publicly, only the BFF Gateway is.

## Architecture

```
Frontend (React)
    ↓ HTTP/REST (CORS enabled)
BFF Gateway (this project) :8080
    ↓ HTTP/REST (internal network)
Backend Services :8081
```

## Tech Stack

- **Framework**: Spring Boot 3.5+ with WebFlux (reactive/async)
- **Security**: Spring Security + JWT (OAuth2 Resource Server)
- **Build**: Maven
- **Java**: 21
- **API**: RESTful + OpenAPI spec

## Prerequisites

- Java 21+
- Maven 3.8+
- Docker & Docker Compose (for infrastructure)

## Development Setup

### 1. Start Infrastructure

```bash
cd ../infrastructure
docker-compose up -d
```

This starts PostgreSQL, MinIO, and pgAdmin.

### 2. Run BFF Gateway

```bash
cd bff-gateway
./mvnw spring-boot:run
```

Or with your IDE (Run `TaxDividendBffGatewayApplication.java`).

**Default port**: `8080`

### 3. Check Health

```bash
curl http://localhost:8080/actuator/health
```

Should return `{"status":"UP"}`.

## Configuration

Configuration is in `src/main/resources/application.yml`.

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `BACKEND_URL` | `http://localhost:8081` | Backend services base URL |
| `JWT_PUBLIC_KEY` | (embedded) | RSA public key for JWT validation |
| `JWT_PRIVATE_KEY` | (embedded) | RSA private key for JWT signing |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Frontend URL |

For production, use environment variables or Spring Cloud Config.

## API Endpoints

### Public Endpoints (no auth required)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/login` | Login (returns JWT) |
| POST | `/api/auth/register` | Register new user |
| GET | `/actuator/health` | Health check |

### Protected Endpoints (JWT required)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/dividends/parse` | Upload PDF, parse dividends |
| GET | `/api/dividends` | List parsed dividends |
| POST | `/api/forms/generate` | Generate Forms 5000/5001 |
| GET | `/api/forms` | List generated forms |
| GET | `/api/forms/{id}` | Get form details |
| GET | `/api/forms/{id}/download` | Download form (pre-signed URL) |

## Testing

### Run Unit Tests

```bash
./mvnw test
```

### Run Integration Tests

```bash
./mvnw verify
```

### Manual Testing with curl

```bash
# 1. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# Response: {"token":"eyJhbGciOiJSUzI1Ni...","expiresIn":3600}

# 2. Use token for authenticated requests
export TOKEN="eyJhbGciOiJSUzI1Ni..."

curl http://localhost:8080/api/dividends \
  -H "Authorization: Bearer $TOKEN"
```

## Building for Production

```bash
./mvnw clean package -DskipTests
```

Generates `target/bff-gateway-0.0.1-SNAPSHOT.jar`.

### Run JAR

```bash
java -jar target/bff-gateway-0.0.1-SNAPSHOT.jar
```

### Docker Build

```bash
docker build -t tax-dividend-bff:latest .
docker run -p 8080:8080 \
  -e BACKEND_URL=http://backend:8081 \
  tax-dividend-bff:latest
```

## Code Structure

```
src/main/java/com/taxdividend/bff/
├── config/
│   ├── BackendClientConfig.java      # WebClient for backend calls
│   └── CorsConfig.java                # CORS configuration
├── controller/
│   ├── AuthController.java            # /api/auth/*
│   ├── DividendController.java        # /api/dividends/*
│   └── FormController.java            # /api/forms/*
├── security/
│   ├── SecurityConfig.java            # Spring Security config
│   ├── TokenService.java              # JWT creation/validation
│   └── RsaKeyProperties.java          # RSA keys for JWT
└── TaxDividendBffGatewayApplication.java  # Main class
```

## Security Notes

### JWT Authentication

- **Algorithm**: RS256 (RSA with SHA-256)
- **Expiration**: 1 hour (configurable)
- **Issuer**: `tax-dividend-bff`
- **Keys**: RSA 2048-bit (embedded for dev, external for prod)

### CORS Configuration

Only `http://localhost:5173` is allowed by default (frontend dev server).

For production, configure allowed origins in `application-prod.yml`.

### Rate Limiting

⚠️ **TODO**: Implement rate limiting (e.g., Bucket4j with Redis).

## Monitoring

### Actuator Endpoints

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Health status |
| `/actuator/info` | Application info |
| `/actuator/metrics` | Metrics |

For production, secure these endpoints or expose only `/health`.

## Troubleshooting

### "Connection refused" when calling backend

- Check that backend is running on port 8081
- Verify `BACKEND_URL` configuration
- Check Docker network if using containers

### CORS errors in browser

- Verify frontend URL in CORS config
- Check that `Access-Control-Allow-Origin` header is present
- Try disabling browser CORS for testing (not for prod!)

### "Invalid JWT" errors

- Check that JWT is not expired
- Verify public key matches private key used for signing
- Check `Authorization: Bearer <token>` header format

## Related Projects

- **Frontend**: `../frontend/` - React + Vite UI
- **Backend**: `../backend/` - Business logic services
- **Infrastructure**: `../infrastructure/` - Docker Compose setup

## Further Reading

- [Spring WebFlux Docs](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [Spring Security OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
- [BFF Pattern](https://samnewman.io/patterns/architectural/bff/)
