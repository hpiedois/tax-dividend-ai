# Backend - État actuel

Date: 2026-01-29

## ✅ Migration Contract-First: Complétée

Tous les contrôleurs implémentent leurs interfaces API générées:

| Contrôleur | Interface | Service dédié | Mappers | Statut |
|------------|-----------|---------------|---------|---------|
| DividendController | DividendsApi | ✅ DividendService | ✅ DividendMapper | ✅ Complet |
| FormController | FormsApi | ✅ FormService | ✅ FormMapper | ✅ Complet |
| TaxRuleController | TaxRulesApi | ✅ TaxRuleService | ✅ TaxRuleMapper | ✅ Complet |
| HealthController | HealthApi | N/A | N/A | ✅ Complet |
| AuthController | AuthApi | N/A | N/A | ✅ Complet |

## 🏗️ Architecture implémentée

```
OpenAPI Spec (specs/backend/openapi.yaml)
    ↓
API Interfaces + DTOs générés (target/generated-sources/)
    ↓
Controllers (implémentent interfaces, manipulent DTOs API uniquement)
    ↓
Services (retournent DTOs API via Mappers)
    ↓
Repositories (manipulent Entities JPA)
```

### Principe appliqué

**Les entités JPA ne quittent JAMAIS la couche service.**

- Controllers: manipulent uniquement `com.taxdividend.backend.api.dto.*`
- Services: utilisent mappers pour convertir Entities → DTOs API
- Repositories: travaillent avec `com.taxdividend.backend.model.*`

## 🔧 Services créés

### DividendService / DividendServiceImpl
- `listDividends()`: retourne `ListDividends200Response` (DTO API)
- `getDividend()`: retourne `Optional<Dividend>` (DTO API)
- `getDividendsByDateRange()`: retourne `List<Dividend>` (DTO API)
- `getUnsubmittedDividends()`: retourne `List<Dividend>` (DTO API)
- `deleteDividend()`: void

### FormService / FormServiceImpl
- `listForms()`: retourne `List<GeneratedForm>` (DTO API)
- `getForm()`: retourne `Optional<GeneratedForm>` (DTO API)
- `downloadForm()`: retourne `ResponseEntity<Resource>`
- `getDownloadUrl()`: retourne `Optional<String>`
- `deleteForm()`: void
- `getFormsByStatus()`: retourne `List<GeneratedForm>` (DTO API)

### TaxRuleService / TaxRuleServiceImpl
- `getAllTaxRules()`: retourne `List<TaxRule>` (DTO API)
- `getTaxRule()`: retourne `Optional<TaxRule>` (DTO API)
- `findApplicableRule()`: retourne `Optional<TaxRule>` (DTO API)
- `getTreatyRate()`: retourne `Optional<TreatyRateResponse>` (DTO API)

## 🗺️ Mappers créés

### DividendMapper
- `toDto()`: Entity → DTO API
- `toDtoList()`: List<Entity> → List<DTO>
- `toPageResponse()`: Page<Entity> → ListDividends200Response
- `toEntity()`: DTO API → Entity

### FormMapper
- `toApiDto()`: Entity → DTO API
- `toApiDtoList()`: List<Entity> → List<DTO>
- `toApiResultDto()`: Internal DTO → API DTO

### TaxCalculationMapper
- `toApiDto()`: Internal DTO → API DTO
- `toApiBatchDto()`: Internal Batch DTO → API Batch DTO

### TaxRuleMapper
- `toApiDto()`: Entity → DTO API
- `toApiDtoList()`: List<Entity> → List<DTO>
- `toTreatyRateResponse()`: Entity → TreatyRateResponse

## ✅ Erreurs de compilation corrigées

### 1. AuthController ✅
**Erreur**: `return type ResponseEntity<Object> is not compatible with ResponseEntity<RegisterUser200Response>`
**Fix appliqué**: Retourné `RegisterUser200Response` avec instanciation correcte du DTO

### 2. FormMapper ✅
**Erreur**: `cannot find symbol: getFormType(), getTaxYear()`, conversion `LocalDateTime` → `OffsetDateTime`
**Fix appliqué**:
- Ajouté champ `fileSize` dans `GeneratedForm` entity
- Conversion `LocalDateTime` → `OffsetDateTime` avec `atOffset(ZoneOffset.UTC)`

### 3. TaxRuleMapper ✅
**Erreur**: `cannot find symbol: isReliefAtSourceAvailable(), isRefundProcedureAvailable()`
**Fix appliqué**: Changé `isXxx()` → `getXxx()` car champs Boolean (pas boolean primitifs)

### 4. FormController ✅
**Erreur**: `cannot find symbol: Optional` (ligne 172)
**Fix appliqué**: Ajouté `import java.util.Optional;`

### Résultat
```
[INFO] BUILD SUCCESS
[INFO] Compiling 68 source files
```

## 📦 Packages OpenAPI générés

Localisation: `target/generated-sources/openapi/`

### Interfaces API
- `AuthApi.java`
- `DividendsApi.java`
- `FormsApi.java`
- `HealthApi.java`
- `PdfApi.java` ⚠️ (non implémenté)
- `TaxRulesApi.java`

### DTOs API
- `Dividend.java`
- `FormGenerationRequest.java`
- `GenerateFormResultDTO.java`
- `GeneratedForm.java`
- `HealthCheckResponse.java`
- `ListDividends200Response.java`
- `RegisterUser200Response.java`
- `RegisterUserRequest.java`
- `TaxCalculationBatchResultDTO.java`
- `TaxCalculationResultDTO.java`
- `TaxRule.java`
- `TreatyRateResponse.java`

## 🚫 PdfApi / PdfController

**Statut**: Interface générée mais pas de contrôleur

**Options**:
1. Créer `PdfController implements PdfApi`
2. Fusionner avec `FormController` (endpoint `/pdf/generate` semble redondant avec `/forms/generate`)

**Recommandation**: Vérifier avec specs si `/pdf/generate` est vraiment nécessaire ou si c'est un reliquat.

## 🧹 Services à nettoyer

### PdfParsingService (deprecated)
- **Statut**: Existe encore mais ne doit PAS être utilisé
- **Raison**: Sera remplacé par Agent IA autonome
- **Action**: Garder temporairement, marquer @Deprecated

## ✅ Migration Spring Boot 4.0 appliquée

### Changements appliqués (via skill springboot-migration)

**Phase 1: Dependencies (pom.xml)**
1. ✅ `spring-boot-starter-web` → `spring-boot-starter-webmvc`
2. ✅ `spring-security-test` → `spring-boot-starter-security-test`
3. ⚠️ Testcontainers: Les nouveaux artifacts (`testcontainers-junit-jupiter`, `testcontainers-postgresql`) n'existent pas encore en version 1.20.4, conservés anciens noms

**Phase 2: Code Changes**
1. ✅ `@MockBean` → `@MockitoBean` (Spring Boot 4.0)
2. ✅ Import `@WebMvcTest` corrigé dans 4 fichiers:
   - `org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest`
   - → `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`
3. ✅ Import `FormGenerationRequest` corrigé (`api.dto` au lieu de `model`)

**Résultat**: Les erreurs de packages manquants Spring Boot 4 sont **résolues** ✅

## ✅ Corrections des tests - COMPLÈTES

Toutes les erreurs de tests ont été corrigées:

### 1. FormControllerTest
- ✅ Corrigé `firstName()`/`lastName()` → `fullName()` (User entity)
- ✅ Corrigé `generatedAt()` → `createdAt()` (GeneratedForm entity)
- ✅ Corrigé `GenerateFormRequest` → `FormGenerationRequest` (nom correct du DTO API)
- ✅ Remplacé `.builder()` par constructeur + méthodes fluides (DTO sans builder)
- ✅ Corrigé `formType("5000")` → `formType(FormTypeEnum._5000)` (enum)

### 2. DividendControllerTest
- ✅ Supprimé `.success(true)` - TaxCalculationResultDTO n'a pas ce champ
- ✅ Supprimé assertion `jsonPath("$.success")` dans les tests

### 3. TaxCalculationServiceTest
- ✅ Supprimé toutes assertions `.getSuccess()` - DTO n'a pas ce champ
- ✅ Supprimé assertions `.getErrorMessage()` - DTO n'a pas ce champ
- ✅ Corrigé type retour `Optional<TaxRule>` → `UUID` (méthode findApplicableTaxRule)

### 4. PdfGenerationServiceTest
- ✅ Corrigé `GenerateFormRequest` → `FormGenerationRequest`
- ✅ Remplacé `.builder()` par constructeur + méthodes fluides
- ✅ Corrigé signature `uploadFile()`: `anyLong()` → `anyString()` (contentType)
- ✅ Corrigé User builder: `firstName()`/`lastName()` → `fullName()`
- ✅ Supprimé champs inexistants: `city`, `postalCode`, `addressLine1`, `nif` → `address`, `taxId`

### 5. TaxRuleControllerTest
- ✅ Corrigé `expiresAt()` → `effectiveTo()` (TaxRule entity)

### 6. StorageServiceTest
- ✅ Corrigé signature `uploadFile()`: supprimé paramètre `long` (taille), ajouté `folder`


## 🎯 Prochaines étapes

### Immédiat ✅
~~1. Fixer AuthController return type~~
~~2. Fixer FormMapper getters~~
~~3. Fixer TaxRuleMapper getters~~
~~4. Investiguer erreur FormController ligne 172~~

### Court terme
1. Décider du sort de PdfApi/PdfController
2. Ajouter tests unitaires pour tous les mappers
3. Ajouter tests d'intégration pour les services
4. Améliorer coverage (objectif 80%+)

### Moyen terme
1. Implémenter Agent IA de parsing de dividend statements
2. Implémenter Agent IA de mise à jour des tax rules
3. Remplacer X-User-Id par JWT authentication
4. Ajouter caching (Redis) pour tax rules

## 📊 Métriques

- **Contrôleurs**: 5/5 implémentent interfaces API ✅
- **Services**: 3/3 retournent DTOs API ✅
- **Mappers**: 4/4 créés ✅
- **Compilation source**: ✅ BUILD SUCCESS (68 fichiers compilés)
- **Migration Spring Boot 4**: ✅ COMPLÈTE
- **Compilation tests**: ✅ BUILD SUCCESS (8 fichiers de tests compilés)
- **Tests**: ⚠️ (prêts à être exécutés)
- **Coverage**: N/A (tests non exécutés)

## 🔑 Règles architecturales appliquées

1. ✅ Contract-first avec OpenAPI
2. ✅ Controllers implémentent interfaces générées
3. ✅ Services retournent DTOs API
4. ✅ Mappers séparent Entities et DTOs API
5. ✅ Entities JPA jamais exposées hors services
6. ✅ Un service dédié par domaine
7. ✅ Injection de services dans controllers (pas de repositories)

## 📝 Notes

- Java 25 LTS utilisé (compatible Lombok 1.18.42)
- Spring Boot 4.0.2
- OpenAPI Generator 7.1.0
- Testcontainers 1.20.4 pour tests d'intégration
- Flyway pour migrations DB (5 migrations appliquées)
