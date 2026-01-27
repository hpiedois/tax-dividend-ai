# Décisions Architecturales

**Date de dernière mise à jour**: 28 Janvier 2026

Ce document acte toutes les décisions architecturales importantes du projet Tax Dividend AI.

---

## ✅ Décisions Actées

### 1. Stack Technique Backend

**DÉCISION**: Spring Boot (Java) pour BFF Gateway + Backend Services séparés

| Composant | Technologie | Version | Justification |
|-----------|-------------|---------|---------------|
| **Backend Framework** | Spring Boot | 3.5+ | Production-ready, écosystème mature, conformité réglementaire |
| **BFF Gateway** | Spring Boot WebFlux | 3.5+ | Réactif (async), orchestration, CORS, rate limiting, cache |
| **Backend Services** | Spring Boot + JPA | 3.5+ | Logique métier, accès DB, services métier |
| **Base de données** | PostgreSQL | 16+ | ACID, relationnel, extensions (JSON, versioning) |
| **Storage** | AWS S3 / MinIO | Latest | Stockage sécurisé des PDFs générés |
| **Génération PDF** | Apache PDFBox | 3.0+ | Bibliothèque Java mature, support Forms complexes |
| **Auth** | Spring Security + JWT | Included | Authentification/autorisation robuste |

**Architecture**:
- **2 projets Spring Boot** : `bff-gateway/` (port 8080) + `backend/` (port 8081)
- **Communication interne** : HTTP/REST (privé, réseau interne uniquement)
- **Exposition publique** : BFF Gateway uniquement (CORS, rate limiting)

**Alternatives écartées**:
- ❌ FastAPI (Python): Moins de garanties pour conformité fiscale
- ❌ Node.js/Express: Typage moins strict, moins adapté pour réglementaire
- ❌ Backend monolithe unique: Moins évolutif, couplage frontend/backend
- ❌ PDF-lib (frontend): Sécurité insuffisante, templates exposés

**Date de décision**: 28 Janvier 2026

---

### 2. Architecture Applicative

**DÉCISION**: Architecture BFF (Backend For Frontend) avec services backend

```
Frontend (React) → BFF Gateway (Spring Boot WebFlux) → Backend Services (Spring Boot)
                                                      → PostgreSQL
                                                      → S3/MinIO
```

**Composants**:

| Composant | Technologie | Port | Responsabilités |
|-----------|-------------|------|-----------------|
| **Frontend** | React 19 + Vite | 5173 | UI/UX uniquement, AUCUNE logique métier |
| **BFF Gateway** | Spring Boot 3.5 WebFlux | 8080 | Point d'entrée unique, orchestration, CORS, cache, rate limiting |
| **Backend Services** | Spring Boot 3.5 + JPA | 8081 | Logique métier, calculs fiscaux, PDF, parsing, base de données |

**Responsabilités BFF Gateway**:
- ✅ Point d'entrée UNIQUE pour le frontend (CORS configuré)
- ✅ Orchestration des appels backend (composition de réponses)
- ✅ Transformation données (Backend → Frontend DTO)
- ✅ Gestion JWT / Session (validation token)
- ✅ Rate Limiting (protection DDoS)
- ✅ Caching (réponses fréquentes)
- ✅ Validation requêtes (schéma, format)

**Responsabilités Backend Services**:
- ✅ Logique métier (Tax Engine, règles fiscales)
- ✅ Parsing PDF (Apache PDFBox)
- ✅ Génération Forms 5000/5001 (Apache PDFBox)
- ✅ Calculs fiscaux (taux, montants reclaimables)
- ✅ Accès base de données (JPA/Hibernate)
- ✅ Storage S3/MinIO (upload/download PDFs)

**Justification BFF malgré complexité**:
- **Évolutivité**: Prêt pour mobile app, API publique B2B
- **Sécurité**: Backend Services non exposés publiquement
- **Performance**: Cache au niveau BFF (réponses fréquentes)
- **Maintenabilité**: Frontend et Backend évoluent indépendamment
- **Monitoring**: Point central pour logs, métriques, rate limiting

**Alternative écartée**: Backend monolithe unique (plus simple mais moins évolutif)

**Date de décision**: 28 Janvier 2026

---

### 3. Séparation Frontend/Backend pour Logique Métier

**DÉCISION**: 100% de la logique métier et des calculs fiscaux côté backend

| Fonctionnalité | Frontend | Backend |
|----------------|----------|---------|
| Calculs fiscaux (taux, montants reclaimables) | ❌ NON | ✅ OUI |
| Parsing PDF relevés bancaires | ❌ NON | ✅ OUI |
| Génération Forms 5000/5001 | ❌ NON | ✅ OUI |
| Validation règles métier | ❌ NON | ✅ OUI |
| Validation UI (champs requis, format) | ✅ OUI | ✅ OUI (double) |
| Affichage données | ✅ OUI | ❌ NON |

**Justification**:
1. **Sécurité**: Règles fiscales non contournables côté client
2. **Conformité**: Audit trail et versioning des calculs
3. **Maintenance**: Changements réglementaires sans recompilation frontend
4. **Performance**: Calculs complexes sur serveur
5. **Testing**: Validation métier testable indépendamment

**Date de décision**: 28 Janvier 2026

---

### 4. Stack Technique Frontend

**DÉCISION**: React 19 + TypeScript + Vite (déjà implémenté)

| Composant | Technologie | Justification |
|-----------|-------------|---------------|
| **Framework** | React 19.2.0 | Standard industrie, écosystème riche |
| **Build Tool** | Vite 7.2.4 | HMR rapide, build optimisé |
| **Language** | TypeScript 5.9.3 (strict) | Type safety, maintenance |
| **Styling** | Tailwind CSS 3.4.17 | Rapid prototyping, design system |
| **Animations** | Framer Motion | Animations fluides, glass-morphism |
| **i18n** | i18next | Support FR/EN/DE/IT |
| **State** | React hooks (local) | Simple pour MVP, migrer vers Zustand si nécessaire |
| **Forms** | À venir: React Hook Form + Zod | Performance, validation |
| **Routing** | À venir: React Router v6 | Standard, mature |
| **API Client** | À venir: React Query | Cache, optimistic updates |

**Date de décision**: 24 Janvier 2026 (initial), confirmé 28 Janvier 2026

---

### 5. Structure Mono-repo vs Multi-repo

**DÉCISION**: Mono-repo pour Phase 0-1, évaluer multi-repo pour Phase 2+

**Structure actuelle**:
```
tax-dividend-ai/
├── frontend/          # React + Vite
├── docs/              # Documentation
└── specs/             # Spécifications (vide)
```

**Structure future (Phase 1)**:
```
tax-dividend-ai/
├── frontend/          # React + Vite (port 5173)
├── bff-gateway/       # Spring Boot WebFlux (port 8080)
├── backend/           # Spring Boot + JPA (port 8081)
├── docs/              # Documentation
└── infrastructure/    # Docker Compose, K8s, CI/CD
```

**Communication**:
- Frontend → BFF Gateway (HTTP/REST public, CORS autorisé)
- BFF Gateway → Backend (HTTP/REST privé, réseau interne)
- Backend → PostgreSQL (JDBC)
- Backend → MinIO/S3 (AWS SDK)

**Date de décision**: 28 Janvier 2026

---

### 6. Stratégie de Déploiement

**DÉCISION**: Firebase Hosting (frontend) + Cloud Run (backend) pour MVP

| Service | Technologie | Justification |
|---------|-------------|---------------|
| **Frontend** | Firebase Hosting | CDN global, gratuit, SSL auto |
| **Backend** | Google Cloud Run | Auto-scale, pay-per-use, containerisé |
| **Database** | Cloud SQL (PostgreSQL) | Managed, backups auto, HA |
| **Storage** | Google Cloud Storage | Compatible S3 API, intégration GCP |
| **CI/CD** | GitHub Actions | Gratuit, intégré GitHub |

**Coût estimé**: ~€40-50/mois pour 100 utilisateurs

**Alternative** (si besoin de scale): Kubernetes (GKE) pour Phase 3+

**Date de décision**: 24 Janvier 2026

---

### 7. Stratégie de Testing

**DÉCISION**: Vitest + React Testing Library (frontend), JUnit + Mockito (backend)

| Type de Test | Outil | Cible |
|--------------|-------|-------|
| **Unit Tests (Frontend)** | Vitest | Composants, hooks, utils |
| **Unit Tests (Backend)** | JUnit 5 + Mockito | Services, repositories |
| **Integration Tests** | TestContainers | API + DB |
| **E2E Tests** | Playwright | User flows critiques |

**Objectif**: >70% code coverage pour Phase 1

**Date de décision**: 28 Janvier 2026

---

## 🔄 Décisions à Prendre (Futur)

### Phase 1 (4-6 semaines)
- [ ] Parsing PDF: Apache PDFBox vs pdfplumber vs OCR (Tesseract)
- [ ] Signature électronique: DocuSign vs Adobe Sign vs solution custom
- [ ] Stockage local dev: MinIO vs LocalStack S3

### Phase 2 (Microservices)
- [ ] Event Bus: RabbitMQ vs Kafka vs Google Pub/Sub
- [ ] API Gateway: Spring Cloud Gateway vs Kong vs Apigee
- [ ] Service Mesh: Istio vs Linkerd

### Phase 3 (B2B)
- [ ] Multi-tenancy: Schema per tenant vs Shared schema + RLS
- [ ] API externe: OpenAPI 3.1 + génération clients (TypeScript, Python)

---

## 📚 Références

### Documentation Principale
- `GETTING_STARTED.md` - **Guide de démarrage rapide (commencer ici !)**
- `DECISIONS.md` - **Ce fichier - Source de vérité pour les décisions**
- `README.md` - Documentation publique du projet
- `CLAUDE.md` - Guide développement pour Claude Code

### Documentation Technique
- `docs/ARCHITECTURE.md` - Architecture technique détaillée
- `docs/ACTIONS_COMPLETED.md` - Historique des actions + roadmap

### Documentation Projets
- `frontend/README.md` - React + Vite frontend
- `bff-gateway/README.md` - BFF Gateway (Spring Boot WebFlux)
- `backend/README.md` - Backend Services (Spring Boot + JPA)
- `infrastructure/README.md` - Docker Compose setup

### Documentation Archivée
- `docs/archive/` - Documents obsolètes/contradictoires (ne pas utiliser)

---

## 📝 Historique des Modifications

| Date | Modification | Auteur |
|------|--------------|--------|
| 2026-01-28 | Création document + décisions Spring Boot, séparation frontend/backend | Claude |
| 2026-01-26 | Architecture BFF Gateway documentée | - |
| 2026-01-24 | Stack frontend initiale implémentée | - |
