# Audit Backend & BFF - État des Lieux et Recommandations

**Date**: 2026-02-01
**Périmètre**: Backend Spring Boot 4 + BFF Gateway

---

## 📊 État des Lieux Backend

### ✅ Points Positifs

#### Tests
- **132 tests** qui passent (0 failures, 0 errors)
- 22 tests skipped (probablement @Disabled ou conditions non remplies)
- Tests bien organisés :
  - 20 tests DividendStatementService (unit)
  - 13 tests DividendStatementController (integration)
  - 11 tests PdfGenerationService
  - Tests pour TaxRuleService, FormService, DividendService

#### Architecture
- ✅ **Contract-first** avec OpenAPI (génération des DTOs et interfaces)
- ✅ Séparation claire : Entity → Internal DTO → API DTO
- ✅ Mappers dédiés pour chaque domaine
- ✅ Services avec implémentations séparées
- ✅ Transactions gérées correctement (@Transactional)
- ✅ Audit trail avec AuditService
- ✅ PDF templates officiels intégrés (Forms 5000/5001)

#### Fonctionnalités Complètes
- ✅ DividendStatement workflow (UPLOADED → PARSING → PARSED → VALIDATED → SENT → PAID)
- ✅ Bulk import endpoint pour AI Agent
- ✅ Génération PDF avec templates officiels
- ✅ Calculs fiscaux avec TaxRuleService
- ✅ Storage MinIO intégré

### ❌ Points à Améliorer

#### 1. Couverture de Tests - CRITIQUE ⚠️

**Couverture Globale: 24%** (objectif: 60%)

| Package | Couverture | Objectif | Gap |
|---------|-----------|----------|-----|
| **service.impl** | 52% | 60% | -8% |
| **controller** | 51% | 60% | -9% |
| **api.dto** | 12% | 60% | -48% |
| **service.pdf** | 1% | 60% | -59% |
| **mapper** | 0% | 60% | -60% |
| **service** (interfaces) | 0% | 60% | -60% |
| **api** (interfaces) | 0% | 60% | -60% |
| **config** | 0% | 60% | -60% |
| **security** | 1% | 60% | -59% |

**Problèmes identifiés**:
- ❌ Interfaces (api, service) comptées dans la couverture mais non testables directement
- ❌ DTOs générés comptés mais sans tests (normal pour code généré)
- ❌ Config classes pas testées
- ❌ Security pas testé
- ❌ Mappers pas testés (0%)
- ❌ Service.pdf quasi pas testé (1%)

#### 2. Configuration Jacoco Non Optimale

```xml
<!-- Problème: Check trop strict sur du code non-testable -->
<execution>
    <id>jacoco-check</id>
    <goals><goal>check</goal></goals>
    <configuration>
        <rules>
            <rule>
                <element>PACKAGE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.60</minimum> <!-- Trop strict pour TOUS les packages -->
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

**Conséquence**: Build FAILURE à cause de packages non pertinents (interfaces, DTOs générés, config).

#### 3. Packages à Exclure de la Couverture

Packages qui **ne devraient PAS** être inclus dans les objectifs de couverture :
- `com.taxdividend.backend.api` (interfaces générées)
- `com.taxdividend.backend.api.dto` (DTOs générés)
- `com.taxdividend.backend.service` (interfaces)
- `org.openapitools.configuration` (configuration OpenAPI générée)
- `com.taxdividend.backend.config` (Spring configurations - difficile à tester)
- `com.taxdividend.backend.exception` (classes simples)

#### 4. Packages à Tester en Priorité

| Package | Couverture Actuelle | Objectif | Actions |
|---------|-------------------|----------|---------|
| **service.impl** | 52% | 80% | +28% - Ajouter tests manquants |
| **controller** | 51% | 80% | +29% - Tests integration manquants |
| **mapper** | 0% | 70% | +70% - Tests unitaires mappers |
| **service.pdf** | 1% | 70% | +69% - Tests PDF form filling |
| **security** | 1% | 60% | +59% - Tests security filter |

---

## 📊 État des Lieux BFF Gateway

### Structure Actuelle

```
bff-gateway/
├── src/main/java/com/taxdividend/bff/
│   ├── config/          # Configuration
│   ├── controller/      # REST controllers
│   ├── service/         # Business logic
│   ├── mapper/          # Entity ↔ DTO
│   ├── model/           # BFF-specific models
│   ├── client/          # Backend API client (généré)
│   ├── agent/client/    # Agent AI client (généré)
│   └── security/        # Security config
└── src/test/java/
    └── 1 seul fichier de test ⚠️
```

### ✅ Points Positifs

- Architecture réactive (WebFlux)
- Client backend généré via OpenAPI
- Structure claire et organisée
- Utilise le pattern BFF correctement

### ❌ Points à Améliorer - CRITIQUE ⚠️

#### 1. Tests Quasi Inexistants
- **1 seul fichier de test** : `DividendServiceTest.java`
- Pas de tests controllers
- Pas de tests mappers
- Pas de tests security
- **Aucun Jacoco configuré** - Pas de mesure de couverture

#### 2. Pas de Configuration Jacoco

Le `pom.xml` du BFF n'a **PAS** de plugin Jacoco configuré.

#### 3. Incohérences avec Backend

| Aspect | Backend | BFF | Alignement |
|--------|---------|-----|------------|
| Tests | 132 tests | ~1 test | ❌ |
| Couverture | Jacoco activé | Pas de Jacoco | ❌ |
| Structure services | Impl séparées | Services directs | ⚠️ |
| Mappers | Testés partiellement | Non testés | ❌ |
| Documentation | Bonne | Manquante | ❌ |

---

## 🎯 Plan d'Action Recommandé

### Phase 1: Configuration Jacoco (Backend + BFF)

#### Backend - Optimiser Jacoco

**Objectif**: Exclure le code non testable, fixer des objectifs réalistes

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.13</version>
    <configuration>
        <excludes>
            <!-- Code généré -->
            <exclude>**/api/**/*</exclude>
            <exclude>**/api/dto/**/*</exclude>
            <exclude>**/org/openapitools/**/*</exclude>
            <!-- Configuration Spring -->
            <exclude>**/config/**/*</exclude>
            <!-- Exceptions simples -->
            <exclude>**/exception/**/*</exclude>
            <!-- Application main -->
            <exclude>**/TaxDividendBackendApplication.class</exclude>
        </excludes>
    </configuration>
    <executions>
        <execution>
            <id>jacoco-check</id>
            <goals><goal>check</goal></goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.60</minimum> <!-- Sur le bundle global -->
                            </limit>
                        </limits>
                    </rule>
                    <!-- Règles spécifiques par package -->
                    <rule>
                        <element>PACKAGE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.70</minimum>
                            </limit>
                        </limits>
                        <includes>
                            <include>com.taxdividend.backend.service.impl</include>
                            <include>com.taxdividend.backend.mapper</include>
                        </includes>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

#### BFF - Ajouter Jacoco

**Fichier**: `bff-gateway/pom.xml`

Ajouter le même plugin Jacoco avec configuration adaptée.

### Phase 2: Tests Manquants Backend

#### Priorité 1 - Mappers (0% → 70%)

Créer tests pour :
- `DividendMapper` (5 méthodes)
- `DividendStatementMapper` (3 méthodes)
- `FormMapper` (3 méthodes)
- `TaxCalculationMapper` (2 méthodes)
- `TaxRuleMapper` (2 méthodes)

**Estimation**: ~150 lignes de tests

#### Priorité 2 - PDF Service (1% → 70%)

Tester :
- `PdfFormFiller.fillPdfForm()`
- `PdfFormFiller.listFormFields()`
- `Form5000FieldMapper.mapToFormFields()`
- `Form5001FieldMapper.mapToFormFields()`

**Estimation**: ~200 lignes de tests

#### Priorité 3 - Security (1% → 60%)

Tester :
- `InternalSecurityConfig` filter
- JWT token validation (si implémenté)

**Estimation**: ~100 lignes de tests

### Phase 3: Tests BFF

#### Priorité 1 - Services

- `DividendService` (compléter tests existants)
- `FormService`
- `AuthService`

#### Priorité 2 - Controllers

Tests WebFlux avec `WebTestClient` :
- `DividendController`
- `FormController`
- `AuthController`

#### Priorité 3 - Mappers

- `DividendMapper`
- Tests de conversion BFF ↔ Backend

**Estimation totale BFF**: ~500 lignes de tests

---

## 📋 Checklist Harmonisation Backend/BFF

### Architecture

- [ ] BFF suit la même structure que Backend (service/impl séparé)
- [ ] Mappers testés dans les deux projets
- [ ] Même convention de nommage
- [ ] Même structure de packages

### Tests

- [ ] Backend: 60% de couverture sur code métier
- [ ] BFF: 60% de couverture sur code métier
- [ ] Tests unitaires pour tous les mappers
- [ ] Tests integration pour tous les controllers
- [ ] Tests services avec mocks

### Configuration

- [ ] Jacoco configuré identiquement
- [ ] Exclusions alignées (code généré, config)
- [ ] Seuils de couverture cohérents
- [ ] Rapports générés dans target/site/jacoco

### Qualité Code

- [ ] Lombok utilisé de manière cohérente
- [ ] Logs structurés (Slf4j)
- [ ] Exception handling uniforme
- [ ] Documentation JavaDoc sur méthodes publiques

---

## 🚀 Prochaines Étapes

### Immédiat (Aujourd'hui)

1. **Fixer Jacoco Backend**
   - Ajouter exclusions
   - Passer le build

2. **Ajouter Jacoco BFF**
   - Configurer plugin
   - Premier rapport

3. **Revue DividendService BFF**
   - Vérifier le code que tu as écrit
   - Valider l'architecture
   - Proposer améliorations

### Court Terme (Cette Semaine)

4. **Tests Mappers Backend**
   - 5 classes de mappers
   - ~150 lignes de tests

5. **Tests PDF Service Backend**
   - 3 classes PDF
   - ~200 lignes de tests

6. **Tests BFF Services**
   - Compléter DividendService
   - Ajouter FormService tests

### Moyen Terme

7. **Tests Controllers BFF**
8. **Tests Security Backend + BFF**
9. **Documentation technique**

---

## 📌 Recommandations Professionnelles

### 1. Séparation Service/Impl

**Backend**: ✅ Bon (interface + impl)
**BFF**: ⚠️ Services directs

**Recommandation**: Garder BFF simple (pas besoin d'interface si 1 seule impl), mais documenter cette décision.

### 2. Gestion Erreurs

**À vérifier**:
- Exception handling global (@ControllerAdvice)
- Codes HTTP cohérents
- Messages d'erreur utilisables

### 3. Logging

**À standardiser**:
- Niveau de logs cohérent
- Format uniforme
- Pas de logs sensibles (tokens, passwords)

### 4. Sécurité

**À tester**:
- Headers X-User-Id validation
- CORS configuration
- Rate limiting (si implémenté)

---

## Résumé Exécutif

### Backend
- ✅ Architecture solide
- ✅ Tests présents (132)
- ❌ Couverture insuffisante (24% vs 60%)
- ⚠️ Jacoco mal configuré (fail le build)

### BFF
- ✅ Structure claire
- ❌ Quasi aucun test (1 fichier)
- ❌ Pas de Jacoco
- ⚠️ Pas aligné avec Backend

### Actions Critiques
1. **Fixer Jacoco Backend** (exclure code non testable)
2. **Ajouter Jacoco BFF** (même config)
3. **Tester mappers Backend** (priorité haute)
4. **Tester services BFF** (priorité haute)

---

**Temps estimé pour atteindre 60% de couverture**:
- Backend: ~2-3 jours (tests mappers + PDF + security)
- BFF: ~3-4 jours (tests complets services + controllers)
- **Total: ~1 semaine** de travail focalisé

---

**Prêt à commencer ?** On attaque par quoi en premier ?

1. Fix Jacoco Backend (30 min)
2. Revue DividendService BFF (1h)
3. Ajout Jacoco BFF (30 min)
4. Tests mappers Backend (2h)
