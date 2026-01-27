# 🚀 PHASE 0 - KICKOFF

**Démarrage**: 27 Janvier 2026
**Durée**: 2 semaines (jusqu'au 10 Février 2026)
**Objectif**: Stabiliser la base de code et poser les fondations qualité

---

## ✅ DÉCISIONS VALIDÉES

### Architecture
- ✅ **Frontend** (React 19 + TypeScript)
- ✅ **BFF Gateway** (Spring Boot WebFlux)
- ✅ **Backend** (Spring Boot + JPA)
- ✅ **Règles fiscales** → Base de données (DB + Admin Panel)
- ✅ **Agent IA** → Phase 2 (Mois 6)

### Stack Confirmé
- ✅ **Conserver Spring Boot** (Backend + BFF)
- ✅ **PostgreSQL** pour rules + data
- ✅ **MinIO/S3** pour PDFs
- ✅ **Calculs fiscaux** → Backend uniquement

---

## 📋 CHECKLIST SEMAINE 1 (27 Jan - 2 Fév)

### 🔴 LUNDI 27/01 (4h)

#### ✅ Matin : Setup Environnement
- [ ] **[30min]** Cloner/Pull dernier code
  ```bash
  cd /Users/hpiedois/perso/workspaces/tax-dividend-ai
  git pull origin main
  ```

- [ ] **[30min]** Vérifier dépendances
  ```bash
  # Frontend
  cd frontend && npm install

  # Backend
  cd ../backend && ./mvnw clean install

  # BFF
  cd ../bff-gateway && ./mvnw clean install
  ```

- [ ] **[1h]** Lancer services en local
  ```bash
  # Terminal 1: PostgreSQL
  docker run -d --name tax-dividend-db \
    -e POSTGRES_DB=taxdividend \
    -e POSTGRES_USER=taxdividend \
    -e POSTGRES_PASSWORD=secret \
    -p 5432:5432 \
    postgres:16

  # Terminal 2: Backend
  cd backend && ./mvnw spring-boot:run

  # Terminal 3: BFF
  cd bff-gateway && ./mvnw spring-boot:run

  # Terminal 4: Frontend
  cd frontend && npm run dev
  ```

- [ ] **[30min]** Vérifier accès
  - Frontend: http://localhost:5173
  - BFF: http://localhost:8080/actuator/health
  - Backend: http://localhost:8081/actuator/health

#### ✅ Après-midi : Corrections ESLint

- [ ] **[1h]** Corriger erreurs ESLint

  **Erreur 1: App.tsx case blocks**
  ```typescript
  // frontend/src/App.tsx
  // AVANT (ligne 63-64)
  case 'scan':
    const scanContent = scanStep === 'upload' ? ...

  // APRÈS
  case 'scan': {
    const scanContent = scanStep === 'upload' ? ...
    return scanContent;
  }
  ```

  **Erreur 2: useTheme export**
  ```typescript
  // 1. Créer frontend/src/hooks/useTheme.ts
  import { useContext } from 'react';
  import { ThemeProviderContext } from '@/components/theme-provider';

  export function useTheme() {
    const context = useContext(ThemeProviderContext);
    if (!context) {
      throw new Error('useTheme must be used within ThemeProvider');
    }
    return context;
  }

  // 2. Mettre à jour imports dans tous les fichiers
  // Remplacer:
  import { useTheme } from '@/components/theme-provider';
  // Par:
  import { useTheme } from '@/hooks/useTheme';
  ```

- [ ] **[30min]** Vérifier 0 erreurs
  ```bash
  cd frontend
  npm run lint
  # Expected: ✓ 0 errors, 0 warnings
  ```

---

### 🔴 MARDI 28/01 (6h)

#### ✅ Matin : Setup Testing Infrastructure

- [ ] **[1h]** Installer Vitest (Frontend)
  ```bash
  cd frontend
  npm install -D vitest @testing-library/react @testing-library/jest-dom \
    @testing-library/user-event jsdom @vitest/ui
  ```

- [ ] **[1h]** Configurer Vitest
  ```typescript
  // frontend/vitest.config.ts
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
        exclude: [
          'node_modules/',
          'src/test/',
          '**/*.config.ts',
          '**/main.tsx'
        ],
      },
    },
    resolve: {
      alias: {
        '@': path.resolve(__dirname, './src'),
      },
    },
  });
  ```

  ```typescript
  // frontend/src/test/setup.ts
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
      i18n: {
        changeLanguage: vi.fn(),
        language: 'fr',
      },
    }),
    Trans: ({ children }: any) => children,
  }));
  ```

  ```json
  // frontend/package.json - Ajouter scripts
  {
    "scripts": {
      "test": "vitest",
      "test:ui": "vitest --ui",
      "test:ci": "vitest run --coverage"
    }
  }
  ```

- [ ] **[1h]** Écrire 3 premiers tests
  ```typescript
  // frontend/src/components/ui/Button.test.tsx
  import { describe, it, expect, vi } from 'vitest';
  import { render, screen } from '@testing-library/react';
  import userEvent from '@testing-library/user-event';
  import { Button } from './Button';

  describe('Button', () => {
    it('renders children correctly', () => {
      render(<Button>Click me</Button>);
      expect(screen.getByText('Click me')).toBeInTheDocument();
    });

    it('calls onClick when clicked', async () => {
      const handleClick = vi.fn();
      const user = userEvent.setup();

      render(<Button onClick={handleClick}>Click</Button>);
      await user.click(screen.getByText('Click'));

      expect(handleClick).toHaveBeenCalledOnce();
    });

    it('is disabled when disabled prop is true', () => {
      render(<Button disabled>Click</Button>);
      expect(screen.getByRole('button')).toBeDisabled();
    });
  });
  ```

#### ✅ Après-midi : Premiers Tests Backend

- [ ] **[1h]** Setup TestContainers (Backend)
  ```xml
  <!-- backend/pom.xml - Ajouter dans dependencies -->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
  </dependency>
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

- [ ] **[2h]** Créer base test class + 1er test
  ```java
  // backend/src/test/java/com/taxdividend/backend/BaseIntegrationTest.java
  package com.taxdividend.backend;

  import org.junit.jupiter.api.extension.ExtendWith;
  import org.springframework.boot.test.context.SpringBootTest;
  import org.springframework.test.context.ActiveProfiles;
  import org.springframework.test.context.DynamicPropertyRegistry;
  import org.springframework.test.context.DynamicPropertySource;
  import org.springframework.test.context.junit.jupiter.SpringExtension;
  import org.testcontainers.containers.PostgreSQLContainer;
  import org.testcontainers.junit.jupiter.Container;
  import org.testcontainers.junit.jupiter.Testcontainers;

  @SpringBootTest
  @ActiveProfiles("test")
  @ExtendWith(SpringExtension.class)
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

  ```java
  // backend/src/test/java/com/taxdividend/backend/repository/UserRepositoryTest.java
  package com.taxdividend.backend.repository;

  import com.taxdividend.backend.BaseIntegrationTest;
  import com.taxdividend.backend.model.User;
  import org.junit.jupiter.api.Test;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
  import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

  import java.util.Optional;

  import static org.assertj.core.api.Assertions.assertThat;

  @DataJpaTest
  @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
  class UserRepositoryTest extends BaseIntegrationTest {

      @Autowired
      private UserRepository userRepository;

      @Test
      void shouldSaveUser() {
          // Given
          User user = new User();
          user.setEmail("test@example.com");
          user.setPasswordHash("hashed");
          user.setFullName("Test User");

          // When
          User saved = userRepository.save(user);

          // Then
          assertThat(saved.getId()).isNotNull();
          assertThat(saved.getEmail()).isEqualTo("test@example.com");
      }
  }
  ```

- [ ] **[30min]** Vérifier tests passent
  ```bash
  cd backend
  ./mvnw test
  # Expected: Tests run: 1, Failures: 0
  ```

---

### 🟡 MERCREDI 29/01 (4h)

#### ✅ Documentation Règles Fiscales

- [ ] **[3h]** Créer `docs/TAX_RULES.md`
  ```markdown
  # Règles Fiscales France-Suisse

  ## Taux Applicables

  ### France (Pays Source)

  #### 1. PFU (Prélèvement Forfaitaire Unique)
  - **Taux**: 12.8% (IR) + 17.2% (PS) = 30% total
  - **Base légale**: Article 200 A du CGI
  - **Date entrée en vigueur**: 01/01/2018
  - **Applicabilité**: CTO (Compte-Titres Ordinaire)
  - **Notes**:
    - Seuls 12.8% sont réclamables via convention
    - Option pour barème progressif possible

  #### 2. Barème Progressif (TMI)
  - **Taux**: Variable selon tranche (0% à 45%)
  - **Taux standard retenue**: 25%
  - **Date**: Historique (pré-2018) et actuel (option)
  - **Applicabilité**: CTO avec option fiscale

  #### 3. PEA (Plan d'Épargne en Actions)
  - **Taux**: 0% (pas de retenue à la source)
  - **Seules prélèvements sociaux**: 17.2%
  - **Condition**: Après 5 ans

  ### Suisse (Pays Résidence)

  #### Convention France-Suisse
  - **Taux maximum**: 15%
  - **Base légale**: Convention du 9 septembre 1966
  - **Article**: Article 10 (Dividendes)
  - **Lien**: https://www.admin.ch/opc/fr/classified-compilation/19660192/index.html

  ## Formules de Calcul

  ### Cas 1: CTO avec PFU
  ```
  Montant brut: 1000 €
  Retenue France: 1000 × 12.8% = 128 €
  Retenue convention: 1000 × 15% = 150 €
  Montant réclamable: 128 - 150 = -22 € → 0 € (aucune réclamation)
  ```

  ### Cas 2: CTO avec Barème Progressif
  ```
  Montant brut: 1000 €
  Retenue France: 1000 × 25% = 250 €
  Retenue convention: 1000 × 15% = 150 €
  Montant réclamable: 250 - 150 = 100 €
  ```

  ### Cas 3: PEA
  ```
  Pas de retenue à la source → Pas de réclamation possible
  ```

  ## Sources Officielles

  - BOFIP: https://bofip.impots.gouv.fr/
  - CGI Article 200 A: https://www.legifrance.gouv.fr/
  - Convention FR-CH: https://www.impots.gouv.fr/conventions-internationales

  ## Historique des Changements

  | Date | Changement | Référence |
  |------|-----------|-----------|
  | 01/01/2018 | Introduction PFU (12.8%) | Loi de Finances 2018 |
  | 01/01/2013 | Hausse taux progressif (21% → 25%) | Loi de Finances 2013 |
  | 09/09/1966 | Convention FR-CH (15%) | Convention bilatérale |
  ```

- [ ] **[1h]** Consultation expert-comptable (si possible)
  - Valider taux et formules
  - Identifier cas limites
  - Sources officielles

---

### 🟡 JEUDI 30/01 (6h)

#### ✅ CI/CD Setup

- [ ] **[3h]** Créer GitHub Actions workflow
  ```yaml
  # .github/workflows/ci.yml
  name: CI Pipeline

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
          working-directory: frontend
          run: npm ci

        - name: ESLint
          working-directory: frontend
          run: npm run lint

        - name: TypeScript check
          working-directory: frontend
          run: npx tsc --noEmit

        - name: Run tests
          working-directory: frontend
          run: npm run test:ci

        - name: Upload coverage
          uses: codecov/codecov-action@v3
          with:
            files: ./frontend/coverage/coverage-final.json

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
          working-directory: backend
          run: ./mvnw clean verify
          env:
            SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/taxdividend_test
            SPRING_DATASOURCE_USERNAME: test
            SPRING_DATASOURCE_PASSWORD: test

        - name: Test BFF
          working-directory: bff-gateway
          run: ./mvnw clean verify

    build:
      needs: [test-frontend, test-backend]
      runs-on: ubuntu-latest
      if: github.ref == 'refs/heads/main'
      steps:
        - uses: actions/checkout@v4

        - name: Build Frontend
          working-directory: frontend
          run: |
            npm ci
            npm run build

        - name: Build Backend Docker
          run: |
            docker build -t tax-dividend-backend:${{ github.sha }} ./backend

        - name: Build BFF Docker
          run: |
            docker build -t tax-dividend-bff:${{ github.sha }} ./bff-gateway
  ```

#### ✅ Validation Fichiers

- [ ] **[2h]** Implémenter validation stricte
  ```typescript
  // frontend/src/lib/validation.ts
  const ALLOWED_MIME_TYPES = ['application/pdf'] as const;
  const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

  export interface FileValidationError {
    code: 'INVALID_TYPE' | 'FILE_TOO_LARGE' | 'FILE_EMPTY';
    message: string;
  }

  export function validatePDFFile(file: File): FileValidationError | null {
    // 1. Check MIME type
    if (!ALLOWED_MIME_TYPES.includes(file.type as any)) {
      return {
        code: 'INVALID_TYPE',
        message: 'Le fichier doit être au format PDF',
      };
    }

    // 2. Check not empty
    if (file.size === 0) {
      return {
        code: 'FILE_EMPTY',
        message: 'Le fichier est vide',
      };
    }

    // 3. Check size
    if (file.size > MAX_FILE_SIZE) {
      return {
        code: 'FILE_TOO_LARGE',
        message: `Le fichier est trop volumineux (maximum ${MAX_FILE_SIZE / 1024 / 1024}MB)`,
      };
    }

    return null;
  }

  // ISIN validation avec checksum Luhn
  export function validateISIN(isin: string): boolean {
    if (!/^[A-Z]{2}[A-Z0-9]{9}[0-9]$/.test(isin)) {
      return false;
    }

    // Convert to digits
    const digits: number[] = [];
    for (const char of isin) {
      const code = char.charCodeAt(0);
      if (code >= 65 && code <= 90) {
        // A=10, B=11, ..., Z=35
        const value = code - 55;
        digits.push(Math.floor(value / 10), value % 10);
      } else {
        digits.push(parseInt(char));
      }
    }

    // Luhn algorithm
    let sum = 0;
    let shouldDouble = false;

    for (let i = digits.length - 1; i >= 0; i--) {
      let digit = digits[i];

      if (shouldDouble) {
        digit *= 2;
        if (digit > 9) digit -= 9;
      }

      sum += digit;
      shouldDouble = !shouldDouble;
    }

    return sum % 10 === 0;
  }
  ```

- [ ] **[1h]** Appliquer dans DropZone
  ```typescript
  // frontend/src/components/upload/DropZone.tsx
  import { validatePDFFile } from '@/lib/validation';

  const handleDrop = useCallback((acceptedFiles: File[]) => {
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
      setValidationErrors(errors);
    }

    if (validFiles.length > 0) {
      onFilesSelect(validFiles);
    }
  }, [onFilesSelect]);
  ```

---

### 🟢 VENDREDI 31/01 (4h)

#### ✅ Documentation & Cleanup

- [ ] **[2h]** Mettre à jour README principal
  ```markdown
  # Tax Dividend AI

  Automated tax reclaim for cross-border investors.

  ## Quick Start

  ### Prerequisites
  - Node.js 20+
  - Java 21+
  - Docker

  ### Development

  1. Start infrastructure:
  ```bash
  docker-compose up -d postgres minio
  ```

  2. Start backend:
  ```bash
  cd backend && ./mvnw spring-boot:run
  ```

  3. Start BFF:
  ```bash
  cd bff-gateway && ./mvnw spring-boot:run
  ```

  4. Start frontend:
  ```bash
  cd frontend && npm install && npm run dev
  ```

  5. Open http://localhost:5173

  ## Testing

  ```bash
  # Frontend
  cd frontend && npm test

  # Backend
  cd backend && ./mvnw test
  ```

  ## Documentation

  - [Architecture](docs/ARCHITECTURE.md)
  - [Tax Rules](docs/TAX_RULES.md)
  - [Production Roadmap](PRODUCTION_ROADMAP.md)
  ```

- [ ] **[1h]** Créer CONTRIBUTING.md
- [ ] **[1h]** Git commit & push
  ```bash
  git add .
  git commit -m "Phase 0: Stabilisation - ESLint fixes, tests setup, validation

  - Fixed ESLint errors (App.tsx, theme-provider)
  - Added Vitest configuration + first tests
  - Added backend TestContainers setup
  - Created TAX_RULES.md documentation
  - Implemented file validation (MIME, size, ISIN checksum)
  - Setup GitHub Actions CI pipeline

  Co-Authored-By: Claude Sonnet 4.5 <noreply@anthropic.com>"
  ```

---

## 📋 CHECKLIST SEMAINE 2 (3-7 Fév)

### Objectifs
- [ ] Écrire 20-30 tests frontend
- [ ] Écrire 10-15 tests backend
- [ ] Coverage >50% code critique
- [ ] Documentation complète
- [ ] Review & retrospective

### Tâches Détaillées

**Lundi 3/02**:
- [ ] Tests UI components (Button, Card, Input)
- [ ] Tests validation.ts

**Mardi 4/02**:
- [ ] Tests LoginScreen
- [ ] Tests DropZone

**Mercredi 5/02**:
- [ ] Tests backend: UserRepository
- [ ] Tests backend: PdfService

**Jeudi 6/02**:
- [ ] Tests backend: StorageService
- [ ] Vérifier coverage >50%

**Vendredi 7/02**:
- [ ] Documentation cleanup
- [ ] Retrospective Phase 0
- [ ] Planning Phase 1

---

## 📊 MÉTRIQUES DE SUCCÈS

### Phase 0 Terminée Si:

- ✅ **ESLint**: 0 erreurs
- ✅ **Tests Frontend**: 20-30 tests, coverage >50%
- ✅ **Tests Backend**: 10-15 tests, coverage >40%
- ✅ **CI/CD**: Pipeline vert (all green)
- ✅ **Documentation**: TAX_RULES.md + README complets
- ✅ **Validation**: Fichiers + ISIN implémentés

---

## 🚨 BLOCKERS POTENTIELS

| Blocker | Solution |
|---------|----------|
| **Docker issues** | Utiliser PostgreSQL local + skip MinIO pour l'instant |
| **Tests ne passent pas** | Commencer avec tests simples, ajouter complexité progressivement |
| **Expert fiscal indisponible** | Utiliser documentation BOFIP online, valider en Phase 1 |
| **CI/CD échoue** | Tests localement d'abord, debug GitHub Actions après |

---

## 📞 SUPPORT

### Questions ?

- **Architecture**: Voir `/docs/ARCHITECTURE.md`
- **Roadmap**: Voir `/PRODUCTION_ROADMAP.md`
- **Tax Rules**: Voir `/docs/TAX_RULES.md`

### Stuck ?

1. Check documentation
2. Run tests locally
3. Review error logs
4. Git commit early, commit often

---

## 🎯 APRÈS PHASE 0

**Phase 1 démarre le 10 Février** avec :
- Backend services implementation
- PDF generation (Apache PDFBox)
- Storage S3/MinIO
- Database migrations
- Authentication complète

---

**Let's go! 🚀**

**Première action**: Corriger ESLint (30 min) → Quick win!
