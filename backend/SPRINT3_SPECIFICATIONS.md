# Sprint 3 - Features Critiques - Spécifications

**Date**: 2026-01-31
**Durée**: 10 jours (Semaines 3-4)
**Objectif**: Implémenter les fonctionnalités business essentielles

---

## ⚠️ Clarifications importantes

### 1. Parsing de dividend statements (CSV/PDF) - AGENT IA EXTERNE

**❌ PAS DANS LE BACKEND**

Le backend **NE FAIT PAS** le parsing des fichiers PDF/CSV des brokers.

**Architecture correcte**:
```
Frontend
  ↓ Upload PDF/CSV
Agent IA Parser (microservice externe)
  ├─ LLM-assisted parsing
  ├─ Supporte: Interactive Brokers, Swissquote, PostFinance, etc.
  ├─ Extraction: ISIN, montants, dates, currency, withholding tax
  └─ Retourne: JSON structuré
      ↓ POST /internal/dividends (bulk import)
Backend
  ├─ Reçoit données parsées (JSON)
  ├─ Validation et enrichissement
  ├─ Calculs fiscaux (via TaxRuleService)
  └─ Stockage en DB
```

**Ce que le backend doit faire**:
- ✅ Endpoint `POST /internal/dividends/bulk` - Import dividendes parsés par l'agent
- ✅ Validation des données reçues
- ✅ Enrichissement avec tax rules
- ✅ Calcul automatique des montants réclamables

**Ce que le backend NE FAIT PAS**:
- ❌ Parser des PDF (c'est l'Agent IA)
- ❌ Parser des CSV (c'est l'Agent IA)
- ❌ OCR ou extraction de texte (c'est l'Agent IA)

**Agent IA Parser - Status**:
- 📂 Location: `agents/dividend-parser/` (à confirmer)
- 📊 Status: Mock créé, non finalisé
- 🔧 Technologies: LLM (Claude/GPT), LangChain, PDF extraction libs
- 🚀 Déploiement: Service indépendant (Docker container)

---

### 2. Tracking des soumissions - DUAL TRACKING

**Il y a 2 types de tracking à implémenter**:

#### A. Tracking des Forms (Formulaires fiscaux 5000/5001)

**Table**: `generated_forms` (sans `form_submissions` pour l'instant)

**Workflow simplifié**:
```
GENERATED → DOWNLOADED → EXPIRED
```

**Statuts `generated_forms.status`**:
- `GENERATED` - Formulaire créé, pas encore téléchargé
- `DOWNLOADED` - Téléchargé par l'utilisateur
- `EXPIRED` - Expiré (30 jours), doit être régénéré

**Note importante**: Le système NE SOUMET PAS les formulaires. L'utilisateur télécharge les PDFs et les soumet manuellement (par email/courrier) aux autorités fiscales. Le tracking de la soumission se fait au niveau `dividend_statements` (voir section B ci-dessous).

**Endpoints existants** (déjà implémentés dans Sprint 2):
```yaml
/forms/{id}/download:
  get:
    summary: Download form PDF

/forms/{id}/download-url:
  get:
    summary: Get pre-signed download URL

/forms/{id}/regenerate:
  post:
    summary: Regenerate expired form
```

#### B. Tracking des Dividend Statements (Relevés courtier)

**Nouveau besoin identifié**: Tracker l'état des relevés de dividendes reçus du courtier.

**Nouvelle table à créer**: `dividend_statements`

```sql
-- Status enum for dividend statements workflow
CREATE TYPE dividend_statement_status AS ENUM (
    'UPLOADED',    -- Fichier uploadé, en attente parsing
    'PARSING',     -- En cours de parsing par Agent IA
    'PARSED',      -- Parsing terminé, dividendes extraits
    'VALIDATED',   -- Dividendes validés, formulaires générés et téléchargés
    'SENT',        -- User a soumis les formulaires au fisc (marquage manuel)
    'PAID'         -- Remboursement reçu (marquage manuel)
);

CREATE TABLE dividend_statements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    -- Fichier source
    source_file_name VARCHAR(255) NOT NULL,
    source_file_s3_key VARCHAR(500) NOT NULL,
    broker VARCHAR(100) NOT NULL,  -- InteractiveBrokers, Swissquote, etc.

    -- Période couverte
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,

    -- Status workflow (replaces boolean flags)
    status dividend_statement_status DEFAULT 'UPLOADED',

    -- Tracking dates
    parsed_at TIMESTAMP,
    validated_at TIMESTAMP,
    sent_at TIMESTAMP,
    sent_method VARCHAR(50),     -- 'EMAIL', 'POSTAL', 'ONLINE'
    sent_notes TEXT,
    paid_at TIMESTAMP,
    paid_amount DECIMAL(12,2),

    -- Métadonnées
    parsed_by VARCHAR(50),       -- 'AI_AGENT'
    dividend_count INTEGER DEFAULT 0,
    total_gross_amount DECIMAL(12,2),
    total_reclaimable DECIMAL(12,2),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Workflow**:
```
UPLOADED → PARSING → PARSED → VALIDATED → SENT → PAID
```

**Statuts `dividend_statements.status`** (ENUM):
- `UPLOADED` - Fichier uploadé, en attente parsing
- `PARSING` - En cours de parsing par Agent IA
- `PARSED` - Parsing terminé, dividendes extraits
- `VALIDATED` - Dividendes validés, formulaires générés et téléchargés
- `SENT` - User a soumis les formulaires au fisc (marquage manuel)
- `PAID` - Remboursement reçu (marquage manuel)

**Transitions autorisées**:
```
UPLOADED → PARSING ✅
PARSING → PARSED ✅
PARSED → VALIDATED ✅
VALIDATED → SENT ✅
SENT → PAID ✅

PAID → SENT ❌ (exception)
SENT → VALIDATED ❌ (exception)
```

**Relation avec dividendes**:
- Un `dividend_statement` contient N `dividends`
- Ajouter colonne `statement_id` dans table `dividends`
- Le statut PAID du statement indique que TOUS les dividendes du statement ont été remboursés
- Note: Si besoin de tracking individuel par dividend, utiliser `dividend.form_id` pour savoir si soumis

**Endpoints nécessaires**:
```yaml
/dividend-statements:
  post:
    summary: Upload dividend statement (returns statement ID)
    requestBody:
      content:
        multipart/form-data:
          schema:
            type: object
            properties:
              file:
                type: string
                format: binary
              broker:
                type: string
              periodStart:
                type: string
                format: date
              periodEnd:
                type: string
                format: date
    responses:
      201:
        description: Statement uploaded, parsing will be triggered
        content:
          application/json:
            schema:
              type: object
              properties:
                statementId:
                  type: string
                  format: uuid
                status:
                  type: string
                parsingJobId:
                  type: string

  get:
    summary: List user's dividend statements
    parameters:
      - name: userId
        in: query
        required: true
      - name: status
        in: query
        schema:
          type: string
    responses:
      200:
        description: List of statements

/dividend-statements/{id}:
  get:
    summary: Get statement details with dividends
    responses:
      200:
        description: Statement with associated dividends

  patch:
    summary: Update statement status (called by AI Agent during parsing, or by user for SENT/PAID)
    requestBody:
      content:
        application/json:
          schema:
            oneOf:
              # AI Agent updates during parsing workflow
              - type: object
                properties:
                  status:
                    type: string
                    enum: [PARSING, PARSED, VALIDATED]
                    description: Updated by AI Agent

              # User marks as SENT after offline submission
              - type: object
                properties:
                  status:
                    type: string
                    enum: [SENT]
                  sentMethod:
                    type: string
                    enum: [EMAIL, POSTAL, ONLINE]
                  sentNotes:
                    type: string

              # User marks as PAID after receiving reimbursement
              - type: object
                properties:
                  status:
                    type: string
                    enum: [PAID]
                  paidAmount:
                    type: number
                  paidAt:
                    type: string
                    format: date-time
    responses:
      200:
        description: Statement status updated
      400:
        description: Invalid status transition (e.g., PAID → SENT)
```

---

## 🎯 Features à implémenter (Sprint 3)

### Feature 1: PdfGenerationService (5 jours) - CRITIQUE

**Objectif**: Générer les formulaires officiels français Forms 5000 et 5001

**Sous-tâches**:

#### 1.1 Form 5000 - Attestation de résidence fiscale (2j)
- Template PDF officiel français (dans `docs/officials/fr/`)
- Remplissage champs:
  - Identité utilisateur (nom, adresse)
  - Canton suisse
  - NIF suisse
  - Année fiscale
  - Signature électronique (optionnel)
- Validation par canton (formulaire peut varier)
- Stockage MinIO avec metadata

#### 1.2 Form 5001 - Liquidation des dividendes (2j)
- Template PDF officiel français
- Remplissage tableau dividendes:
  - Colonne: Nom société
  - Colonne: ISIN
  - Colonne: Date paiement
  - Colonne: Montant brut
  - Colonne: Impôt retenu
  - Colonne: Montant réclamable
- Calcul totaux
- Multi-page si > 10 dividendes
- Stockage MinIO

#### 1.3 Bundle Generation (1j)
- Génération ZIP contenant:
  - `form-5000.pdf`
  - `form-5001.pdf`
  - `summary.json` (métadonnées)
- Stockage MinIO
- Download URL pré-signée

**Technologies**:
- Apache PDFBox 3.0.3 (déjà dans pom.xml)
- iText (si PDFBox insuffisant pour formulaires)
- Template engine (Thymeleaf ou FreeMarker)

**Tests**:
- Unit tests: génération avec données mock
- Integration tests: vérification PDF valide
- Visual tests: vérifier rendu PDF (manuel)

---

### Feature 2: DividendService (3 jours)

**Objectif**: Service complet pour gestion des dividendes

**Sous-tâches**:

#### 2.1 Bulk Import depuis Agent IA (1j)
```java
POST /internal/dividends/bulk
Body: {
  "statementId": "uuid",
  "dividends": [
    {
      "isin": "FR0000120271",
      "securityName": "TotalEnergies",
      "paymentDate": "2024-06-15",
      "grossAmount": 1000.00,
      "currency": "EUR",
      "withholdingTax": 300.00,
      "withholdingRate": 30.00,
      "sourceCountry": "FR"
    }
  ]
}
```

**Logique**:
- Validation des données
- Enrichissement avec tax rules (calculer treaty rate)
- Calcul automatique reclaimable amount
- Stockage avec `statement_id`

#### 2.2 Calculs fiscaux (1j)
- Service `TaxCalculationService` (déjà existant, à compléter)
- Logique:
  - Lookup applicable tax rule (source country, residence country, date)
  - Apply treaty rate
  - Calculate reclaimable: `withholdingTax - (grossAmount * treatyRate)`
  - Handle multiple currencies (conversion?)

#### 2.3 Agrégations et stats (1j)
- Total par année fiscale
- Total par ISIN
- Total réclamable
- Grouping par soumission status

---

### Feature 3: FormService (2 jours)

**Objectif**: Service complet pour cycle de vie des formulaires

**Sous-tâches**:

#### 3.1 Génération workflow (1j)
- Trigger génération form 5000 + 5001 pour une liste de dividendes
- Validation: dividendes du même user, même pays source
- Validation: dividendes pas déjà dans un form
- Création entry `generated_forms`
- Appel PdfGenerationService
- Stockage MinIO

#### 3.2 Expiration et régénération (0.5j)
- Cron job: marquer forms expirés (> 30 jours)
- Endpoint `/forms/{id}/regenerate`
- Régénération avec données à jour (recalcul tax)

#### 3.3 Download URLs (0.5j)
- Génération pre-signed URLs MinIO (expire 1h)
- Endpoint `/forms/{id}/download-url`

---

## 📊 Estimation détaillée

| Feature | Tâches | Temps | Priorité |
|---------|--------|-------|----------|
| **PdfGenerationService** | Form 5000, 5001, Bundle | 5j | 🔴 CRITIQUE |
| **DividendService** | Bulk import, calculs, stats | 3j | 🟡 HAUTE |
| **FormService** | Workflow, expiration, download | 2j | 🟡 HAUTE |
| **TOTAL** | | **10j** | |

---

## ✅ Prérequis

Avant de démarrer Sprint 3:
- ✅ Sprint 2 terminé (OpenAPI spec à jour)
- ✅ Migration Flyway pour `dividend_statements` table
- ✅ Templates PDF Forms 5000/5001 disponibles dans `docs/officials/fr/`
- ✅ Agent IA Parser en cours de développement (peut travailler en parallèle)

---

## 🔄 Workflow complet end-to-end (Simplifié)

```
1. USER uploads PDF statement
   ↓
2. Backend creates DividendStatement (status: UPLOADED)
   ↓
3. Backend triggers Agent IA Parser (async)
   ↓
4. Agent IA parses PDF → JSON dividends
   ↓ PATCH /dividend-statements/{id} → status: PARSING
5. Agent IA calls POST /internal/dividends/bulk
   ↓
6. Backend validates + enriches + calculates tax
   ↓
7. Backend stores dividends (linked to statement)
   ↓ PATCH /dividend-statements/{id} → status: PARSED
8. USER generates forms (selects dividends)
   ↓
9. Backend generates Form 5000 + 5001 (PdfGenerationService)
   ↓
10. Backend stores in MinIO + creates GeneratedForm (status: GENERATED)
   ↓
11. Backend updates DividendStatement (status: VALIDATED)
   ↓
12. USER downloads forms (via /forms/{id}/download or /forms/{id}/download-url)
   ↓
13. Backend updates GeneratedForm (status: DOWNLOADED)
   ↓
14. USER submits forms to tax authority (OFFLINE - email/postal/online portal)
   ↓
15. USER marks statement as SENT in app
   ↓ PATCH /dividend-statements/{id} → status: SENT, sentMethod: EMAIL, sentNotes: "..."
16. Backend updates DividendStatement (status: SENT, sent_at: now)
   ↓
17. USER receives reimbursement from tax authority (OFFLINE - bank transfer)
   ↓
18. USER marks statement as PAID in app
   ↓ PATCH /dividend-statements/{id} → status: PAID, paidAmount: 1500.00, paidAt: "2024-06-15"
19. Backend updates DividendStatement (status: PAID, paid_at: now, paid_amount: 1500.00)
```

**Note importante**: Les étapes 14, 17 sont faites OFFLINE par l'utilisateur. Le système ne soumet PAS automatiquement aux autorités fiscales.

**Validation des transitions**:
- Le backend DOIT valider que les transitions de status sont valides
- Exception levée si transition invalide (ex: PAID → SENT)
- Seul l'AI Agent peut mettre PARSING/PARSED
- Seul le user peut mettre SENT/PAID

---

## ❓ Questions à clarifier

1. **Forms 5000/5001 templates**: Sont-ils déjà dans `docs/officials/fr/`?
2. **Agent IA Parser**: Repository séparé ou dans `/agents/`?
3. **Currencies**: Faut-il gérer conversion EUR/CHF/USD?
4. **Signature électronique**: Form 5000 nécessite signature canton - comment gérer?
5. **Multi-year**: Un statement peut-il couvrir plusieurs années fiscales?

---

**Prêt à démarrer Sprint 3 après Sprint 2 ✅**
