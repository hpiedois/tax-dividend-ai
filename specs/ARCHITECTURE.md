# Architecture Tax Dividend AI

## Vue d'ensemble

Tax Dividend AI est une plateforme SaaS qui automatise les processus de réclamation fiscale pour les investisseurs transfrontaliers (cas d'usage : résidents suisses détenant des titres français).

### Stack technique

- **Frontend** : React 19 + TypeScript + Vite
- **BFF** : Spring Boot 4.0.2 + Java 25 (orchestrateur)
- **Backend** : Spring Boot 4.0.2 + Java 25 (storage/CRUD)
- **Agent** : Service de parsing AI/LLM (microservice indépendant)
- **Infrastructure** : PostgreSQL, MinIO, Docker Compose

---

## Architecture des services

```
┌─────────────┐
│  Frontend   │
│ React + TS  │
└──────┬──────┘
       │ HTTP/REST
       ▼
┌─────────────────────────────────────┐
│         BFF (Orchestrator)          │
│    Spring Boot 4 + Java 25          │
│  • Authentification JWT              │
│  • Orchestration Backend + Agent     │
│  • Mapping entre les APIs            │
└──────┬─────────────────┬────────────┘
       │                 │
       │ Internal API    │ Parsing API
       ▼                 ▼
┌─────────────┐   ┌──────────────┐
│   Backend   │   │    Agent     │
│ Storage/CRUD│   │  AI Parsing  │
│ PostgreSQL  │   │  LLM Service │
│   MinIO     │   │              │
└─────────────┘   └──────────────┘
```

---

## Responsabilités des services

### 1. Backend (Port 8081)

**Rôle** : Couche de stockage et CRUD pur

**Responsabilités** :
- Persistance des données (PostgreSQL)
- Gestion du stockage de fichiers (MinIO/S3)
- CRUD sur les entités :
  - `User` (utilisateurs)
  - `DividendStatement` (relevés de dividendes uploadés)
  - `Dividend` (dividendes individuels)
  - `GeneratedForm` (formulaires fiscaux générés)
  - `TaxRule` (règles de conventions fiscales)
  - `AuditLog` (audit trail)
- Calcul fiscal (application des TaxRules)
- Génération de formulaires PDF (Forms 5000, 5001)
- Envoi d'emails (vérification, notifications)

**Ne fait PAS** :
- ❌ Parsing de documents
- ❌ Appels à l'Agent
- ❌ Logique d'orchestration
- ❌ Authentification JWT (délégué au BFF)

**API** : `/internal/*` - API interne (header `X-User-Id` requis)

**Contrat** : `specs/backend/openapi.yaml`

---

### 2. BFF - Backend for Frontend (Port 8080)

**Rôle** : Orchestrateur intelligent et API gateway

**Responsabilités** :
- Authentification JWT (login, register, verify email)
- Orchestration des appels Backend + Agent
- Mapping entre les modèles :
  - Agent → Backend (parsing → bulk import)
  - Backend → Frontend (DTOs publiques)
- Gestion de session utilisateur (cookies HTTP-only)
- Agrégation de données multi-sources
- Logique métier spécifique au frontend

**Flow de parsing** (orchestration) :
1. Recevoir upload PDF du Frontend
2. Appeler Backend POST `/dividend-statements` (créer entity, status: UPLOADED)
3. Appeler Agent POST `/parse` (avec le fichier PDF)
4. Recevoir `ParsedDividendStatement` de l'Agent
5. Mapper `DocumentDividendData[]` → `BulkImportDividendItem[]`
6. Appeler Backend POST `/dividends/bulk` (créer les dividendes)
7. Appeler Backend PATCH `/dividend-statements/{id}` (status: PARSED)
8. Retourner résultat au Frontend

**Clients générés** :
```java
// Client Agent (depuis specs/agent/openapi.yaml)
com.taxdividend.bff.client.agent.ParsedDividendStatement
com.taxdividend.bff.client.agent.DocumentDividendData

// Client Backend (depuis specs/backend/openapi.yaml)
com.taxdividend.bff.client.backend.DividendStatement
com.taxdividend.bff.client.backend.Dividend
com.taxdividend.bff.client.backend.BulkImportDividendsRequest

// API publique BFF (exposée au Frontend)
com.taxdividend.bff.model.DividendStatement
com.taxdividend.bff.model.Dividend
```

**API** : `/api/*` - API publique (JWT Bearer token)

**Contrat** : `specs/bff/openapi.yaml`

---

### 3. Agent - AI Parsing Service (Port 8083)

**Rôle** : Service de parsing de documents avec LLM

**Responsabilités** :
- Parsing de PDF de relevés de dividendes
- Support des PDFs chiffrés (avec mot de passe)
- Analyse d'images (JPEG, PNG)
- Extraction de données structurées via LLM
- Support multi-broker :
  - Interactive Brokers
  - Swissquote
  - Degiro
  - etc.
- Détection automatique du broker
- Extraction des périodes (periodStart, periodEnd)

**Indépendant** :
- ❌ Ne connaît pas le Backend
- ❌ Ne stocke rien
- ❌ Stateless (pas de session, pas de DB)

**API** : `/api/*` - API de parsing

**Contrat** : `specs/agent/openapi.yaml`

**Modèles** :
- `ParsedDividendStatement` : Résultat complet du parsing
- `DocumentDividendData` : Données d'un dividende individuel

---

## Flow de données : Parsing d'un relevé

### Séquence complète

```
┌─────────┐    ┌─────┐    ┌─────────┐    ┌───────┐
│Frontend │    │ BFF │    │ Backend │    │ Agent │
└────┬────┘    └──┬──┘    └────┬────┘    └───┬───┘
     │            │             │             │
     │ 1. Upload PDF            │             │
     ├───────────>│             │             │
     │            │             │             │
     │            │ 2. Create DividendStatement (UPLOADED)
     │            ├────────────>│             │
     │            │<────────────┤             │
     │            │ {id, status: UPLOADED}    │
     │            │             │             │
     │            │ 3. Parse PDF              │
     │            ├─────────────────────────>│
     │            │             │             │
     │            │ 4. ParsedDividendStatement│
     │            │<─────────────────────────┤
     │            │ {data: DocumentDividendData[]}
     │            │             │             │
     │            │ 5. Bulk import dividends  │
     │            ├────────────>│             │
     │            │<────────────┤             │
     │            │ {importedCount, dividendIds}
     │            │             │             │
     │            │ 6. Update statement (PARSED)
     │            ├────────────>│             │
     │            │<────────────┤             │
     │            │             │             │
     │ 7. Response              │             │
     │<───────────┤             │             │
     │ {statement, dividends}   │             │
     │            │             │             │
```

### Détails des étapes

#### 1. Frontend → BFF : Upload PDF

```http
POST /api/dividends/parse-statement
Content-Type: multipart/form-data

file: [binary PDF data]
```

#### 2. BFF → Backend : Create DividendStatement

```http
POST /internal/dividend-statements
X-User-Id: {userId}
Content-Type: multipart/form-data

file: [binary PDF data]
broker: "Interactive Brokers" (optionnel)
periodStart: "2024-01-01" (optionnel)
periodEnd: "2024-12-31" (optionnel)
```

**Response** :
```json
{
  "id": "uuid",
  "userId": "uuid",
  "sourceFileName": "statement.pdf",
  "status": "UPLOADED",
  "createdAt": "2024-01-15T10:00:00Z"
}
```

#### 3. BFF → Agent : Parse PDF

```http
POST /api/parse
Content-Type: multipart/form-data

file: [binary PDF data]
password: "secret" (optionnel)
```

#### 4. Agent → BFF : ParsedDividendStatement

```json
{
  "data": [
    {
      "securityName": "LVMH",
      "isin": "FR0000121014",
      "grossAmount": 150.00,
      "currency": "EUR",
      "paymentDate": "2024-03-15",
      "country": "FR",
      "withholdingTax": 45.00,
      "reclaimableAmount": 22.50
    }
  ],
  "broker": "Interactive Brokers",
  "periodStart": "2024-01-01",
  "periodEnd": "2024-12-31"
}
```

#### 5. BFF → Backend : Bulk import dividends

**Mapping BFF** :
```java
// Mapper DocumentDividendData → BulkImportDividendItem
BulkImportDividendsRequest request = new BulkImportDividendsRequest();
request.setStatementId(statementId);
request.setBroker(parsedStatement.getBroker());

List<BulkImportDividendItem> items = parsedStatement.getData().stream()
    .map(data -> {
        BulkImportDividendItem item = new BulkImportDividendItem();
        item.setSecurityName(data.getSecurityName());
        item.setIsin(data.getIsin());
        item.setGrossAmount(data.getGrossAmount());
        item.setCurrency(data.getCurrency());
        item.setPaymentDate(data.getPaymentDate());
        item.setSourceCountry(data.getCountry());
        item.setWithholdingTax(data.getWithholdingTax());
        // Calculer le taux de retenue
        item.setWithholdingRate(
            data.getGrossAmount().compareTo(BigDecimal.ZERO) != 0
                ? data.getWithholdingTax()
                    .divide(data.getGrossAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                : BigDecimal.ZERO
        );
        return item;
    })
    .collect(Collectors.toList());

request.setDividends(items);
```

**HTTP Request** :
```http
POST /internal/dividends/bulk
X-User-Id: {userId}
Content-Type: application/json

{
  "statementId": "uuid",
  "broker": "Interactive Brokers",
  "dividends": [
    {
      "securityName": "LVMH",
      "isin": "FR0000121014",
      "paymentDate": "2024-03-15",
      "grossAmount": 150.00,
      "currency": "EUR",
      "withholdingTax": 45.00,
      "withholdingRate": 30.00,
      "sourceCountry": "FR"
    }
  ]
}
```

**Response** :
```json
{
  "importedCount": 5,
  "failedCount": 0,
  "totalGrossAmount": 1250.00,
  "totalReclaimable": 187.50,
  "dividendIds": ["uuid1", "uuid2", "uuid3", "uuid4", "uuid5"],
  "errors": []
}
```

#### 6. BFF → Backend : Update statement status

```http
PATCH /internal/dividend-statements/{id}
X-User-Id: {userId}
Content-Type: application/json

{
  "status": "PARSED",
  "parsedBy": "AI_AGENT"
}
```

#### 7. BFF → Frontend : Response

```json
{
  "statement": {
    "id": "uuid",
    "status": "PARSED",
    "broker": "Interactive Brokers",
    "dividendCount": 5,
    "totalGrossAmount": 1250.00,
    "totalReclaimable": 187.50
  },
  "dividends": [
    {
      "id": "uuid",
      "securityName": "LVMH",
      "isin": "FR0000121014",
      "grossAmount": 150.00,
      "reclaimableAmount": 22.50,
      "status": "OPEN"
    }
  ]
}
```

---

## Nommage des modèles par service

### Agent (specs/agent/openapi.yaml)

| Modèle | Description |
|--------|-------------|
| `ParsedDividendStatement` | Résultat complet du parsing d'un relevé |
| `DocumentDividendData` | Données d'un dividende individuel extrait |

**Préfixe "Document"** : Indique que ce sont des données extraites d'un document, pas encore des entités persistées.

### Backend (specs/backend/openapi.yaml)

| Modèle | Description |
|--------|-------------|
| `DividendStatement` | Entité relevé de dividendes (persisted) |
| `Dividend` | Entité dividende individuel (persisted) |
| `BulkImportDividendsRequest` | Request pour import en masse |
| `BulkImportDividendItem` | Item d'import (mapping de `DocumentDividendData`) |
| `DividendStatementUpdate` | DTO de mise à jour de statement |
| `TaxRule` | Règle de convention fiscale |
| `GeneratedForm` | Formulaire fiscal généré |

### BFF (specs/bff/openapi.yaml)

**API publique exposée au Frontend** :

| Modèle | Description |
|--------|-------------|
| `DividendStatement` | DTO statement (API publique) |
| `Dividend` | DTO dividende (API publique) |
| `ParsePDFResponse` | Response du parsing (mappé depuis Agent + Backend) |

**Clients internes BFF** (générés) :

```
com.taxdividend.bff.client.agent.*      // Client Agent
com.taxdividend.bff.client.backend.*    // Client Backend
```

---

## Contrats OpenAPI

### Structure des specs

```
specs/
├── ARCHITECTURE.md              # Ce fichier
├── agent/
│   ├── openapi.yaml            # Contrat API Agent
│   ├── paths/
│   │   └── parse.yaml
│   └── schemas/
│       ├── ParsedDividendStatement.yaml
│       └── DocumentDividendData.yaml
├── backend/
│   ├── openapi.yaml            # Contrat API Backend
│   ├── paths/
│   │   └── pdf-generate.yaml
│   └── schemas/
│       ├── DividendStatsDTO.yaml
│       ├── FormGenerationRequest.yaml
│       ├── GenerateFormResultDTO.yaml
│       ├── RegisterUserRequest.yaml
│       ├── TaxCalculationResult.yaml
│       └── VerifyEmailResponse.yaml
└── bff/
    ├── openapi.yaml            # Contrat API BFF (publique)
    ├── paths/
    │   ├── dividends-history.yaml
    │   ├── dividends-stats.yaml
    │   ├── forms-generate.yaml
    │   └── statements-parse.yaml
    └── schemas/
        ├── DividendCase.yaml
        ├── .yaml
        ├── DividendHistoryResponse.yaml
        ├── DividendStats.yaml
        ├── GenerateTaxFormsRequest.yaml
        ├── GenerateTaxFormsResponse.yaml
        ├── ParsePDFResponse.yaml
        └── RegisterRequest.yaml
```

### Génération de code

**Backend** :
```bash
cd backend
mvn clean compile  # Génère les DTOs depuis specs/backend/openapi.yaml
```

**BFF** :
```bash
cd bff-gateway
mvn clean compile  # Génère :
                   # - API BFF (specs/bff/openapi.yaml)
                   # - Client Agent (specs/agent/openapi.yaml)
                   # - Client Backend (specs/backend/openapi.yaml)
```

---

## Principes architecturaux

### 1. Separation of Concerns

- **Backend** : Stockage pur (no business logic lourde)
- **BFF** : Orchestration et adaptation pour le frontend
- **Agent** : Service métier spécialisé (parsing)

### 2. Contract-First Development

- Tous les services sont définis par des contrats OpenAPI
- Les DTOs sont générés automatiquement (OpenAPI Generator)
- Les contrats servent de documentation et de source de vérité

### 3. Internal vs Public APIs

- **Backend** : API interne (`/internal/*`, header `X-User-Id`)
- **BFF** : API publique (`/api/*`, JWT Bearer)
- **Agent** : API de service (`/api/*`, stateless)

### 4. Stateless Services

- **Agent** : Complètement stateless (pas de DB, pas de session)
- **Backend** : Stateful (PostgreSQL)
- **BFF** : Session utilisateur (JWT, cookies)

### 5. Single Responsibility

Chaque service a une responsabilité claire et ne dépend pas des autres pour ses fonctions core.

---

## Évolution future

### Agent 2 : Tax Rules Updater (à implémenter)

**Purpose** : Maintenir les règles fiscales à jour automatiquement

**Design** :
- Microservice indépendant
- Monitoring des sources officielles (traités fiscaux)
- LLM-assisted analysis des changements
- Propose updates to TaxRule entities
- Requires admin approval
- Audit trail complet

**Database impact** : Write access à `tax_rules` table

---

## Résumé

| Service | Port | Rôle | Base de données | Dépendances |
|---------|------|------|-----------------|-------------|
| Frontend | 5173 | UI React | - | BFF |
| BFF | 8080 | Orchestrator | - | Backend + Agent |
| Backend | 8081 | Storage/CRUD | PostgreSQL + MinIO | - |
| Agent | 8083 | AI Parsing | - | - |

**Flow principal** : Frontend → BFF → (Backend + Agent) → Storage

**Responsabilités claires** :
- BFF orchestrate
- Backend stocke
- Agent parse
