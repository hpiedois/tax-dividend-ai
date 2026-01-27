# Architecture Tax Dividend AI

## 📐 Vue d'ensemble

```
┌────────────────────────────────────────────────────────────────────┐
│                          FRONTEND                                   │
│                     React 19 + Vite 7                              │
│                      localhost:5173                                 │
│                                                                     │
│  Responsabilités:                                                   │
│  - Interface utilisateur (UI/UX)                                   │
│  - Gestion d'état local (Jotai)                                    │
│  - Appels HTTP vers BFF Gateway UNIQUEMENT                         │
│  - Aucune logique métier                                           │
└──────────────────────────┬─────────────────────────────────────────┘
                           │
                           │ HTTP/REST (public)
                           │ CORS autorisé: localhost:5173
                           ↓
┌────────────────────────────────────────────────────────────────────┐
│                       BFF GATEWAY                                   │
│                   Spring Boot 3.5 WebFlux                          │
│                      localhost:8080                                 │
│                                                                     │
│  Responsabilités:                                                   │
│  ├─ Point d'entrée UNIQUE pour le frontend                        │
│  ├─ Orchestration des appels backend                              │
│  ├─ Transformation données (Backend → Frontend)                    │
│  ├─ Gestion JWT / Session                                          │
│  ├─ CORS, Rate Limiting, Caching                                   │
│  ├─ Agrégation de réponses multiples                              │
│  └─ Validation des requêtes                                        │
│                                                                     │
│  Controllers:                                                       │
│  ├─ AuthController       → Backend Auth Service                    │
│  ├─ DividendController   → Backend PDF Service                     │
│  └─ FormController       → Backend PDF + Storage Services          │
└──────────────────────────┬─────────────────────────────────────────┘
                           │
                           │ HTTP/REST (privé)
                           │ Réseau interne uniquement
                           ↓
┌────────────────────────────────────────────────────────────────────┐
│                      BACKEND SERVICES                               │
│                   Spring Boot 3.5 + JPA                            │
│                      localhost:8081                                 │
│                                                                     │
│  Services:                                                          │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │  AuthService                                                  │ │
│  │  - Login / Logout                                             │ │
│  │  - JWT generation & validation                                │ │
│  │  - User management                                            │ │
│  └──────────────────────────────────────────────────────────────┘ │
│                                                                     │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │  PDFService                                                   │ │
│  │  - Génération Form 5000 (Apache PDFBox)                       │ │
│  │  - Génération Form 5001 (Apache PDFBox)                       │ │
│  │  - Création ZIP                                               │ │
│  │  - Validation données fiscales                                │ │
│  └──────────────────────────────────────────────────────────────┘ │
│                                                                     │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │  StorageService                                               │ │
│  │  - Upload vers S3/MinIO                                       │ │
│  │  - Génération URLs présignées                                 │ │
│  │  - Gestion lifecycle des fichiers                            │ │
│  └──────────────────────────────────────────────────────────────┘ │
│                                                                     │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │  DividendService                                              │ │
│  │  - Parsing PDF relevés bancaires                              │ │
│  │  - Calculs fiscaux                                            │ │
│  │  - Validation ISIN                                            │ │
│  └──────────────────────────────────────────────────────────────┘ │
│                                                                     │
│  Database:                                                          │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │  PostgreSQL 16                                                │ │
│  │  - users, forms, dividends, submissions                       │ │
│  └──────────────────────────────────────────────────────────────┘ │
│                                                                     │
│  Storage:                                                           │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │  MinIO (dev) / AWS S3 (prod)                                  │ │
│  │  - PDFs générés                                               │ │
│  │  - Templates                                                   │ │
│  └──────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘
```

## 🔄 Flow d'une génération de formulaires

### **1. Requête Frontend → BFF**

```typescript
// Frontend: useFormGeneration.ts
const response = await fetch('http://localhost:8080/api/forms/generate', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${jwtToken}`
  },
  body: JSON.stringify({
    taxpayerName: "Jean Dupont",
    taxId: "CH-123456",
    dividends: [...]
  })
});
```

### **2. BFF Gateway traite la requête**

```java
// BFF: FormController.java
@PostMapping("/api/forms/generate")
public Mono<FormGenerationResponse> generateForms(
    @Valid @RequestBody FormGenerationRequest request,
    @AuthenticationPrincipal Jwt jwt
) {
    // 1. Valider JWT
    String userId = jwt.getSubject();

    // 2. Appeler Backend PDF Service
    return webClient.post()
        .uri("http://backend:8081/internal/pdf/generate")
        .header("X-User-Id", userId)
        .bodyValue(request)
        .retrieve()
        .bodyToMono(PdfGenerationResponse.class)
        .flatMap(pdfResponse -> {
            // 3. Transformer la réponse pour le frontend
            return Mono.just(new FormGenerationResponse(
                pdfResponse.getFormId(),
                pdfResponse.getDownloadUrl(), // URL présignée S3
                pdfResponse.getFileName()
            ));
        });
}
```

### **3. Backend traite la génération**

```java
// Backend: PDFController.java
@PostMapping("/internal/pdf/generate")
public ResponseEntity<PdfGenerationResponse> generatePdf(
    @RequestHeader("X-User-Id") String userId,
    @RequestBody FormGenerationRequest request
) {
    // 1. Générer les PDFs
    byte[] pdf5000 = pdfService.generateForm5000(request);
    byte[] pdf5001 = pdfService.generateForm5001(request);

    // 2. Créer ZIP
    byte[] zipFile = zipService.create(pdf5000, pdf5001);

    // 3. Upload S3
    String s3Key = storageService.upload(zipFile, userId);

    // 4. Sauvegarder en BDD
    GeneratedForm form = formRepository.save(new GeneratedForm(
        userId, s3Key, request.getTaxYear()
    ));

    // 5. Générer URL présignée (expire dans 1h)
    String downloadUrl = storageService.generatePresignedUrl(s3Key, 3600);

    return ResponseEntity.ok(new PdfGenerationResponse(
        form.getId(), downloadUrl, "formulaires-2024.zip"
    ));
}
```

### **4. Réponse au Frontend**

```typescript
// Frontend reçoit
{
  formId: "uuid-123",
  pdfUrl: "https://s3.amazonaws.com/tax-dividend/forms/user123/...?expires=...",
  fileName: "formulaires-2024.zip",
  generatedAt: "2024-01-26T21:00:00Z"
}

// Téléchargement automatique
window.location.href = response.pdfUrl;
```

## 📡 Contrats API

### **Frontend ↔ BFF Gateway**

**Base URL**: `http://localhost:8080/api`

#### **Endpoints publics**

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/login` | Authentification |
| POST | `/auth/register` | Inscription |

#### **Endpoints authentifiés** (require JWT)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/dividends/scan` | Scanner un relevé PDF |
| GET | `/dividends` | Liste des dividendes |
| POST | `/forms/generate` | Générer formulaires 5000+5001 |
| GET | `/forms` | Historique des formulaires |
| GET | `/forms/{id}` | Détails d'un formulaire |

### **BFF Gateway ↔ Backend**

**Base URL**: `http://backend:8081/internal`

**Headers requis**: `X-User-Id: <userId>`

#### **Endpoints internes** (non exposés publiquement)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/validate-token` | Valider un JWT |
| GET | `/users/{id}` | Infos utilisateur |
| POST | `/pdf/generate` | Générer PDFs |
| POST | `/pdf/parse` | Parser relevé PDF |
| POST | `/storage/upload` | Upload S3 |
| GET | `/storage/presigned-url` | Générer URL S3 |

## 🔐 Sécurité

### **Frontend**

```typescript
// Store JWT in memory (not localStorage for security)
let jwtToken: string | null = null;

async function login(email: string, password: string) {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, password })
  });

  const { token } = await response.json();
  jwtToken = token; // Store in memory
}

// Interceptor pour ajouter le token
async function authenticatedFetch(url: string, options: RequestInit) {
  return fetch(url, {
    ...options,
    headers: {
      ...options.headers,
      'Authorization': `Bearer ${jwtToken}`
    }
  });
}
```

### **BFF Gateway**

```java
// SecurityConfig.java
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(auth -> auth
                .pathMatchers("/api/auth/**").permitAll()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        return request -> config;
    }
}
```

### **Backend**

```java
// Sécurité interne : vérifier X-User-Id header
@Component
public class InternalSecurityFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, ...) {
        String userId = request.getHeader("X-User-Id");
        if (userId == null) {
            throw new UnauthorizedException("Missing X-User-Id header");
        }
        // Continuer la requête
    }
}
```

## 📊 Base de données

### **Schema PostgreSQL**

```sql
-- Users
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    tax_id VARCHAR(50),
    city VARCHAR(100),
    postal_code VARCHAR(20),
    address TEXT,
    country VARCHAR(100) DEFAULT 'Suisse',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Generated Forms
CREATE TABLE generated_forms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    s3_key VARCHAR(500) NOT NULL,
    file_name VARCHAR(255),
    tax_year INTEGER NOT NULL,
    status VARCHAR(50) DEFAULT 'GENERATED',
    created_at TIMESTAMP DEFAULT NOW(),
    expires_at TIMESTAMP
);

-- Dividends
CREATE TABLE dividends (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    form_id UUID REFERENCES generated_forms(id) ON DELETE CASCADE,
    security_name VARCHAR(255) NOT NULL,
    isin VARCHAR(12),
    payment_date DATE NOT NULL,
    gross_amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'EUR',
    withholding_tax DECIMAL(10,2) NOT NULL,
    treaty_amount DECIMAL(10,2),
    reclaimable_amount DECIMAL(10,2) NOT NULL,
    french_rate DECIMAL(5,2)
);

-- Form Submissions (tracking)
CREATE TABLE form_submissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    form_id UUID REFERENCES generated_forms(id) ON DELETE CASCADE,
    submitted_at TIMESTAMP DEFAULT NOW(),
    submission_method VARCHAR(50), -- EMAIL, API, POSTAL
    tracking_number VARCHAR(100),
    status VARCHAR(50) DEFAULT 'PENDING' -- PENDING, APPROVED, REJECTED
);

-- Indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_forms_user_id ON generated_forms(user_id);
CREATE INDEX idx_forms_tax_year ON generated_forms(tax_year);
CREATE INDEX idx_dividends_form_id ON dividends(form_id);
```

## 🚀 Démarrage du projet

### **1. PostgreSQL**

```bash
docker run -d \
  --name tax-dividend-db \
  -e POSTGRES_DB=taxdividend \
  -e POSTGRES_USER=taxdividend \
  -e POSTGRES_PASSWORD=secret \
  -p 5432:5432 \
  postgres:16
```

### **2. MinIO (S3 local)**

```bash
docker run -d \
  --name tax-dividend-minio \
  -p 9000:9000 \
  -p 9001:9001 \
  -e MINIO_ROOT_USER=minioadmin \
  -e MINIO_ROOT_PASSWORD=minioadmin \
  minio/minio server /data --console-address ":9001"
```

### **3. Backend**

```bash
cd backend
./mvnw spring-boot:run
# → http://localhost:8081
```

### **4. BFF Gateway**

```bash
cd bff-gateway
./mvnw spring-boot:run
# → http://localhost:8080
```

### **5. Frontend**

```bash
cd frontend
npm run dev
# → http://localhost:5173
```

## 📝 Variables d'environnement

### **BFF Gateway** (`bff-gateway/src/main/resources/application.yml`)

```yaml
server:
  port: 8080

backend:
  url: http://localhost:8081

spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          public-key-location: classpath:public-key.pem
```

### **Backend** (`backend/src/main/resources/application.yml`)

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/taxdividend
    username: taxdividend
    password: secret

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true

storage:
  s3:
    endpoint: http://localhost:9000
    bucket: tax-dividend-forms
    access-key: minioadmin
    secret-key: minioadmin
```

## 🔜 Prochaines étapes

### **Phase 1 : Setup Backend** (cette semaine)
1. ✅ Initialiser projet Spring Boot Backend
2. ✅ Configurer PostgreSQL + JPA
3. ✅ Configurer MinIO/S3
4. ✅ Créer les services de base
5. ✅ Tests unitaires

### **Phase 2 : Migration PDF** (semaine prochaine)
1. Migrer génération PDF vers Backend
2. Intégrer Apache PDFBox
3. Créer endpoints BFF → Backend
4. Adapter Frontend

### **Phase 3 : Features avancées** (après)
1. Parser PDF relevés bancaires
2. Signature électronique
3. Workflow de soumission
4. Dashboard B2B

---

**Note importante** : Cette architecture permet de :
- ✅ Séparer frontend/backend clairement
- ✅ Protéger les services backend
- ✅ Faciliter le scaling horizontal
- ✅ Simplifier les tests
- ✅ Préparer le passage en production
