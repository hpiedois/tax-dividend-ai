# 🚀 ROADMAP PRODUCTION - TAX DIVIDEND AI

**Version**: 1.0
**Date**: 27 Janvier 2026
**Objectif**: MVP Production-Ready en 13-17 semaines

---

## 📋 VUE D'ENSEMBLE

### État Actuel vs Cible

| Composant | État Actuel | État Cible | Gap |
|-----------|-------------|------------|-----|
| **Frontend** | 80% (UI mockée) | 100% (Connecté API) | 20% |
| **BFF Gateway** | 20% (Skeleton) | 100% (Functional) | 80% |
| **Backend** | 10% (Empty services) | 100% (Full logic) | 90% |
| **Tests** | 0% | 70%+ coverage | 100% |
| **PDF Generation** | 0% | 100% (Real forms) | 100% |
| **PDF Parsing** | 0% (Mock) | 95%+ accuracy | 100% |
| **CI/CD** | 0% | Automated deploy | 100% |
| **Monitoring** | 0% | Full observability | 100% |

### Timeline Globale

```
┌────────────────────────────────────────────────────────────────┐
│                     GANTT CHART                                 │
├────────────────────────────────────────────────────────────────┤
│ Semaine 1-2   │ PHASE 0: Stabilisation          │              │
│ Semaine 3-6   │ PHASE 1: Backend Core           │              │
│ Semaine 7-9   │ PHASE 2: Integration            │              │
│ Semaine 10-13 │ PHASE 3: PDF Features           │              │
│ Semaine 14-16 │ PHASE 4: Production Ready       │              │
│ Semaine 17    │ PHASE 5: Launch                 │              │
└────────────────────────────────────────────────────────────────┘
```

### Budget & Ressources

| Ressource | Allocation | Coût Total |
|-----------|-----------|------------|
| Dev Full-Stack Senior | 17 semaines (100%) | €51,000 |
| DevOps Engineer | 4 semaines (40%) | €4,400 |
| QA Engineer | 5 semaines (60%) | €7,500 |
| Infrastructure (GCP) | 4 mois | €215 |
| Outils (Sentry, etc.) | 4 mois | €152 |
| **TOTAL** | **17 semaines** | **€63,267** |

---

## 🎯 PHASE 0: STABILISATION (Semaines 1-2)

**Objectif**: Corriger les bugs critiques et poser les fondations qualité

### ✅ CHECKLIST SEMAINE 1

#### **Jour 1: Corrections ESLint** (4h)

- [ ] **Tâche 1.1**: Corriger `App.tsx:63-64` (case block)
  ```typescript
  // AVANT
  case 'scan':
    const scanContent = scanStep === 'upload' ? ...

  // APRÈS
  case 'scan': {
    const scanContent = scanStep === 'upload' ? ...
    return scanContent;
  }
  ```
  **Assigné**: Dev Frontend
  **Estimation**: 1h

- [ ] **Tâche 1.2**: Refactor `useTheme` export
  ```typescript
  // Créer src/hooks/useTheme.ts
  export function useTheme() {
    const context = useContext(ThemeProviderContext);
    if (!context) throw new Error("useTheme must be used within ThemeProvider");
    return context;
  }
  ```
  **Assigné**: Dev Frontend
  **Estimation**: 30min

- [ ] **Tâche 1.3**: Vérifier 0 erreurs ESLint
  ```bash
  cd frontend && npm run lint
  # Expected: ✓ 0 errors
  ```
  **Assigné**: Dev Frontend
  **Estimation**: 15min

#### **Jour 2: Setup Testing** (6h)

- [ ] **Tâche 2.1**: Installer Vitest
  ```bash
  cd frontend
  npm install -D vitest @testing-library/react @testing-library/jest-dom jsdom @testing-library/user-event
  ```
  **Assigné**: Dev Frontend
  **Estimation**: 30min

- [ ] **Tâche 2.2**: Configurer Vitest
  ```typescript
  // vitest.config.ts
  import { defineConfig } from 'vitest/config';
  import react from '@vitejs/plugin-react';
  import path from 'path';

  export default defineConfig({
    plugins: [react()],
    test: {
      environment: 'jsdom',
      globals: true,
      setupFiles: './src/test/setup.ts',
      coverage: {
        provider: 'v8',
        reporter: ['text', 'json', 'html'],
        exclude: ['node_modules/', 'src/test/'],
      },
    },
    resolve: {
      alias: {
        '@': path.resolve(__dirname, './src'),
      },
    },
  });
  ```
  **Assigné**: Dev Frontend
  **Estimation**: 1h

- [ ] **Tâche 2.3**: Créer setup file
  ```typescript
  // src/test/setup.ts
  import '@testing-library/jest-dom';
  import { expect, afterEach, vi } from 'vitest';
  import { cleanup } from '@testing-library/react';

  afterEach(() => {
    cleanup();
  });

  // Mock i18next
  vi.mock('react-i18next', () => ({
    useTranslation: () => ({
      t: (key: string) => key,
      i18n: { changeLanguage: vi.fn() },
    }),
  }));
  ```
  **Assigné**: Dev Frontend
  **Estimation**: 30min

- [ ] **Tâche 2.4**: Écrire 3 tests UI de base
  ```typescript
  // src/components/ui/Button.test.tsx
  import { render, screen } from '@testing-library/react';
  import userEvent from '@testing-library/user-event';
  import { Button } from './Button';

  describe('Button', () => {
    it('renders children', () => {
      render(<Button>Click me</Button>);
      expect(screen.getByText('Click me')).toBeInTheDocument();
    });

    it('calls onClick when clicked', async () => {
      const handleClick = vi.fn();
      render(<Button onClick={handleClick}>Click</Button>);
      await userEvent.click(screen.getByText('Click'));
      expect(handleClick).toHaveBeenCalledOnce();
    });

    it('is disabled when disabled prop is true', () => {
      render(<Button disabled>Click</Button>);
      expect(screen.getByRole('button')).toBeDisabled();
    });
  });
  ```
  **Assigné**: Dev Frontend
  **Estimation**: 2h

#### **Jour 3: Documentation Règles Fiscales** (4h)

⚠️ **IMPORTANT**: Les calculs fiscaux doivent être **UNIQUEMENT côté backend** pour des raisons de sécurité, audit et maintenance.

- [ ] **Tâche 3.1**: Créer documentation règles fiscales
  ```markdown
  // docs/TAX_RULES.md

  # Règles Fiscales - France-Suisse

  ## Taux Applicables

  ### France (Source Country)
  - **PFU (Flat Tax)**: 12.8% + 17.2% social = 30% total
    - Seuls 12.8% sont réclamables via convention
  - **Barème Progressif**: 25% standard (peut varier selon TMI)
  - **PEA**: 0% (pas de retenue à la source)

  ### Suisse (Residence Country)
  - **Convention fiscale**: 15% maximum

  ## Formule de Calcul

  ```
  Montant réclamable = (Taux FR - Taux Convention) × Montant brut

  Exemple CTO avec PFU:
  - Brut: 1000€
  - Retenue FR: 1000 × 12.8% = 128€
  - Retenue convention: 1000 × 15% = 150€
  - Réclamable: 128 - 150 = -22€ → 0€ (pas de réclamation)

  Exemple CTO avec Barème Progressif:
  - Brut: 1000€
  - Retenue FR: 1000 × 25% = 250€
  - Retenue convention: 1000 × 15% = 150€
  - Réclamable: 250 - 150 = 100€
  ```

  ## Cas Spéciaux
  - PEA: Aucune réclamation (pas de retenue)
  - Assurance-vie: Règles différentes
  - Actions US dans compte français: Double convention
  ```
  **Assigné**: Dev Backend + Expert Fiscal
  **Estimation**: 3h

- [ ] **Tâche 3.2**: Validation avec expert-comptable
  - Consulter fiscaliste spécialisé France-Suisse
  - Valider tous les taux et cas limites
  - Obtenir sources officielles (BOI, impots.gouv.fr)
  **Assigné**: Product Owner
  **Estimation**: 1 réunion

- [ ] **Tâche 3.3**: Mettre à jour `mock-parser.ts` avec valeurs correctes
  ```typescript
  // ⚠️ TEMPORAIRE - Mock seulement
  // En production, le BACKEND calculera ces valeurs

  export const parseDividendPDF = async (file: File): Promise<Dividend> => {
    await new Promise(resolve => setTimeout(resolve, 1500));

    const grossAmount = parseFloat((Math.random() * 500 + 100).toFixed(2));

    // Mock calculation (backend fera le vrai calcul)
    const withholdingTax = Number((grossAmount * 0.25).toFixed(2)); // 25% progressif
    const treatyAmount = Number((grossAmount * 0.15).toFixed(2)); // 15% convention
    const reclaimableAmount = Number((withholdingTax - treatyAmount).toFixed(2));

    return {
      securityName: mockSecurities[Math.floor(Math.random() * mockSecurities.length)],
      isin: mockISINs[Math.floor(Math.random() * mockISINs.length)],
      grossAmount,
      currency: 'EUR',
      paymentDate: mockDate(),
      withholdingTax,
      reclaimableAmount: Math.max(0, reclaimableAmount),
    };
  };
  ```
  **Assigné**: Dev Frontend
  **Estimation**: 30min

#### **Jour 4: Validation Stricte** (4h)

- [ ] **Tâche 4.1**: Créer `lib/validation.ts`
  ```typescript
  const ALLOWED_MIME_TYPES = ['application/pdf'] as const;
  const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

  export interface FileValidationError {
    code: 'INVALID_TYPE' | 'FILE_TOO_LARGE' | 'FILE_EMPTY';
    message: string;
  }

  export function validatePDFFile(file: File): FileValidationError | null {
    if (!ALLOWED_MIME_TYPES.includes(file.type as any)) {
      return {
        code: 'INVALID_TYPE',
        message: 'Le fichier doit être au format PDF',
      };
    }

    if (file.size === 0) {
      return {
        code: 'FILE_EMPTY',
        message: 'Le fichier est vide',
      };
    }

    if (file.size > MAX_FILE_SIZE) {
      return {
        code: 'FILE_TOO_LARGE',
        message: `Le fichier est trop volumineux (max ${MAX_FILE_SIZE / 1024 / 1024}MB)`,
      };
    }

    return null;
  }

  export function validateISIN(isin: string): boolean {
    // Format: 2 letters + 9 alphanumeric + 1 check digit
    if (!/^[A-Z]{2}[A-Z0-9]{9}[0-9]$/.test(isin)) {
      return false;
    }

    // Luhn checksum validation
    const digits = isin.split('').map(c => {
      const code = c.charCodeAt(0);
      return code >= 65 && code <= 90 ? code - 55 : parseInt(c);
    });

    let sum = 0;
    let shouldDouble = digits.length % 2 === 0;

    for (const digit of digits) {
      let value = digit;
      if (shouldDouble) {
        value *= 2;
        if (value > 9) value -= 9;
      }
      sum += value;
      shouldDouble = !shouldDouble;
    }

    return sum % 10 === 0;
  }
  ```
  **Assigné**: Dev Backend/Frontend
  **Estimation**: 2h

- [ ] **Tâche 4.2**: Appliquer validation dans DropZone
  ```typescript
  // components/upload/DropZone.tsx
  import { validatePDFFile } from '@/lib/validation';

  const handleDrop = (acceptedFiles: File[]) => {
    const validFiles: File[] = [];
    const errors: string[] = [];

    for (const file of acceptedFiles) {
      const error = validatePDFFile(file);
      if (error) {
        errors.push(`${file.name}: ${error.message}`);
      } else {
        validFiles.push(file);
      }
    }

    if (errors.length > 0) {
      setError(errors.join('\n'));
    }

    if (validFiles.length > 0) {
      onFilesSelect(validFiles);
    }
  };
  ```
  **Assigné**: Dev Frontend
  **Estimation**: 1h

#### **Jour 5: CI/CD Setup** (6h)

- [ ] **Tâche 5.1**: Créer `.github/workflows/ci.yml`
  ```yaml
  name: CI

  on:
    push:
      branches: [main, develop]
    pull_request:
      branches: [main]

  jobs:
    test-frontend:
      runs-on: ubuntu-latest
      steps:
        - uses: actions/checkout@v4

        - name: Setup Node.js
          uses: actions/setup-node@v4
          with:
            node-version: '20'
            cache: 'npm'
            cache-dependency-path: frontend/package-lock.json

        - name: Install dependencies
          run: cd frontend && npm ci

        - name: Lint
          run: cd frontend && npm run lint

        - name: Type check
          run: cd frontend && npx tsc --noEmit

        - name: Run tests
          run: cd frontend && npm run test:ci

        - name: Build
          run: cd frontend && npm run build

    test-backend:
      runs-on: ubuntu-latest
      services:
        postgres:
          image: postgres:16
          env:
            POSTGRES_DB: taxdividend_test
            POSTGRES_USER: test
            POSTGRES_PASSWORD: test
          options: >-
            --health-cmd pg_isready
            --health-interval 10s
            --health-timeout 5s
            --health-retries 5
          ports:
            - 5432:5432

      steps:
        - uses: actions/checkout@v4

        - name: Setup Java
          uses: actions/setup-java@v4
          with:
            distribution: 'temurin'
            java-version: '21'
            cache: 'maven'

        - name: Test Backend
          run: cd backend && ./mvnw clean verify
          env:
            SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/taxdividend_test
            SPRING_DATASOURCE_USERNAME: test
            SPRING_DATASOURCE_PASSWORD: test

        - name: Test BFF
          run: cd bff-gateway && ./mvnw clean verify
  ```
  **Assigné**: DevOps
  **Estimation**: 3h

- [ ] **Tâche 5.2**: Ajouter npm script `test:ci`
  ```json
  // frontend/package.json
  {
    "scripts": {
      "test": "vitest",
      "test:ci": "vitest run --coverage",
      "test:ui": "vitest --ui"
    }
  }
  ```
  **Assigné**: Dev Frontend
  **Estimation**: 15min

---

### ✅ CHECKLIST SEMAINE 2

#### **Jour 1-2: Tests Backend Setup** (10h)

- [ ] **Tâche 6.1**: Setup TestContainers
  ```xml
  <!-- backend/pom.xml -->
  <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>testcontainers</artifactId>
      <version>1.19.3</version>
      <scope>test</scope>
  </dependency>
  <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>postgresql</artifactId>
      <version>1.19.3</version>
      <scope>test</scope>
  </dependency>
  ```
  **Assigné**: Dev Backend
  **Estimation**: 1h

- [ ] **Tâche 6.2**: Base test class
  ```java
  // backend/src/test/java/com/taxdividend/backend/BaseIntegrationTest.java
  @SpringBootTest
  @ActiveProfiles("test")
  @Testcontainers
  public abstract class BaseIntegrationTest {

      @Container
      static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
          .withDatabaseName("taxdividend_test")
          .withUsername("test")
          .withPassword("test");

      @DynamicPropertySource
      static void configureProperties(DynamicPropertyRegistry registry) {
          registry.add("spring.datasource.url", postgres::getJdbcUrl);
          registry.add("spring.datasource.username", postgres::getUsername);
          registry.add("spring.datasource.password", postgres::getPassword);
      }
  }
  ```
  **Assigné**: Dev Backend
  **Estimation**: 2h

- [ ] **Tâche 6.3**: Tests UserRepository
  ```java
  @DataJpaTest
  class UserRepositoryTest extends BaseIntegrationTest {

      @Autowired
      private UserRepository userRepository;

      @Test
      void shouldSaveUser() {
          User user = new User();
          user.setEmail("test@example.com");
          user.setPasswordHash("hashedpassword");
          user.setFullName("Test User");

          User saved = userRepository.save(user);

          assertThat(saved.getId()).isNotNull();
          assertThat(saved.getEmail()).isEqualTo("test@example.com");
      }

      @Test
      void shouldFindByEmail() {
          User user = new User();
          user.setEmail("find@example.com");
          user.setPasswordHash("hash");
          userRepository.save(user);

          Optional<User> found = userRepository.findByEmail("find@example.com");

          assertThat(found).isPresent();
          assertThat(found.get().getEmail()).isEqualTo("find@example.com");
      }
  }
  ```
  **Assigné**: Dev Backend
  **Estimation**: 2h

#### **Jour 3-4: Documentation** (8h)

- [ ] **Tâche 7.1**: Créer `GETTING_STARTED.md`
  ```markdown
  # Getting Started - Tax Dividend AI

  ## Prerequisites

  - Node.js 20+
  - Java 21+
  - Docker & Docker Compose
  - PostgreSQL 16 (optional, can use Docker)

  ## Quick Start

  ### 1. Start Infrastructure

  ```bash
  docker-compose up -d postgres minio mailhog
  ```

  ### 2. Start Backend

  ```bash
  cd backend
  ./mvnw spring-boot:run
  ```

  ### 3. Start BFF Gateway

  ```bash
  cd bff-gateway
  ./mvnw spring-boot:run
  ```

  ### 4. Start Frontend

  ```bash
  cd frontend
  npm install
  npm run dev
  ```

  Open http://localhost:5173
  ```
  **Assigné**: Dev Full-Stack
  **Estimation**: 2h

- [ ] **Tâche 7.2**: Mettre à jour README principal
  **Assigné**: Dev Full-Stack
  **Estimation**: 1h

- [ ] **Tâche 7.3**: Créer TROUBLESHOOTING.md
  **Assigné**: Dev Full-Stack
  **Estimation**: 2h

#### **Jour 5: Review & Planning Phase 1** (6h)

- [ ] **Tâche 8.1**: Code review semaine 1-2
- [ ] **Tâche 8.2**: Rétrospective
- [ ] **Tâche 8.3**: Planification détaillée Phase 1
- [ ] **Tâche 8.4**: Setup project board (GitHub Projects)

---

### 📊 MÉTRIQUES PHASE 0

**Critères de Succès**:
- ✅ 0 erreurs ESLint
- ✅ 20-30 tests frontend (coverage >50%)
- ✅ 10-15 tests backend (coverage >40%)
- ✅ CI/CD pipeline fonctionnel (all green)
- ✅ Calculs fiscaux corrects (validés par tests)
- ✅ Documentation complète (Getting Started + Troubleshooting)

**Livrables**:
- [x] Code sans erreurs lint
- [x] Testing infrastructure
- [x] Calculs fiscaux corrects
- [x] Validation fichiers stricte
- [x] CI/CD pipeline
- [x] Documentation

---

## 🔧 PHASE 1: BACKEND CORE (Semaines 3-6)

**Objectif**: Implémenter les services backend critiques

### 📝 BACKLOG PRIORISÉ

#### **Epic 1: PDF Generation** (Story Points: 40)

**User Story**: En tant qu'utilisateur, je veux générer des formulaires 5000 et 5001 pré-remplis pour faciliter ma réclamation fiscale.

**Tâches**:

- [ ] **Story 1.1**: Charger template PDF Form 5000 (8 SP)
  - Télécharger formulaire officiel depuis impots.gouv.fr
  - Analyser structure PDF (AcroForm fields)
  - Intégrer template dans `backend/src/main/resources/templates/`
  **Estimation**: 1 jour

- [ ] **Story 1.2**: Remplir Form 5000 avec Apache PDFBox (13 SP)
  ```java
  @Service
  public class Form5000Generator {
      public byte[] generate(Form5000Data data) throws IOException {
          InputStream templateStream = getClass().getResourceAsStream("/templates/5000.pdf");
          PDDocument document = PDDocument.load(templateStream);
          PDAcroForm form = document.getDocumentCatalog().getAcroForm();

          // Fill fields
          form.getField("nom").setValue(data.getFullName());
          form.getField("adresse").setValue(data.getAddress());
          form.getField("codePostal").setValue(data.getPostalCode());
          form.getField("ville").setValue(data.getCity());
          form.getField("pays").setValue(data.getCountry());
          form.getField("nif").setValue(data.getTaxId());
          form.getField("annee").setValue(String.valueOf(data.getTaxYear()));

          // Flatten form (make non-editable)
          form.flatten();

          ByteArrayOutputStream output = new ByteArrayOutputStream();
          document.save(output);
          document.close();

          return output.toByteArray();
      }
  }
  ```
  **Estimation**: 2 jours

- [ ] **Story 1.3**: Générer Form 5001 avec tableau dividendes (13 SP)
  ```java
  @Service
  public class Form5001Generator {
      public byte[] generate(Form5001Data data) throws IOException {
          PDDocument document = PDDocument.load(template5001);
          PDAcroForm form = document.getDocumentCatalog().getAcroForm();

          // Header fields
          form.getField("nom").setValue(data.getFullName());
          form.getField("annee").setValue(String.valueOf(data.getTaxYear()));

          // Dividends table
          PDPage page = document.getPage(0);
          PDPageContentStream contentStream = new PDPageContentStream(
              document, page, PDPageContentStream.AppendMode.APPEND, true
          );

          float y = 600; // Starting Y position
          for (Dividend div : data.getDividends()) {
              contentStream.beginText();
              contentStream.setFont(PDType1Font.HELVETICA, 10);
              contentStream.newLineAtOffset(50, y);
              contentStream.showText(div.getSecurityName());
              contentStream.newLineAtOffset(150, 0);
              contentStream.showText(div.getIsin());
              contentStream.newLineAtOffset(100, 0);
              contentStream.showText(formatAmount(div.getGrossAmount()));
              contentStream.endText();
              y -= 20;
          }

          contentStream.close();
          // ... total calculations

          ByteArrayOutputStream output = new ByteArrayOutputStream();
          document.save(output);
          document.close();

          return output.toByteArray();
      }
  }
  ```
  **Estimation**: 2 jours

- [ ] **Story 1.4**: Tests PDF generation (8 SP)
  ```java
  @Test
  void shouldGenerateForm5000() throws Exception {
      Form5000Data data = new Form5000Data(
          "Jean Dupont",
          "123 Rue de Genève",
          "1200",
          "Genève",
          "Suisse",
          "CHE-123.456.789",
          2024
      );

      byte[] pdf = form5000Generator.generate(data);

      assertThat(pdf).isNotEmpty();

      // Verify PDF structure
      PDDocument document = PDDocument.load(pdf);
      assertThat(document.getNumberOfPages()).isEqualTo(1);
      document.close();
  }
  ```
  **Estimation**: 1 jour

#### **Epic 2: Storage S3/MinIO** (Story Points: 20)

- [ ] **Story 2.1**: MinIO client configuration (5 SP)
  ```java
  @Configuration
  public class MinIOConfig {
      @Value("${storage.minio.url}")
      private String url;

      @Value("${storage.minio.access-key}")
      private String accessKey;

      @Value("${storage.minio.secret-key}")
      private String secretKey;

      @Value("${storage.minio.bucket}")
      private String bucket;

      @Bean
      public MinioClient minioClient() {
          return MinioClient.builder()
              .endpoint(url)
              .credentials(accessKey, secretKey)
              .build();
      }

      @PostConstruct
      public void initBucket() throws Exception {
          if (!minioClient().bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
              minioClient().makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
          }
      }
  }
  ```
  **Estimation**: 0.5 jour

- [ ] **Story 2.2**: Implémenter StorageService (8 SP)
  ```java
  @Service
  public class StorageService {
      private final MinioClient minioClient;
      private final String bucket;

      public String upload(byte[] data, String userId, String filename) {
          String objectName = String.format(
              "forms/%s/%s/%s",
              userId,
              LocalDate.now().format(DateTimeFormatter.ISO_DATE),
              filename
          );

          try {
              minioClient.putObject(
                  PutObjectArgs.builder()
                      .bucket(bucket)
                      .object(objectName)
                      .stream(new ByteArrayInputStream(data), data.length, -1)
                      .contentType("application/zip")
                      .build()
              );
              return objectName;
          } catch (Exception e) {
              throw new StorageException("Failed to upload file", e);
          }
      }

      public String generatePresignedUrl(String objectName, int expirySeconds) {
          try {
              return minioClient.getPresignedObjectUrl(
                  GetPresignedObjectUrlArgs.builder()
                      .bucket(bucket)
                      .object(objectName)
                      .expiry(expirySeconds)
                      .method(Method.GET)
                      .build()
              );
          } catch (Exception e) {
              throw new StorageException("Failed to generate presigned URL", e);
          }
      }
  }
  ```
  **Estimation**: 1 jour

- [ ] **Story 2.3**: Tests StorageService (5 SP)
  **Estimation**: 0.5 jour

#### **Epic 3: Database & Migrations** (Story Points: 25)

- [ ] **Story 3.1**: Créer migrations Flyway (13 SP)
  ```sql
  -- backend/src/main/resources/db/migration/V1__init_schema.sql
  CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

  CREATE TABLE users (
      id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
      email VARCHAR(255) UNIQUE NOT NULL,
      password_hash VARCHAR(255) NOT NULL,
      full_name VARCHAR(255),
      tax_id VARCHAR(50),
      address TEXT,
      city VARCHAR(100),
      postal_code VARCHAR(20),
      country VARCHAR(100) DEFAULT 'Suisse',
      created_at TIMESTAMP DEFAULT NOW(),
      updated_at TIMESTAMP DEFAULT NOW()
  );

  CREATE TABLE generated_forms (
      id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
      user_id UUID REFERENCES users(id) ON DELETE CASCADE,
      s3_key VARCHAR(500) NOT NULL,
      file_name VARCHAR(255),
      tax_year INTEGER NOT NULL,
      status VARCHAR(50) DEFAULT 'GENERATED',
      created_at TIMESTAMP DEFAULT NOW(),
      downloaded_at TIMESTAMP,
      expires_at TIMESTAMP
  );

  CREATE TABLE dividends (
      id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
      form_id UUID REFERENCES generated_forms(id) ON DELETE CASCADE,
      security_name VARCHAR(255) NOT NULL,
      isin VARCHAR(12) NOT NULL,
      payment_date DATE NOT NULL,
      gross_amount NUMERIC(10,2) NOT NULL,
      currency VARCHAR(3) DEFAULT 'EUR',
      withholding_tax NUMERIC(10,2) NOT NULL,
      treaty_amount NUMERIC(10,2),
      reclaimable_amount NUMERIC(10,2) NOT NULL,
      french_rate NUMERIC(5,2),
      broker_name VARCHAR(255)
  );

  CREATE INDEX idx_users_email ON users(email);
  CREATE INDEX idx_forms_user_id ON generated_forms(user_id);
  CREATE INDEX idx_forms_tax_year ON generated_forms(tax_year);
  CREATE INDEX idx_dividends_form_id ON dividends(form_id);
  CREATE INDEX idx_dividends_isin ON dividends(isin);
  ```
  **Estimation**: 1 jour

- [ ] **Story 3.2**: Créer JPA Entities (8 SP)
  - `GeneratedForm.java`
  - `Dividend.java`
  - Relationships (@OneToMany, @ManyToOne)
  **Estimation**: 1 jour

- [ ] **Story 3.3**: Repositories (3 SP)
  - `GeneratedFormRepository`
  - `DividendRepository`
  **Estimation**: 0.5 jour

#### **Epic 4: Authentication** (Story Points: 30)

- [ ] **Story 4.1**: User registration endpoint (8 SP)
- [ ] **Story 4.2**: Login endpoint + JWT generation (8 SP)
- [ ] **Story 4.3**: Email verification (8 SP)
- [ ] **Story 4.4**: Password reset (optionnel) (5 SP)

---

### 📊 MÉTRIQUES PHASE 1

**Critères de Succès**:
- ✅ Forms 5000 + 5001 générés avec données réelles
- ✅ Upload S3/MinIO fonctionnel
- ✅ Database migrations appliquées
- ✅ Authentication complète
- ✅ Tests backend coverage >70%

---

## 🔄 PHASE 2: INTEGRATION (Semaines 7-9)

**Objectif**: Connecter Frontend ↔ BFF ↔ Backend

### 📝 TASKS

#### **Semaine 7: Frontend API Layer**

- [ ] Configurer Axios client
- [ ] Implémenter authAPI
- [ ] Implémenter dividendsAPI
- [ ] Implémenter formsAPI
- [ ] Remplacer tous les mocks par appels API
- [ ] Error handling UI

#### **Semaine 8: BFF Implementation**

- [ ] Header propagation filter (X-User-Id)
- [ ] AuthController implementation
- [ ] DividendController implementation
- [ ] FormController implementation
- [ ] Global error handler
- [ ] Request/response logging

#### **Semaine 9: E2E Testing**

- [ ] Setup Playwright/Cypress
- [ ] E2E test: User registration
- [ ] E2E test: Login flow
- [ ] E2E test: Upload PDF → Generate forms
- [ ] E2E test: Download forms
- [ ] Performance testing (response times)

---

## 📄 PHASE 3: PDF FEATURES (Semaines 10-13)

**Objectif**: Parser PDF relevés + Génération forms optimisée

### 📝 BACKLOG

#### **Epic 1: PDF Parsing** (40 SP)

- [ ] Recherche & POC libraries (pdfplumber, Tabula, Camelot)
- [ ] Parser Swissquote statements
- [ ] Parser Interactive Brokers statements
- [ ] Extraction ISIN (regex)
- [ ] Extraction dates (regex)
- [ ] Extraction montants (regex)
- [ ] Validation ISIN (API OpenFIGI)
- [ ] Tests parsing (50+ PDF samples)

#### **Epic 2: Form Optimization** (20 SP)

- [ ] Design forms similaires aux officiels
- [ ] Optimisation PDF size
- [ ] Watermark "Généré par Tax Dividend AI"
- [ ] QR code tracking (optionnel)

---

## 🚀 PHASE 4: PRODUCTION READY (Semaines 14-16)

**Objectif**: Déploiement, monitoring, scaling

### 📝 TASKS

#### **Semaine 14: CI/CD Production**

- [ ] Dockerfile optimization
- [ ] Multi-stage builds
- [ ] Docker Compose production
- [ ] GitHub Actions deploy workflow
- [ ] Secrets management (GitHub Secrets)

#### **Semaine 15: Cloud Deployment**

- [ ] Setup Google Cloud Platform project
- [ ] Deploy Backend → Cloud Run
- [ ] Deploy BFF → Cloud Run
- [ ] Deploy Frontend → Firebase Hosting
- [ ] Setup Cloud SQL (PostgreSQL)
- [ ] Setup Cloud Storage (instead MinIO)
- [ ] Configure Cloud Load Balancer

#### **Semaine 16: Monitoring & Optimization**

- [ ] Sentry error tracking
- [ ] Cloud Logging
- [ ] Cloud Monitoring dashboards
- [ ] Uptime checks
- [ ] Backup strategy (DB snapshots)
- [ ] Load testing (Locust, k6)
- [ ] Performance optimization

---

## 🎉 PHASE 5: LAUNCH (Semaine 17)

### 📝 CHECKLIST GO-LIVE

#### **Pré-Launch (Jours 1-3)**

- [ ] **Security Audit**
  - [ ] Penetration testing
  - [ ] OWASP Top 10 check
  - [ ] Secrets rotation
  - [ ] HTTPS everywhere

- [ ] **Legal & Compliance**
  - [ ] Terms of Service published
  - [ ] Privacy Policy published
  - [ ] GDPR compliance verified
  - [ ] Cookie consent banner

- [ ] **Beta Testing**
  - [ ] 10 beta testers invited
  - [ ] Feedback form sent
  - [ ] Bug tracking (Jira/Linear)

#### **Launch Day (Jour 4)**

- [ ] **Morning (9h-12h)**
  - [ ] Final smoke tests production
  - [ ] Backup database
  - [ ] Enable monitoring alerts
  - [ ] Prepare rollback procedure

- [ ] **Launch (12h)**
  - [ ] Switch DNS to production
  - [ ] Announce on social media
  - [ ] Send email to beta users
  - [ ] Monitor errors (Sentry)

- [ ] **Afternoon (14h-18h)**
  - [ ] Monitor metrics (users, errors)
  - [ ] Support chat ready
  - [ ] Fix critical bugs immediately

#### **Post-Launch (Jours 5-7)**

- [ ] **Day +1**
  - [ ] Retrospective team
  - [ ] Bug prioritization
  - [ ] Performance analysis

- [ ] **Week +1**
  - [ ] User feedback synthesis
  - [ ] Roadmap Phase 6 planning
  - [ ] Blog post launch announcement

---

## 📊 DASHBOARDS & TRACKING

### KPIs Techniques

| Métrique | Cible | Alertes |
|----------|-------|---------|
| **Uptime** | >99.5% | <99% |
| **API Latency p95** | <500ms | >1s |
| **Error Rate** | <1% | >5% |
| **Test Coverage** | >70% | <60% |
| **Build Time** | <5min | >10min |

### KPIs Produit

| Métrique | Cible Semaine 1 | Cible Mois 1 |
|----------|-----------------|--------------|
| **Signups** | 50 | 500 |
| **Activation** (1st PDF) | >60% | >70% |
| **Forms Generated** | 30 | 300 |
| **User Retention D7** | >40% | >50% |

---

## 🎯 DÉFINITION OF DONE

### Feature

- [ ] Code écrit et testé
- [ ] Tests unitaires >70% coverage
- [ ] Tests d'intégration passent
- [ ] Pas de régression
- [ ] Code reviewed (PR approved)
- [ ] Documentation mise à jour
- [ ] Merged dans develop

### Sprint

- [ ] Toutes les features complètes
- [ ] CI/CD vert
- [ ] Demo stakeholders
- [ ] Rétrospective faite
- [ ] Backlog raffiné

### Phase

- [ ] Tous les sprints complétés
- [ ] Livrables validés
- [ ] Tests E2E passent
- [ ] Documentation complète
- [ ] Review technique externe (optionnel)

---

## 📞 CONTACTS & RESSOURCES

### Équipe

| Rôle | Contact | Disponibilité |
|------|---------|---------------|
| Tech Lead | TBD | 100% |
| DevOps | TBD | 40% |
| QA | TBD | 60% |

### Outils

- **Project Board**: GitHub Projects
- **CI/CD**: GitHub Actions
- **Monitoring**: Sentry + GCP
- **Communication**: Slack #tax-dividend-ai

### Documentation

- Technical Review: `/docs/TECHNICAL_REVIEW.md`
- Architecture: `/docs/ARCHITECTURE.md`
- API Specs: `/specs/`
- This Roadmap: `/PRODUCTION_ROADMAP.md`

---

**Dernière mise à jour**: 27 Janvier 2026
**Prochaine Review**: Fin Phase 0 (Semaine 2)
