# 🏗️ STRATÉGIE MULTI-REPO

**Date**: 27 Janvier 2026
**Objectif**: Séparer le monorepo en repositories indépendants pour équipes multiples

---

## 🎯 POURQUOI MULTI-REPO ?

### Avantages

| Critère | Monorepo | Multi-repo |
|---------|----------|------------|
| **Équipes indépendantes** | ❌ Conflits merge | ✅ Isolation complète |
| **CI/CD** | ❌ Tout rebuild | ✅ Build sélectif |
| **Permissions** | ❌ Tout ou rien | ✅ Granulaire (GitHub teams) |
| **Releases** | ❌ Versions couplées | ✅ Versions indépendantes |
| **Onboarding** | ❌ Clone 500MB | ✅ Clone 50MB (un repo) |
| **Ownership** | ❌ Flou | ✅ Clair (CODEOWNERS) |

### Inconvénients (Mitigés)

| Challenge | Solution |
|-----------|----------|
| **Shared code** | → NPM packages privés ou Git submodules |
| **Versioning** | → API contracts (OpenAPI specs) |
| **Local dev** | → Docker Compose orchestration |
| **Documentation** | → Repo dédié `tax-dividend-docs` |

---

## 🗂️ STRUCTURE MULTI-REPO

### Repositories

```
GitHub Organization: tax-dividend-ai/

├── tax-dividend-ui (Frontend Team)
│   ├── React 19 + TypeScript + Vite
│   ├── Tailwind + Framer Motion
│   ├── i18n (4 langues)
│   ├── Storybook (design system)
│   └── Cypress (E2E tests)
│
├── tax-dividend-bff (Backend-for-Frontend Team)
│   ├── Spring Boot 3.5 WebFlux
│   ├── JWT validation
│   ├── API orchestration
│   └── Rate limiting
│
├── tax-dividend-api (Backend Team)
│   ├── Spring Boot 3.5 + JPA
│   ├── PostgreSQL
│   ├── PDF generation (Apache PDFBox)
│   ├── Storage (S3/MinIO)
│   └── Tax calculations
│
├── tax-dividend-infra (DevOps Team)
│   ├── Docker Compose (local dev)
│   ├── Kubernetes manifests
│   ├── Terraform (GCP/AWS)
│   ├── GitHub Actions workflows
│   └── Monitoring (Prometheus, Grafana)
│
├── tax-dividend-docs (Product/Tech Writers)
│   ├── Architecture diagrams
│   ├── API documentation (OpenAPI)
│   ├── User guides
│   ├── Tax rules documentation
│   └── Onboarding guides
│
└── tax-dividend-contracts (Shared)
    ├── OpenAPI specs (API contracts)
    ├── TypeScript types (shared)
    └── Protobuf definitions (if needed)
```

---

## 📋 DÉTAIL PAR REPO

### 1. `tax-dividend-ui` (Frontend)

**Équipe**: Frontend Developers (React/TypeScript)

**Structure**:
```
tax-dividend-ui/
├── .github/
│   └── workflows/
│       ├── ci.yml
│       ├── deploy-staging.yml
│       └── deploy-prod.yml
├── src/
│   ├── components/
│   ├── pages/
│   ├── hooks/
│   ├── lib/
│   └── locales/
├── public/
├── cypress/          # E2E tests
├── .storybook/       # Component library
├── package.json
├── vite.config.ts
├── tailwind.config.js
├── CHANGELOG.md
└── README.md
```

**CI/CD**:
```yaml
# .github/workflows/ci.yml
on: [push, pull_request]
jobs:
  test:
    - npm ci
    - npm run lint
    - npm run test
    - npm run build
  deploy-staging:
    if: branch == develop
    - npm run build
    - firebase deploy --only hosting:staging
  deploy-prod:
    if: branch == main
    - npm run build
    - firebase deploy --only hosting:prod
```

**Versioning**: Semantic versioning `v1.2.3`
- Major: Breaking UI changes
- Minor: New features
- Patch: Bug fixes

**Dependencies**:
- Appelle BFF via `http://localhost:8080/api` (dev)
- Appelle BFF via `https://api.taxdividend.ai` (prod)

---

### 2. `tax-dividend-bff` (BFF Gateway)

**Équipe**: Backend Developers (Spring Boot)

**Structure**:
```
tax-dividend-bff/
├── .github/workflows/
├── src/main/java/com/taxdividend/bff/
│   ├── controller/
│   ├── security/
│   ├── config/
│   └── client/
├── src/main/resources/
│   ├── application.yml
│   └── application-prod.yml
├── src/test/java/
├── Dockerfile
├── pom.xml
└── README.md
```

**CI/CD**:
```yaml
on: [push, pull_request]
jobs:
  test:
    - ./mvnw clean verify
  build-docker:
    - docker build -t gcr.io/tax-dividend/bff:${{ github.sha }}
    - docker push
  deploy-staging:
    if: branch == develop
    - gcloud run deploy bff-staging --image=...
  deploy-prod:
    if: branch == main
    - gcloud run deploy bff-prod --image=...
```

**API Contract**: Consomme `../tax-dividend-contracts/openapi/backend-api.yaml`

**Versioning**: `v1.0.0-beta.1`

---

### 3. `tax-dividend-api` (Backend)

**Équipe**: Backend Developers (Spring Boot + Database)

**Structure**:
```
tax-dividend-api/
├── .github/workflows/
├── src/main/java/com/taxdividend/api/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── model/
│   └── config/
├── src/main/resources/
│   ├── db/migration/       # Flyway migrations
│   └── templates/          # PDF templates
├── src/test/java/
├── Dockerfile
└── pom.xml
```

**CI/CD**:
```yaml
on: [push, pull_request]
jobs:
  test:
    services:
      postgres: ...
    - ./mvnw clean verify
  migration-check:
    - flyway validate
  deploy-staging:
    - gcloud run deploy api-staging
    - Run migrations (flyway migrate)
```

**Database Migrations**:
- Flyway pour PostgreSQL
- Chaque migration = PR séparée
- Rollback scripts obligatoires

**Versioning**: `v2.1.0`

---

### 4. `tax-dividend-infra` (Infrastructure)

**Équipe**: DevOps / SRE

**Structure**:
```
tax-dividend-infra/
├── docker-compose/
│   ├── docker-compose.dev.yml     # Local dev
│   ├── docker-compose.staging.yml
│   └── docker-compose.prod.yml
├── kubernetes/
│   ├── base/
│   │   ├── deployment.yaml
│   │   ├── service.yaml
│   │   └── ingress.yaml
│   ├── overlays/
│   │   ├── staging/
│   │   └── production/
├── terraform/
│   ├── gcp/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   └── modules/
│       ├── cloud-run/
│       ├── cloud-sql/
│       └── gcs/
├── monitoring/
│   ├── prometheus/
│   ├── grafana/
│   └── alertmanager/
├── scripts/
│   ├── setup-local.sh
│   ├── deploy-staging.sh
│   └── deploy-prod.sh
└── README.md
```

**Docker Compose (Dev Local)**:
```yaml
# docker-compose/docker-compose.dev.yml
version: '3.8'

services:
  ui:
    build: ../../tax-dividend-ui
    ports: ["5173:5173"]
    environment:
      - VITE_API_URL=http://localhost:8080/api

  bff:
    build: ../../tax-dividend-bff
    ports: ["8080:8080"]
    environment:
      - BACKEND_URL=http://api:8081

  api:
    build: ../../tax-dividend-api
    ports: ["8081:8081"]
    depends_on: [postgres, minio]

  postgres:
    image: postgres:16
    ports: ["5432:5432"]

  minio:
    image: minio/minio
    ports: ["9000:9000", "9001:9001"]
```

**Usage**:
```bash
# Clone tous les repos
./scripts/setup-local.sh

# Lance tous les services
cd docker-compose
docker-compose -f docker-compose.dev.yml up
```

---

### 5. `tax-dividend-docs` (Documentation)

**Équipe**: Product Owners, Tech Writers, Architects

**Structure**:
```
tax-dividend-docs/
├── architecture/
│   ├── system-overview.md
│   ├── data-flow.md
│   └── diagrams/
│       ├── architecture.drawio
│       └── sequence-diagrams/
├── api/
│   ├── openapi-specs/      # Copied from contracts
│   └── postman-collections/
├── user-guides/
│   ├── fr/
│   ├── en/
│   ├── de/
│   └── it/
├── developer/
│   ├── getting-started.md
│   ├── local-setup.md
│   ├── testing-guide.md
│   └── deployment.md
├── tax-rules/
│   ├── france-switzerland.md
│   ├── germany-switzerland.md
│   └── changelog.md
└── README.md
```

**Hébergement**: GitHub Pages ou Docusaurus

---

### 6. `tax-dividend-contracts` (Shared)

**Équipe**: Backend + Frontend (collaboration)

**Structure**:
```
tax-dividend-contracts/
├── openapi/
│   ├── bff-api.yaml              # BFF public API
│   ├── backend-api.yaml          # Backend internal API
│   └── schemas/
│       ├── Dividend.yaml
│       ├── TaxRule.yaml
│       └── User.yaml
├── typescript/
│   ├── generated/                # Auto-generated from OpenAPI
│   │   └── api-types.ts
│   └── package.json
├── scripts/
│   └── generate-types.sh
└── README.md
```

**Workflow**:
1. Backend team modifie `backend-api.yaml`
2. PR + review frontend team
3. Merge → Trigger generation types TypeScript
4. Publish `@tax-dividend/contracts@1.2.3` sur npm privé
5. Frontend `npm install @tax-dividend/contracts@1.2.3`

---

## 🔄 MIGRATION DU MONOREPO → MULTI-REPO

### Phase 1: Extraction (Semaine 1)

#### Jour 1: Créer les repos GitHub

```bash
# GitHub CLI
gh repo create tax-dividend-ai/tax-dividend-ui --public
gh repo create tax-dividend-ai/tax-dividend-bff --private
gh repo create tax-dividend-ai/tax-dividend-api --private
gh repo create tax-dividend-ai/tax-dividend-infra --private
gh repo create tax-dividend-ai/tax-dividend-docs --public
gh repo create tax-dividend-ai/tax-dividend-contracts --private
```

#### Jour 2: Extraire Frontend

```bash
# Dans monorepo actuel
cd /Users/hpiedois/perso/workspaces/tax-dividend-ai

# Créer nouveau repo UI avec historique
git subtree split -P frontend -b frontend-only
cd ../
mkdir tax-dividend-ui
cd tax-dividend-ui
git init
git pull ../tax-dividend-ai frontend-only

# Push vers GitHub
git remote add origin git@github.com:tax-dividend-ai/tax-dividend-ui.git
git push -u origin main

# Cleanup
git branch -D frontend-only
```

#### Jour 3: Extraire BFF

```bash
cd /Users/hpiedois/perso/workspaces/tax-dividend-ai
git subtree split -P bff-gateway -b bff-only
cd ../
mkdir tax-dividend-bff
cd tax-dividend-bff
git init
git pull ../tax-dividend-ai bff-only
git remote add origin git@github.com:tax-dividend-ai/tax-dividend-bff.git
git push -u origin main
```

#### Jour 4: Extraire Backend

```bash
cd /Users/hpiedois/perso/workspaces/tax-dividend-ai
git subtree split -P backend -b backend-only
cd ../
mkdir tax-dividend-api
cd tax-dividend-api
git init
git pull ../tax-dividend-ai backend-only
git remote add origin git@github.com:tax-dividend-ai/tax-dividend-api.git
git push -u origin main
```

#### Jour 5: Créer Infra & Docs

```bash
# Infra repo (nouveau)
mkdir tax-dividend-infra
cd tax-dividend-infra
git init

# Copier docker-compose.yml depuis monorepo
cp ../tax-dividend-ai/docker-compose.yml ./docker-compose/docker-compose.dev.yml

# Créer structure
mkdir -p kubernetes/{base,overlays/{staging,production}}
mkdir -p terraform/gcp
mkdir -p monitoring/{prometheus,grafana}
mkdir -p scripts

git add -A
git commit -m "Initial infrastructure setup"
git remote add origin git@github.com:tax-dividend-ai/tax-dividend-infra.git
git push -u origin main

# Docs repo
mkdir tax-dividend-docs
cd tax-dividend-docs
git init

# Copier docs depuis monorepo
cp -r ../tax-dividend-ai/docs/* ./

# Organiser
mkdir -p architecture api user-guides developer tax-rules
mv ARCHITECTURE.md architecture/
mv TECHNICAL_REVIEW.md developer/

git add -A
git commit -m "Initial documentation"
git remote add origin git@github.com:tax-dividend-ai/tax-dividend-docs.git
git push -u origin main
```

---

### Phase 2: Configuration (Semaine 2)

#### GitHub Teams & Permissions

```bash
# Créer teams
gh api -X POST /orgs/tax-dividend-ai/teams -f name="frontend-team"
gh api -X POST /orgs/tax-dividend-ai/teams -f name="backend-team"
gh api -X POST /orgs/tax-dividend-ai/teams -f name="devops-team"

# Permissions
# Frontend team
gh api -X PUT /orgs/tax-dividend-ai/teams/frontend-team/repos/tax-dividend-ai/tax-dividend-ui \
  -f permission=push

# Backend team
gh api -X PUT /orgs/tax-dividend-ai/teams/backend-team/repos/tax-dividend-ai/tax-dividend-bff \
  -f permission=push
gh api -X PUT /orgs/tax-dividend-ai/teams/backend-team/repos/tax-dividend-ai/tax-dividend-api \
  -f permission=admin

# DevOps team (admin sur tout)
gh api -X PUT /orgs/tax-dividend-ai/teams/devops-team/repos/tax-dividend-ai/tax-dividend-infra \
  -f permission=admin
```

#### CODEOWNERS

**tax-dividend-ui/.github/CODEOWNERS**:
```
* @tax-dividend-ai/frontend-team
/cypress/ @tax-dividend-ai/qa-team
```

**tax-dividend-api/.github/CODEOWNERS**:
```
* @tax-dividend-ai/backend-team
/src/main/resources/db/migration/ @tax-dividend-ai/dba-team @tax-dividend-ai/backend-team
```

#### Branch Protection

```bash
# Require PR reviews
gh api -X PUT /repos/tax-dividend-ai/tax-dividend-ui/branches/main/protection \
  --input - <<EOF
{
  "required_status_checks": {
    "strict": true,
    "contexts": ["ci", "build"]
  },
  "enforce_admins": false,
  "required_pull_request_reviews": {
    "required_approving_review_count": 1,
    "dismiss_stale_reviews": true
  },
  "restrictions": null
}
EOF
```

---

### Phase 3: Local Dev Setup (Semaine 3)

#### Script Setup Automatique

**tax-dividend-infra/scripts/setup-local.sh**:
```bash
#!/bin/bash

set -e

echo "🚀 Tax Dividend AI - Local Setup"

# 1. Clone tous les repos
WORKSPACE_DIR="${HOME}/workspaces/tax-dividend-ai"
mkdir -p "$WORKSPACE_DIR"
cd "$WORKSPACE_DIR"

repos=(
  "tax-dividend-ui"
  "tax-dividend-bff"
  "tax-dividend-api"
  "tax-dividend-infra"
  "tax-dividend-docs"
  "tax-dividend-contracts"
)

for repo in "${repos[@]}"; do
  if [ ! -d "$repo" ]; then
    echo "📦 Cloning $repo..."
    git clone "git@github.com:tax-dividend-ai/$repo.git"
  else
    echo "✓ $repo already exists"
  fi
done

# 2. Install dependencies
echo "📦 Installing frontend dependencies..."
cd tax-dividend-ui && npm install && cd ..

echo "📦 Building backend..."
cd tax-dividend-api && ./mvnw clean install -DskipTests && cd ..
cd tax-dividend-bff && ./mvnw clean install -DskipTests && cd ..

# 3. Setup Docker
echo "🐳 Starting Docker services..."
cd tax-dividend-infra/docker-compose
docker-compose -f docker-compose.dev.yml up -d postgres minio mailhog

echo "✅ Setup complete!"
echo ""
echo "Start services:"
echo "  Terminal 1: cd tax-dividend-api && ./mvnw spring-boot:run"
echo "  Terminal 2: cd tax-dividend-bff && ./mvnw spring-boot:run"
echo "  Terminal 3: cd tax-dividend-ui && npm run dev"
echo ""
echo "URLs:"
echo "  Frontend: http://localhost:5173"
echo "  BFF:      http://localhost:8080"
echo "  API:      http://localhost:8081"
```

**Usage**:
```bash
curl -fsSL https://raw.githubusercontent.com/tax-dividend-ai/tax-dividend-infra/main/scripts/setup-local.sh | bash
```

---

## 📊 WORKFLOW QUOTIDIEN

### Développement Frontend

```bash
# Développeur frontend
cd ~/workspaces/tax-dividend-ai/tax-dividend-ui

# Créer feature branch
git checkout -b feature/new-dashboard

# Développer
npm run dev

# Tests
npm run test
npm run lint

# Commit
git add -A
git commit -m "feat: Add new dashboard view"
git push origin feature/new-dashboard

# Créer PR sur GitHub
gh pr create --title "New dashboard view" --body "..."

# Après review & merge
git checkout main
git pull
```

### Développement Backend (Avec changement DB)

```bash
cd ~/workspaces/tax-dividend-ai/tax-dividend-api

# Feature branch
git checkout -b feature/add-tax-rules-table

# Créer migration Flyway
cat > src/main/resources/db/migration/V3__add_tax_rules.sql <<EOF
CREATE TABLE tax_rules (
  id UUID PRIMARY KEY,
  country_code VARCHAR(2),
  rate DECIMAL(5,4)
);
EOF

# Développer service
# ...

# Tests
./mvnw test

# Commit
git add -A
git commit -m "feat: Add tax rules table and service"
git push origin feature/add-tax-rules-table

# PR
gh pr create --title "Add tax rules management"
```

---

## 🔐 SECRETS & CONFIG

### GitHub Secrets (par repo)

**tax-dividend-ui**:
```
FIREBASE_TOKEN
SENTRY_DSN
```

**tax-dividend-bff**:
```
GCP_PROJECT_ID
GCP_SA_KEY
BACKEND_API_URL
JWT_SECRET
```

**tax-dividend-api**:
```
DATABASE_URL
DATABASE_PASSWORD
S3_ACCESS_KEY
S3_SECRET_KEY
SENDGRID_API_KEY
```

### Centralisé via `.env` (local)

**tax-dividend-infra/.env.example**:
```bash
# Frontend
VITE_API_URL=http://localhost:8080/api

# BFF
BACKEND_URL=http://localhost:8081
JWT_SECRET=dev-secret-change-in-prod

# Backend
DATABASE_URL=postgresql://localhost:5432/taxdividend
DATABASE_USER=taxdividend
DATABASE_PASSWORD=secret
S3_ENDPOINT=http://localhost:9000
S3_ACCESS_KEY=minioadmin
S3_SECRET_KEY=minioadmin
```

---

## 📈 MONITORING MULTI-REPO

### Unified Dashboard

**Grafana Dashboard**: Tous les repos en un seul écran

```
┌─────────────────────────────────────────────────┐
│ Tax Dividend AI - System Health                 │
├─────────────────────────────────────────────────┤
│ Frontend (tax-dividend-ui)                      │
│ ├─ Build Status: ✅ Passing                     │
│ ├─ Deploy: v1.2.3 (5 min ago)                   │
│ └─ Errors: 0.01% (last 1h)                      │
│                                                  │
│ BFF (tax-dividend-bff)                          │
│ ├─ Build Status: ✅ Passing                     │
│ ├─ Deploy: v1.0.5 (10 min ago)                  │
│ ├─ Latency p95: 245ms                           │
│ └─ Errors: 0.1% (last 1h)                       │
│                                                  │
│ API (tax-dividend-api)                          │
│ ├─ Build Status: ⚠️ Testing                     │
│ ├─ Deploy: v2.1.0 (2h ago)                      │
│ ├─ DB Connections: 15/100                       │
│ └─ Errors: 0.05% (last 1h)                      │
└─────────────────────────────────────────────────┘
```

### GitHub Organization Insights

Tous les repos visibles en un coup d'œil :
- Commits par équipe
- PR ouvertes
- Issues par repo
- Code coverage

---

## ✅ CHECKLIST MIGRATION

### Pre-Migration
- [ ] Backup monorepo complet
- [ ] Créer GitHub organization `tax-dividend-ai`
- [ ] Définir teams & permissions
- [ ] Prévenir toutes les équipes

### Extraction (Jour 1-5)
- [ ] Créer 6 repos GitHub
- [ ] Extraire `tax-dividend-ui` avec historique
- [ ] Extraire `tax-dividend-bff` avec historique
- [ ] Extraire `tax-dividend-api` avec historique
- [ ] Créer `tax-dividend-infra` (nouveau)
- [ ] Créer `tax-dividend-docs` (réorganiser)
- [ ] Créer `tax-dividend-contracts` (nouveau)

### Configuration (Semaine 2)
- [ ] Setup branch protection (main)
- [ ] Setup CODEOWNERS
- [ ] Setup GitHub Actions (CI/CD)
- [ ] Migrer secrets
- [ ] Créer npm scope `@tax-dividend`

### Testing (Semaine 3)
- [ ] Tester setup local (script)
- [ ] Tester CI/CD chaque repo
- [ ] Tester docker-compose dev
- [ ] Tester déploiement staging

### Go-Live (Jour 1)
- [ ] Freeze monorepo (read-only)
- [ ] Announcement équipes
- [ ] Documentation onboarding
- [ ] Support channel Slack

---

## 🎯 PROCHAINES ÉTAPES

**Aujourd'hui (27 Jan)**:
1. ✅ Commit monorepo actuel
2. ✅ Créer ce document
3. ⏳ Décision: Migrer maintenant ou Phase 1 ?

**Option A: Migrer Maintenant** (2 semaines)
- Semaine 1: Extraction repos
- Semaine 2: Configuration & tests
- Avantage: Équipes autonomes immédiatement
- Risque: Pause développement 2 semaines

**Option B: Migrer Phase 1** (après Backend Core)
- Continuer Phase 0-1 en monorepo
- Migration pendant Phase 2
- Avantage: Pas de pause, stabilité
- Risque: Conflits merge si >3 devs

**Recommandation**: **Option B** si <5 devs, **Option A** si équipes déjà constituées

---

**Décision requise**: Migrer maintenant ou après Phase 1 ? 🤔
