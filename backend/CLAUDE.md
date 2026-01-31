# CLAUDE.md - Backend

Ce fichier fournit des directives à Claude Code pour travailler sur le backend du projet Tax Dividend AI.

## Vue d'ensemble du projet Backend

**Tax Dividend AI Backend** est une API REST Spring Boot 4.0.2 avec Java 25 qui gère les opérations de calcul fiscal et de génération de formulaires pour les investisseurs transfrontaliers.

## Stack Technique

### Core
- **Java**: 25 (LTS depuis septembre 2025)
- **Spring Boot**: 4.0.2
- **Maven**: 3.9.12
- **Base de données**: PostgreSQL avec Flyway pour les migrations
- **ORM**: Spring Data JPA (Hibernate)

### Dépendances principales
- **Lombok**: 1.18.42 (compatible Java 25)
- **OpenAPI Generator**: 7.1.0 (contract-first)
- **PDF Processing**: Apache PDFBox 3.0.3
- **Storage**: MinIO 8.5.7
- **Testing**: JUnit 5, Testcontainers 1.20.4
- **Metrics**: Micrometer + Prometheus
- **Tracing**: OpenTelemetry

## Architecture Contract-First

### Principe fondamental

Le backend suit une approche **contract-first** avec OpenAPI:

```
OpenAPI Spec (specs/backend/openapi.yaml)
    ↓ génère (via maven plugin)
API Interfaces + DTOs API (target/generated-sources/)
    ↓ implémentées par
Controllers (manipulent uniquement DTOs API)
    ↓ appellent
Services (retournent DTOs API via Mappers)
    ↓ manipulent
Repositories/Entities (JPA entities - jamais exposées)
```

### Règle architecturale CRITIQUE

**Les entités JPA restent dans le service/repository. Les DTOs API sont créés par le service (via mappers) avant que les données ne quittent le service. Le controller manipule uniquement des objets sûrs et adaptés à l'API.**

❌ **INTERDIT:**
```java
@RestController
public class DividendController {
    private final DividendRepository repository;

    @GetMapping
    public List<Dividend> list() {  // ❌ Retourne Entity directement
        return repository.findAll();
    }
}
```

✅ **CORRECT:**
```java
@RestController
public class DividendController implements DividendsApi {
    private final DividendService service;

    @Override
    public ResponseEntity<ListDividends200Response> listDividends(UUID userId, Integer page, Integer size) {
        return ResponseEntity.ok(service.listDividends(userId, pageable));  // ✅ Service retourne DTO API
    }
}
```

## Structure du projet

### Packages

```
com.taxdividend.backend/
├── api/                    # Interfaces générées (ne pas éditer)
│   └── dto/                # DTOs API générés (ne pas éditer)
├── config/                 # Configuration Spring
│   ├── InternalSecurityConfig.java
│   ├── OpenApiConfig.java
│   └── StorageConfig.java
├── controller/             # REST Controllers (implémentent API interfaces)
│   ├── DividendController.java
│   ├── FormController.java
│   ├── TaxRuleController.java
│   ├── HealthController.java
│   └── AuthController.java
├── dto/                    # DTOs internes (business logic)
│   ├── TaxCalculationResultDTO.java
│   ├── GenerateFormResultDTO.java
│   └── ...
├── mapper/                 # Conversion Entity ↔ DTO API
│   ├── DividendMapper.java
│   ├── FormMapper.java
│   ├── TaxCalculationMapper.java
│   └── TaxRuleMapper.java
├── model/                  # JPA Entities
│   ├── User.java
│   ├── Dividend.java
│   ├── GeneratedForm.java
│   └── TaxRule.java
├── repository/             # Spring Data JPA repositories
├── service/                # Business logic layer
│   ├── DividendService.java
│   ├── FormService.java
│   ├── TaxRuleService.java
│   ├── TaxCalculationService.java
│   └── PdfGenerationService.java
└── service/impl/          # Service implementations
```

### Contrôleurs et APIs

| Contrôleur | Interface API | Service | Statut |
|------------|---------------|---------|---------|
| DividendController | DividendsApi | DividendService | ✅ Contract-first |
| FormController | FormsApi | FormService | ✅ Contract-first |
| TaxRuleController | TaxRulesApi | TaxRuleService | ✅ Contract-first |
| HealthController | HealthApi | - | ✅ Contract-first |
| AuthController | AuthApi | - | ✅ Contract-first |
| ~~PdfController~~ | PdfApi | - | ⚠️ Non implémenté (fusionné avec FormController?) |

## Services et responsabilités

### DividendService
Gestion des dividendes utilisateur:
- Liste paginée des dividendes (retourne DTOs API)
- Détails d'un dividende (retourne DTO API)
- Filtrage par date, statut
- Suppression avec vérification ownership
- **Ne retourne JAMAIS d'entités JPA**

### FormService
Gestion des formulaires fiscaux générés:
- Liste des formulaires (retourne DTOs API)
- Téléchargement de formulaires (retourne Resource)
- Suppression avec nettoyage storage
- URLs pré-signées
- **Ne retourne JAMAIS d'entités JPA**

### TaxRuleService
Consultation des règles fiscales:
- Recherche de règles applicables
- Calcul des taux de traité
- **Agent IA autonome** pour mise à jour des règles fiscales (à implémenter)

### TaxCalculationService
Calculs fiscaux:
- Calcul pour un dividende
- Calcul batch
- Utilise TaxRuleService pour obtenir les règles

### PdfGenerationService
Génération de PDF (Forms 5000, 5001):
- Génération de formulaires individuels
- Génération de bundles (ZIP)
- Régénération de formulaires expirés

## Agents IA autonomes

### 1. Agent de parsing des Dividend Statements (À implémenter)

**Responsabilité**: Parser les relevés de dividendes PDF des brokers (Interactive Brokers, Swissquote, etc.)

**Localisation**: Service externe autonome (pas dans le backend Spring Boot)

**Flux**:
```
User upload PDF → Storage (MinIO/S3)
    ↓
Backend déclenche agent IA
    ↓
Agent IA parse PDF (LLM-assisted)
    ↓
Agent retourne données structurées
    ↓
Backend crée entités Dividend
```

**Note**: Le parsing n'est PAS fait dans `PdfParsingService` - ce service a été supprimé. Un agent IA externe le remplacera.

### 2. Agent de mise à jour des Tax Rules (À implémenter)

**Responsabilité**: Maintenir les règles fiscales à jour en consultant les traités fiscaux officiels

**Localisation**: Service externe autonome avec accès en écriture à la base

**Flux**:
```
Agent surveille sources officielles
    ↓
Détecte changements de traités
    ↓
Valide avec LLM
    ↓
Propose mise à jour
    ↓
Admin approuve → Update TaxRule entities
```

## Base de données

### Migrations Flyway

Localisation: `src/main/resources/db/migration/`

Migrations existantes:
- `V1__create_users_table.sql`
- `V2__create_forms_tables.sql`
- `V3__create_submissions_and_audit_tables.sql`
- `V4__create_tax_rules_table.sql`
- `V5__insert_default_tax_rules.sql`

**Règle**: Toujours créer une nouvelle migration, JAMAIS modifier une migration existante.

### Entities principales

#### User
- Informations utilisateur
- Pays de résidence (pour calculs fiscaux)
- Statut de vérification

#### Dividend
- Dividendes reçus par l'utilisateur
- Lien vers User
- Lien optionnel vers GeneratedForm
- Montants bruts, taxes retenues, montants récupérables

#### TaxRule
- Règles de traité fiscal
- Source country → Residence country
- Taux standard vs taux de traité
- Dates d'effectivité
- **Mise à jour par agent IA autonome**

#### GeneratedForm
- Métadonnées des formulaires générés
- Lien vers fichier storage (S3/MinIO)
- Type (5000, 5001, BUNDLE)
- Dates de génération/expiration

## Configuration OpenAPI Generator

### pom.xml plugin

```xml
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>7.1.0</version>
    <configuration>
        <inputSpec>${project.basedir}/../specs/backend/openapi.yaml</inputSpec>
        <generatorName>spring</generatorName>
        <library>spring-boot</library>
        <apiPackage>com.taxdividend.backend.api</apiPackage>
        <modelPackage>com.taxdividend.backend.api.dto</modelPackage>
        <configOptions>
            <interfaceOnly>true</interfaceOnly>
            <useSpringBoot3>true</useSpringBoot3>
            <useTags>true</useTags>
            <documentationProvider>none</documentationProvider>
            <useJakartaEe>true</useJakartaEe>
        </configOptions>
    </configuration>
</plugin>
```

### Génération

```bash
mvn clean generate-sources  # Génère les interfaces API et DTOs
```

**IMPORTANT**: Les fichiers générés dans `target/generated-sources/openapi/` ne doivent JAMAIS être édités manuellement.

## Commandes de développement

```bash
# Compilation
mvn clean compile

# Compilation sans tests
mvn compile -DskipTests

# Génération OpenAPI + Compilation
mvn clean generate-sources compile

# Tests
mvn test

# Package
mvn clean package

# Run local
mvn spring-boot:run

# Coverage report
mvn jacoco:report
```

## Règles de codage

### 1. Jamais exposer les entités JPA

❌ Ne JAMAIS faire:
```java
public ResponseEntity<Dividend> getDividend() { ... }  // Entity exposée
public List<GeneratedForm> listForms() { ... }        // Entities exposées
```

✅ Toujours faire:
```java
public ResponseEntity<com.taxdividend.backend.api.dto.Dividend> getDividend() { ... }
public List<com.taxdividend.backend.api.dto.GeneratedForm> listForms() { ... }
```

### 2. Services retournent des DTOs API

Les services doivent utiliser les mappers pour convertir entities → DTOs API:

```java
@Service
public class DividendServiceImpl implements DividendService {
    private final DividendRepository repository;
    private final DividendMapper mapper;

    public Optional<com.taxdividend.backend.api.dto.Dividend> getDividend(UUID id, UUID userId) {
        return repository.findById(id)
            .filter(entity -> entity.getUser().getId().equals(userId))
            .map(mapper::toApiDto);  // ✅ Entity → DTO API
    }
}
```

### 3. Controllers délèguent aux services

Les controllers NE font PAS de logique métier:

```java
@RestController
public class DividendController implements DividendsApi {
    private final DividendService service;  // Pas de Repository!

    @Override
    public ResponseEntity<Dividend> getDividend(UUID id, UUID userId) {
        return service.getDividend(id, userId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
```

### 4. Mappers sont dédiés et simples

Un mapper par domaine (Dividend, Form, TaxRule, etc.):

```java
@Component
public class DividendMapper {
    public com.taxdividend.backend.api.dto.Dividend toApiDto(
        com.taxdividend.backend.model.Dividend entity) {
        // Conversion entity → DTO API
    }

    public List<com.taxdividend.backend.api.dto.Dividend> toApiDtoList(
        List<com.taxdividend.backend.model.Dividend> entities) {
        // Conversion liste
    }
}
```

### 5. Gestion des erreurs

- `404 Not Found`: Ressource n'existe pas ou user n'a pas accès
- `400 Bad Request`: Données invalides
- `500 Internal Server Error`: Erreur serveur (avec logs)
- Toujours logger les erreurs avec contexte (`log.error()`)

### 6. Audit trail

Utiliser `AuditService` pour tracer les opérations critiques:
- Génération de formulaires
- Calculs fiscaux
- Suppressions de données

## Sécurité

### Authentication
- Header `X-User-Id` pour identifier l'utilisateur (temporaire)
- À remplacer par JWT tokens

### Authorization
- Vérification ownership: toujours filtrer par `userId`
- Exemple: `repository.findById(id).filter(e -> e.getUser().getId().equals(userId))`

### Validation
- `@Valid` sur les request bodies
- Jakarta Validation annotations sur les DTOs

## Tests

### Structure
```
src/test/java/
├── controller/    # Tests d'intégration (MockMvc)
├── service/       # Tests unitaires
└── repository/    # Tests avec Testcontainers
```

### Testcontainers
Utilisé pour tests d'intégration avec vraie base PostgreSQL.

### Coverage
Minimum requis: 60% (configuré dans jacoco-maven-plugin)

## Domaine fiscal

### Formulaires français
- **5000**: Attestation de résidence fiscale (Suisse)
- **5001**: Liquidation des dividendes

### Concepts
- **Withholding tax**: Taxe retenue à la source
- **Treaty rate**: Taux selon traité fiscal
- **Reclaimable amount**: Montant récupérable = withheld - treaty
- **Relief at source**: Application du taux réduit immédiatement
- **Tax refund (Aval)**: Récupération après paiement

## Points d'attention

### 1. Ne pas supprimer PdfParsingService tant que l'agent IA n'est pas prêt
Le service existe encore mais ne doit pas être utilisé pour de nouveaux développements.

### 2. OpenAPI spec est la source de vérité
Toute modification d'API doit commencer par `specs/backend/openapi.yaml`, puis régénération.

### 3. Flyway migrations sont immuables
Une fois en production, ne jamais modifier une migration existante. Créer une nouvelle migration.

### 4. Java 25 LTS
Le projet utilise Java 25 LTS (septembre 2025). Toujours vérifier la compatibilité des dépendances.

### 5. Mappers vs DTOs internes
- **DTOs API** (`com.taxdividend.backend.api.dto`): générés, pour API externe
- **DTOs internes** (`com.taxdividend.backend.dto`): pour logique métier interne
- Les mappers convertissent entre les deux quand nécessaire

## Ressources

- OpenAPI Spec: `specs/backend/openapi.yaml`
- Migrations DB: `src/main/resources/db/migration/`
- Application config: `src/main/resources/application.yml`
- Documentation officielle formulaires français: `docs/officials/fr/`

## TODO / Roadmap

- [ ] Implémenter agent IA de parsing de dividend statements
- [ ] Implémenter agent IA de mise à jour des tax rules
- [ ] Remplacer X-User-Id par JWT authentication
- [ ] Implémenter PdfController ou confirmer fusion avec FormController
- [ ] Augmenter coverage de tests à 80%+
- [ ] Ajouter rate limiting
- [ ] Ajouter caching (Redis) pour tax rules
