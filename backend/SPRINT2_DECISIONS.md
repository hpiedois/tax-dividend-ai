# Sprint 2 - Contract-First Alignment - Décisions

**Date**: 2026-01-31
**Objectif**: Décider du sort des 16 endpoints non définis dans OpenAPI spec

## Méthodologie de décision

Pour chaque endpoint, répondre à:
1. ✅ **Est-il nécessaire pour les features business?**
2. ✅ **Est-il utilisé par le BFF/Frontend?**
3. ✅ **Peut-il être remplacé par un endpoint existant?**

**Décision finale**:
- 🟢 **GARDER** → Ajouter à OpenAPI spec + implémenter
- 🔴 **SUPPRIMER** → Retirer du controller + supprimer tests

---

## 1. HealthController (7 endpoints)

### `/health/database` - Database health check
**Usage**: Monitoring granulaire de la DB (temps de requête, connexions pool)
**Nécessaire?**: ⚠️ Partiel - Actuator `/actuator/health` fait déjà ça
**Décision**: 🔴 **SUPPRIMER** - Redondant avec Actuator standard

### `/health/storage` - Storage (MinIO) health check
**Usage**: Vérifier connectivité MinIO
**Nécessaire?**: ⚠️ Partiel - Actuator `/actuator/health` peut l'inclure
**Décision**: 🔴 **SUPPRIMER** - Ajouter MinIO health indicator à Actuator

### `/health/tax-rules` - Tax rules availability check
**Usage**: Vérifier que des règles fiscales existent
**Nécessaire?**: ❌ Non - Check applicatif, pas infrastructure
**Décision**: 🔴 **SUPPRIMER** - Pas un health check standard

### `/health/services` - All services health aggregation
**Usage**: Agrégation de tous les checks
**Nécessaire?**: ❌ Non - `/actuator/health/live` et `/ready` suffisent
**Décision**: 🔴 **SUPPRIMER** - Redondant avec Actuator

### `/health/info` - Application info (version, build, etc.)
**Usage**: Info sur la version déployée
**Nécessaire?**: ⚠️ Partiel - `/actuator/info` fait déjà ça
**Décision**: 🔴 **SUPPRIMER** - Redondant avec Actuator standard

### `/health/database` - Measure query time
**Usage**: Métriques de performance DB
**Nécessaire?**: ⚠️ Partiel - Prometheus metrics font mieux
**Décision**: 🔴 **SUPPRIMER** - Utiliser Micrometer/Prometheus

### `/health/database` - Handle metrics failure gracefully
**Usage**: Test de robustesse
**Nécessaire?**: ❌ Non - Test unitaire, pas endpoint
**Décision**: 🔴 **SUPPRIMER** - Pas un endpoint business

**Résumé HealthController**: 🔴 **SUPPRIMER les 7 endpoints** - Actuator standard suffit

---

## 2. DividendController (3 endpoints)

### `POST /internal/dividends/calculate-all` - Calculate tax for all user dividends
**Usage**: Batch tax calculation pour tous les dividendes d'un user
**Nécessaire?**: ✅ Oui - Feature business (recalcul après mise à jour tax rules)
**Utilisé par?**: BFF (trigger manuel ou automatique)
**Décision**: 🟢 **GARDER** - Ajouter à OpenAPI spec

**Spec OpenAPI à ajouter**:
```yaml
/dividends/{userId}/calculate-all:
  post:
    summary: Recalculate tax for all user dividends
    operationId: calculateAllUserDividends
    parameters:
      - name: userId
        in: path
        required: true
        schema:
          type: string
          format: uuid
    responses:
      200:
        description: Tax recalculated for all dividends
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/TaxCalculationBatchResultDTO'
```

### `GET /internal/dividends/by-date-range` - Get dividends in date range
**Usage**: Filter dividendes par période (année fiscale)
**Nécessaire?**: ✅ Oui - Feature business essentielle
**Utilisé par?**: BFF (filtrage année fiscale pour génération forms)
**Décision**: 🟢 **GARDER** - Ajouter à OpenAPI spec

**Spec OpenAPI à ajouter**:
```yaml
/dividends:
  get:
    summary: List user dividends
    parameters:
      - name: userId
        in: query
        required: true
      - name: startDate
        in: query
        schema:
          type: string
          format: date
      - name: endDate
        in: query
        schema:
          type: string
          format: date
```

### `GET /internal/dividends/unsubmitted` - Get dividends not yet submitted
**Usage**: Lister dividendes sans formulaire généré
**Nécessaire?**: ✅ Oui - Feature business (workflow génération forms)
**Utilisé par?**: BFF (afficher dividendes à traiter)
**Décision**: 🟢 **GARDER** - Ajouter à OpenAPI spec

**Spec OpenAPI à ajouter**:
```yaml
/dividends:
  get:
    summary: List user dividends
    parameters:
      - name: userId
        in: query
        required: true
      - name: status
        in: query
        schema:
          type: string
          enum: [UNSUBMITTED, SUBMITTED, APPROVED, PAID]
```

**Résumé DividendController**: 🟢 **GARDER les 3 endpoints** - Features business essentielles

---

## 3. FormController (6 endpoints)

### `POST /internal/forms/generate` - Generate Form 5000/5001/BUNDLE
**Usage**: Génération de formulaires fiscaux
**Nécessaire?**: ✅ Oui - **CORE FEATURE**
**Status actuel**: Endpoint existe dans spec mais implémentation incomplète
**Décision**: ✅ **DÉJÀ DANS SPEC** - Implémenter PdfGenerationService (Sprint 3)

### `GET /internal/forms/{id}/download-url` - Get pre-signed download URL
**Usage**: URL temporaire pour télécharger PDF depuis MinIO
**Nécessaire?**: ✅ Oui - Sécurité (évite d'exposer MinIO directement)
**Utilisé par?**: BFF (afficher lien download au frontend)
**Décision**: 🟢 **GARDER** - Ajouter à OpenAPI spec

**Spec OpenAPI à ajouter**:
```yaml
/forms/{id}/download-url:
  get:
    summary: Get pre-signed download URL
    operationId: getFormDownloadUrl
    parameters:
      - name: id
        in: path
        required: true
        schema:
          type: string
          format: uuid
      - name: expiresIn
        in: query
        schema:
          type: integer
          default: 3600
          description: URL expiration in seconds
    responses:
      200:
        description: Pre-signed URL
        content:
          application/json:
            schema:
              type: object
              properties:
                url:
                  type: string
                  format: uri
                expiresAt:
                  type: string
                  format: date-time
```

### `POST /internal/forms/{id}/regenerate` - Regenerate expired form
**Usage**: Régénérer un formulaire expiré (après 30 jours)
**Nécessaire?**: ✅ Oui - Feature business (forms expirent)
**Utilisé par?**: BFF (bouton "Regenerate" si expiré)
**Décision**: 🟢 **GARDER** - Ajouter à OpenAPI spec

**Spec OpenAPI à ajouter**:
```yaml
/forms/{id}/regenerate:
  post:
    summary: Regenerate expired form
    operationId: regenerateForm
    parameters:
      - name: id
        in: path
        required: true
        schema:
          type: string
          format: uuid
    responses:
      200:
        description: Form regenerated
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/GeneratedForm'
```

### `POST /internal/forms/generate-all-unsubmitted` - Generate forms for all unsubmitted dividends
**Usage**: Batch génération pour tous les dividendes d'un user
**Nécessaire?**: ⚠️ Questionable - Peut être fait par multiple appels à `/forms/generate`
**Utilisé par?**: BFF (bouton "Generate all")
**Décision**: 🟡 **À DISCUTER** - Utile pour UX mais peut être côté BFF

**Recommandation**: 🔴 **SUPPRIMER** - Le BFF peut boucler sur `/forms/generate`

**Résumé FormController**:
- ✅ `/forms/generate` - Déjà dans spec, implémenter (Sprint 3)
- 🟢 `/forms/{id}/download-url` - GARDER
- 🟢 `/forms/{id}/regenerate` - GARDER
- 🔴 `/forms/generate-all-unsubmitted` - SUPPRIMER (logique BFF)

---

## 4. TaxRuleController (13 endpoints)

### Endpoints de filtrage (9 endpoints)

Ces endpoints permettent de filtrer les tax rules par critères:
- `/tax-rules/by-countries` - Filter by source + residence
- `/tax-rules/active` - Only active rules
- `/tax-rules/expired` - Only expired rules
- `/tax-rules/check-treaty` - Check if treaty exists
- `/tax-rules/by-source-country` - Filter by source
- `/tax-rules/by-residence-country` - Filter by residence
- `/tax-rules/with-relief-at-source` - With relief at source
- `/tax-rules/with-refund-procedure` - With refund procedure

**Nécessaire?**: ⚠️ Partiellement - Certains utiles, d'autres redondants
**Alternative**: Endpoint `/tax-rules` existant avec query params

**Décision**: 🔴 **SUPPRIMER** - Utiliser `/tax-rules` avec query params

**Spec OpenAPI à améliorer**:
```yaml
/tax-rules:
  get:
    summary: List tax rules with filters
    parameters:
      - name: sourceCountry
        in: query
        schema:
          type: string
      - name: residenceCountry
        in: query
        schema:
          type: string
      - name: active
        in: query
        schema:
          type: boolean
      - name: reliefAtSource
        in: query
        schema:
          type: boolean
      - name: refundProcedure
        in: query
        schema:
          type: boolean
```

### Endpoints avec @RequestParam (4 endpoints - problème technique)

Ces endpoints sont **déjà dans l'OpenAPI spec** mais les tests échouent:
- `/tax-rules/applicable` - Find applicable rule
- `/tax-rules/treaty-rate` - Get treaty rate

**Problème**: WebMvcTest + Spring Boot 4 retourne 404 pour @RequestParam
**Décision**: ✅ **GARDER** - Fixer les tests (passer à @SpringBootTest ou investiguer)

---

## 📊 Résumé des décisions

| Controller | Endpoints analysés | À GARDER | À SUPPRIMER |
|------------|-------------------|----------|-------------|
| HealthController | 7 | 0 | 7 |
| DividendController | 3 | 3 | 0 |
| FormController | 6 | 3 | 3 |
| TaxRuleController | 13 | 4 (déjà spec) | 9 |
| **TOTAL** | **29** | **10** | **19** |

---

## 🎯 Plan d'action Sprint 2

### Jour 1-2: Ajouter endpoints manquants à OpenAPI
- ✅ DividendController: 3 endpoints
- ✅ FormController: 2 endpoints (`/download-url`, `/regenerate`)
- ✅ TaxRuleController: Améliorer endpoint existant avec query params

### Jour 3: Régénérer + implémenter
- Régénérer interfaces OpenAPI
- Implémenter nouvelles méthodes dans controllers
- Créer DTOs manquants si besoin

### Jour 4: Supprimer code obsolète
- Supprimer 7 endpoints HealthController
- Supprimer 9 endpoints TaxRuleController (filtrage)
- Supprimer 3 endpoints FormController (batch/non nécessaires)
- Supprimer tests associés

### Jour 5: Fixer tests + documentation
- Fixer 4 tests WebMvcTest (@RequestParam issue)
- Réactiver tests des endpoints gardés
- Mettre à jour TEST_STATUS.md
- Mettre à jour README.md

---

## ✅ Validation avant implémentation

**Questions à valider avec l'équipe**:
1. ❓ `/forms/generate-all-unsubmitted` - Vraiment nécessaire ou logique BFF?
2. ❓ TaxRuleController filtrage - Query params suffisants ou besoin d'endpoints dédiés?
3. ❓ HealthController - Besoin de checks custom ou Actuator standard suffit?

**Prêt à démarrer?** ✅
