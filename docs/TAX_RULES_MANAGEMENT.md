# 🏛️ GESTION DES RÈGLES FISCALES

**Date**: 27 Janvier 2026
**Objectif**: Architecture pour règles fiscales évolutives et maintenables

---

## 🎯 PROBLÉMATIQUE

Les règles fiscales changent régulièrement :
- **Taux**: PFU passe de 21% → 12.8% en 2018
- **Conventions**: Renégociations bilatérales
- **Lois**: Loi de finances annuelle
- **Cas spéciaux**: Nouvelles enveloppes fiscales

**Contraintes**:
- ❌ Pas de redéploiement à chaque changement
- ✅ Versioning (historique pour audit)
- ✅ Validation avant application
- ✅ Rollback rapide si erreur
- ✅ A/B testing pour nouvelles règles

---

## 🏗️ ARCHITECTURE RECOMMANDÉE

### Option 1: Base de Données (Recommandé) ⭐

**Principe**: Stocker les règles fiscales dans PostgreSQL avec versioning temporel

#### **Schema Database**

```sql
-- Table principale des règles
CREATE TABLE tax_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    country_code VARCHAR(2) NOT NULL,           -- 'FR', 'DE', 'IT'
    residence_country VARCHAR(2) NOT NULL,       -- 'CH' (Suisse)
    rule_type VARCHAR(50) NOT NULL,              -- 'WITHHOLDING_RATE', 'TREATY_RATE'
    account_type VARCHAR(20),                    -- 'CTO', 'PEA', NULL (all)
    tax_option VARCHAR(20),                      -- 'PFU', 'PROGRESSIVE', NULL
    rate DECIMAL(5,4) NOT NULL,                  -- 0.1280, 0.2500, 0.1500
    effective_from DATE NOT NULL,                -- Date d'entrée en vigueur
    effective_until DATE,                        -- NULL = toujours valide
    priority INTEGER DEFAULT 0,                  -- Pour résoudre conflits
    created_at TIMESTAMP DEFAULT NOW(),
    created_by VARCHAR(255),                     -- Utilisateur/système
    source_reference TEXT,                       -- URL BOI, article loi
    notes TEXT,
    is_active BOOLEAN DEFAULT true,
    version INTEGER DEFAULT 1
);

-- Historique des changements
CREATE TABLE tax_rules_audit (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rule_id UUID REFERENCES tax_rules(id),
    action VARCHAR(20) NOT NULL,                 -- 'CREATE', 'UPDATE', 'DELETE'
    old_value JSONB,
    new_value JSONB,
    changed_at TIMESTAMP DEFAULT NOW(),
    changed_by VARCHAR(255),
    reason TEXT
);

-- Index pour performance
CREATE INDEX idx_tax_rules_lookup ON tax_rules(
    country_code,
    residence_country,
    rule_type,
    effective_from
) WHERE is_active = true;

CREATE INDEX idx_tax_rules_dates ON tax_rules(effective_from, effective_until);
```

#### **Données Initiales**

```sql
-- France → Suisse: Taux conventionnel
INSERT INTO tax_rules (
    country_code,
    residence_country,
    rule_type,
    rate,
    effective_from,
    source_reference,
    notes
) VALUES (
    'FR',
    'CH',
    'TREATY_RATE',
    0.1500,
    '1967-09-09',  -- Date convention France-Suisse
    'https://www.impots.gouv.fr/conventions-internationales',
    'Convention fiscale France-Suisse, article 10 (dividendes)'
);

-- France: PFU (Flat Tax) depuis 2018
INSERT INTO tax_rules (
    country_code,
    residence_country,
    rule_type,
    account_type,
    tax_option,
    rate,
    effective_from,
    source_reference
) VALUES (
    'FR',
    'CH',
    'WITHHOLDING_RATE',
    'CTO',
    'PFU',
    0.1280,
    '2018-01-01',
    'https://bofip.impots.gouv.fr/bofip/2815-PGP.html'
);

-- France: Barème progressif (historique et actuel)
INSERT INTO tax_rules (
    country_code,
    residence_country,
    rule_type,
    account_type,
    tax_option,
    rate,
    effective_from,
    effective_until,
    source_reference
) VALUES (
    'FR',
    'CH',
    'WITHHOLDING_RATE',
    'CTO',
    'PROGRESSIVE',
    0.2500,
    '2013-01-01',
    NULL,  -- Toujours valide
    'https://bofip.impots.gouv.fr/'
);

-- PEA: Aucune retenue
INSERT INTO tax_rules (
    country_code,
    residence_country,
    rule_type,
    account_type,
    rate,
    effective_from,
    notes
) VALUES (
    'FR',
    'CH',
    'WITHHOLDING_RATE',
    'PEA',
    0.0000,
    '1992-09-13',  -- Création PEA
    'PEA: Pas de retenue à la source sur dividendes'
);
```

#### **Service Backend**

```java
// backend/src/main/java/com/taxdividend/backend/service/TaxRulesService.java

@Service
public class TaxRulesService {

    private final TaxRulesRepository taxRulesRepository;

    @Cacheable(value = "taxRules", key = "#lookup.cacheKey()")
    public TaxRule getApplicableRule(TaxRuleLookup lookup) {
        List<TaxRule> rules = taxRulesRepository.findApplicableRules(
            lookup.getCountryCode(),
            lookup.getResidenceCountry(),
            lookup.getRuleType(),
            lookup.getPaymentDate(),
            lookup.getAccountType(),
            lookup.getTaxOption()
        );

        if (rules.isEmpty()) {
            throw new TaxRuleNotFoundException(
                "No tax rule found for: " + lookup
            );
        }

        // Tri par priorité et spécificité
        return rules.stream()
            .sorted(Comparator
                .comparing(TaxRule::getPriority).reversed()
                .thenComparing(r -> r.getAccountType() != null ? 1 : 0)
                .thenComparing(r -> r.getTaxOption() != null ? 1 : 0)
            )
            .findFirst()
            .orElseThrow();
    }

    public BigDecimal calculateReclaimableAmount(DividendCalculationRequest request) {
        // 1. Get withholding rate
        TaxRule withholdingRule = getApplicableRule(
            TaxRuleLookup.builder()
                .countryCode(request.getCountryCode())
                .residenceCountry(request.getResidenceCountry())
                .ruleType("WITHHOLDING_RATE")
                .paymentDate(request.getPaymentDate())
                .accountType(request.getAccountType())
                .taxOption(request.getTaxOption())
                .build()
        );

        // 2. Get treaty rate
        TaxRule treatyRule = getApplicableRule(
            TaxRuleLookup.builder()
                .countryCode(request.getCountryCode())
                .residenceCountry(request.getResidenceCountry())
                .ruleType("TREATY_RATE")
                .paymentDate(request.getPaymentDate())
                .build()
        );

        // 3. Calculate
        BigDecimal withheld = request.getGrossAmount()
            .multiply(withholdingRule.getRate())
            .setScale(2, RoundingMode.HALF_UP);

        BigDecimal treaty = request.getGrossAmount()
            .multiply(treatyRule.getRate())
            .setScale(2, RoundingMode.HALF_UP);

        BigDecimal reclaimable = withheld.subtract(treaty)
            .max(BigDecimal.ZERO);

        // 4. Audit log
        auditService.logCalculation(
            request,
            withholdingRule,
            treatyRule,
            reclaimable
        );

        return reclaimable;
    }
}
```

#### **Repository Custom Query**

```java
// backend/src/main/java/com/taxdividend/backend/repository/TaxRulesRepository.java

@Repository
public interface TaxRulesRepository extends JpaRepository<TaxRule, UUID> {

    @Query("""
        SELECT tr FROM TaxRule tr
        WHERE tr.countryCode = :countryCode
          AND tr.residenceCountry = :residenceCountry
          AND tr.ruleType = :ruleType
          AND tr.effectiveFrom <= :paymentDate
          AND (tr.effectiveUntil IS NULL OR tr.effectiveUntil > :paymentDate)
          AND tr.isActive = true
          AND (
              (tr.accountType IS NULL OR tr.accountType = :accountType)
              AND (tr.taxOption IS NULL OR tr.taxOption = :taxOption)
          )
        ORDER BY tr.priority DESC,
                 CASE WHEN tr.accountType IS NOT NULL THEN 1 ELSE 0 END DESC,
                 CASE WHEN tr.taxOption IS NOT NULL THEN 1 ELSE 0 END DESC
    """)
    List<TaxRule> findApplicableRules(
        @Param("countryCode") String countryCode,
        @Param("residenceCountry") String residenceCountry,
        @Param("ruleType") String ruleType,
        @Param("paymentDate") LocalDate paymentDate,
        @Param("accountType") String accountType,
        @Param("taxOption") String taxOption
    );
}
```

#### **Admin API pour Gestion**

```java
// backend/src/main/java/com/taxdividend/backend/controller/admin/TaxRulesAdminController.java

@RestController
@RequestMapping("/admin/tax-rules")
@PreAuthorize("hasRole('ADMIN')")
public class TaxRulesAdminController {

    private final TaxRulesService taxRulesService;
    private final TaxRulesAuditService auditService;

    @PostMapping
    public ResponseEntity<TaxRule> createRule(
            @Valid @RequestBody CreateTaxRuleRequest request,
            @AuthenticationPrincipal UserDetails admin
    ) {
        // Validation
        validateRule(request);

        // Create
        TaxRule rule = taxRulesService.createRule(request);

        // Audit
        auditService.logRuleCreation(rule, admin.getUsername());

        // Clear cache
        cacheManager.getCache("taxRules").clear();

        return ResponseEntity.ok(rule);
    }

    @PutMapping("/{ruleId}")
    public ResponseEntity<TaxRule> updateRule(
            @PathVariable UUID ruleId,
            @Valid @RequestBody UpdateTaxRuleRequest request,
            @AuthenticationPrincipal UserDetails admin
    ) {
        TaxRule oldRule = taxRulesService.getRule(ruleId);
        TaxRule updatedRule = taxRulesService.updateRule(ruleId, request);

        // Audit (old vs new)
        auditService.logRuleUpdate(oldRule, updatedRule, admin.getUsername());

        cacheManager.getCache("taxRules").clear();

        return ResponseEntity.ok(updatedRule);
    }

    @DeleteMapping("/{ruleId}")
    public ResponseEntity<Void> deleteRule(
            @PathVariable UUID ruleId,
            @RequestParam String reason,
            @AuthenticationPrincipal UserDetails admin
    ) {
        TaxRule rule = taxRulesService.getRule(ruleId);

        // Soft delete (is_active = false)
        taxRulesService.deactivateRule(ruleId);

        auditService.logRuleDeletion(rule, reason, admin.getUsername());

        cacheManager.getCache("taxRules").clear();

        return ResponseEntity.noContent().build();
    }

    // Preview: Test rule without saving
    @PostMapping("/preview")
    public ResponseEntity<PreviewResult> previewRule(
            @Valid @RequestBody TaxRulePreviewRequest request
    ) {
        // Test avec 10 cas réels
        List<DividendCase> testCases = testDataService.getTestCases();

        List<CalculationComparison> results = testCases.stream()
            .map(testCase -> {
                BigDecimal currentResult = taxRulesService.calculate(testCase);
                BigDecimal newResult = taxRulesService.calculateWithRule(testCase, request.getRule());
                return new CalculationComparison(testCase, currentResult, newResult);
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(new PreviewResult(results));
    }
}
```

---

## 🔄 PROCESSUS DE MISE À JOUR

### 1. Veille Réglementaire

**Déclencheurs**:
- Loi de finances (décembre chaque année)
- Conventions bilatérales
- Bulletins officiels (BOI)
- Circulaires administration fiscale

**Responsable**: Product Owner / Expert Fiscal

### 2. Validation

**Étapes**:
1. **Analyse**: Expert fiscal analyse le changement
2. **Documentation**: Mise à jour `docs/TAX_RULES.md`
3. **Source**: Lien officiel (impots.gouv.fr, BOI)
4. **Impact**: Estimation nombre d'utilisateurs affectés

### 3. Implémentation

**Via Admin Panel**:

```
┌─────────────────────────────────────────────────┐
│  TAX DIVIDEND AI - Admin Panel                  │
├─────────────────────────────────────────────────┤
│                                                  │
│  Create New Tax Rule                            │
│                                                  │
│  Source Country:     [FR ▼]                     │
│  Residence Country:  [CH ▼]                     │
│  Rule Type:          [WITHHOLDING_RATE ▼]       │
│  Account Type:       [CTO ▼] (optional)         │
│  Tax Option:         [PFU ▼] (optional)         │
│  Rate:               [0.1280]                    │
│  Effective From:     [2025-01-01]               │
│  Effective Until:    [        ] (optional)      │
│  Source Reference:   [https://bofip...]         │
│  Notes:              [PFU rate change...]       │
│                                                  │
│  [Preview with Test Cases]  [Save]  [Cancel]    │
└─────────────────────────────────────────────────┘
```

### 4. Test & Preview

**Avant activation**:
```bash
POST /admin/tax-rules/preview
{
  "rule": {
    "countryCode": "FR",
    "ruleType": "WITHHOLDING_RATE",
    "rate": 0.13,  // Nouveau taux hypothétique
    "effectiveFrom": "2025-01-01"
  }
}

Response:
{
  "testCases": [
    {
      "dividend": { "grossAmount": 1000, "accountType": "CTO" },
      "currentCalculation": { "reclaimable": 100 },
      "newCalculation": { "reclaimable": 105 },
      "difference": 5
    },
    // ... 9 autres cas
  ],
  "summary": {
    "totalAffected": 1234,
    "averageImpact": 5.23
  }
}
```

### 5. Activation

**Stratégies**:

#### **A. Immédiate** (règles rétroactives)
```java
TaxRule rule = new TaxRule();
rule.setEffectiveFrom(LocalDate.parse("2025-01-01"));
taxRulesService.createRule(rule);
// Immédiatement actif pour tous les calculs
```

#### **B. Planifiée** (future)
```java
TaxRule rule = new TaxRule();
rule.setEffectiveFrom(LocalDate.parse("2026-01-01")); // Future
taxRulesService.createRule(rule);
// Sera appliqué automatiquement à partir du 01/01/2026
```

#### **C. Feature Flag** (A/B testing)
```java
@Service
public class TaxRulesService {

    public BigDecimal calculate(Request req) {
        if (featureFlagService.isEnabled("new-pfu-rate", req.getUserId())) {
            return calculateWithNewRule(req);
        } else {
            return calculateWithOldRule(req);
        }
    }
}
```

### 6. Monitoring

**Après activation**:

```java
// Alertes si comportement anormal
@Scheduled(fixedRate = 3600000) // 1h
public void monitorCalculations() {
    List<CalculationAudit> recent = auditRepository.findLast1000();

    // Statistiques
    double avgReclaimable = recent.stream()
        .mapToDouble(a -> a.getReclaimableAmount().doubleValue())
        .average()
        .orElse(0);

    // Alerte si déviation > 20%
    if (Math.abs(avgReclaimable - historicalAvg) > historicalAvg * 0.2) {
        alertService.send(
            "ANOMALY_DETECTED",
            "Average reclaimable amount deviated significantly"
        );
    }
}
```

### 7. Rollback

**En cas d'erreur**:

```sql
-- Option 1: Soft delete
UPDATE tax_rules
SET is_active = false
WHERE id = 'rule-id';

-- Option 2: Modifier date de fin
UPDATE tax_rules
SET effective_until = NOW()
WHERE id = 'rule-id';

-- Option 3: Restaurer version précédente
INSERT INTO tax_rules (...)
SELECT old_value::jsonb
FROM tax_rules_audit
WHERE rule_id = 'rule-id'
  AND action = 'UPDATE'
ORDER BY changed_at DESC
LIMIT 1;
```

**Automatisé**:
```java
@Transactional
public void rollbackRule(UUID ruleId, String reason) {
    TaxRule rule = taxRulesRepository.findById(ruleId)
        .orElseThrow();

    // Get previous version from audit
    TaxRuleAudit lastAudit = auditRepository
        .findLatestByRuleId(ruleId)
        .orElseThrow();

    // Restore old values
    TaxRule restoredRule = objectMapper.convertValue(
        lastAudit.getOldValue(),
        TaxRule.class
    );

    taxRulesRepository.save(restoredRule);

    // Log rollback
    auditService.logRollback(rule, restoredRule, reason);

    // Clear cache
    cacheManager.getCache("taxRules").clear();

    // Notify
    notificationService.notifyAdmins(
        "RULE_ROLLBACK",
        "Tax rule " + ruleId + " rolled back: " + reason
    );
}
```

---

## 📊 DASHBOARD ADMIN

### Metrics à Afficher

```typescript
// Dashboard metrics
{
  "activeRules": 15,
  "scheduledRules": 2,  // Future effective_from
  "recentChanges": [
    {
      "date": "2026-01-15",
      "action": "UPDATE",
      "rule": "FR-CH-PFU",
      "impact": "1234 users affected"
    }
  ],
  "calculationsToday": 5432,
  "averageReclaimable": 125.50,
  "anomalies": []
}
```

---

## 🔐 SÉCURITÉ

### Contrôle d'Accès

```java
@PreAuthorize("hasRole('TAX_ADMIN')")
public class TaxRulesAdminController {
    // Seuls les TAX_ADMIN peuvent modifier
}

// Audit automatique
@Audited
@Entity
public class TaxRule {
    // Tous les changements sont logged
}
```

### Validation

```java
@Component
public class TaxRuleValidator {

    public void validate(TaxRule rule) {
        // 1. Rate doit être entre 0 et 1
        if (rule.getRate().compareTo(BigDecimal.ZERO) < 0 ||
            rule.getRate().compareTo(BigDecimal.ONE) > 0) {
            throw new ValidationException("Rate must be between 0 and 1");
        }

        // 2. Dates cohérentes
        if (rule.getEffectiveUntil() != null &&
            rule.getEffectiveUntil().isBefore(rule.getEffectiveFrom())) {
            throw new ValidationException("effective_until must be after effective_from");
        }

        // 3. Pas de conflit avec règles existantes
        List<TaxRule> overlapping = taxRulesRepository.findOverlapping(rule);
        if (!overlapping.isEmpty()) {
            throw new ValidationException("Rule conflicts with existing: " + overlapping);
        }

        // 4. Source reference obligatoire
        if (rule.getSourceReference() == null || rule.getSourceReference().isBlank()) {
            throw new ValidationException("Source reference is required");
        }
    }
}
```

---

## 📈 ALTERNATIVES

### Option 2: Fichiers de Configuration (YAML)

**Structure**:
```yaml
# config/tax-rules.yml
rules:
  - id: fr-ch-treaty
    source: FR
    residence: CH
    type: TREATY_RATE
    rate: 0.15
    effectiveFrom: 1967-09-09

  - id: fr-pfu-2018
    source: FR
    residence: CH
    type: WITHHOLDING_RATE
    accountType: CTO
    taxOption: PFU
    rate: 0.128
    effectiveFrom: 2018-01-01
```

**Avantages**:
- ✅ Simple
- ✅ Versionné Git
- ✅ Review via PR

**Inconvénients**:
- ❌ Redéploiement requis
- ❌ Pas d'historique automatique
- ❌ Pas de preview en prod

### Option 3: Service Externe (API)

**Principe**: Appeler API tierce pour règles fiscales

```java
@Service
public class ExternalTaxRulesService {

    public BigDecimal getRate(String country, LocalDate date) {
        return restTemplate.getForObject(
            "https://tax-api.example.com/rates/{country}/{date}",
            BigDecimal.class,
            country,
            date
        );
    }
}
```

**Avantages**:
- ✅ Données toujours à jour
- ✅ Aucune maintenance

**Inconvénients**:
- ❌ Dépendance externe
- ❌ Coût
- ❌ Latency

---

## 🎯 RECOMMANDATION

### Solution Hybride (Recommandée) ⭐

1. **Base de données** pour règles actives (production)
2. **Fichiers YAML** pour règles initiales (seed data)
3. **Admin Panel** pour modifications manuelles
4. **Feature flags** pour A/B testing
5. **Audit automatique** pour compliance

**Migration**:
```bash
# Phase 1: YAML → DB (initial seed)
./mvnw flyway:migrate  # Load from resources/db/data/tax-rules.sql

# Phase 2: Admin panel (updates)
POST /admin/tax-rules  # Via UI

# Phase 3: Monitoring
GET /admin/tax-rules/audit  # Review history
```

---

## 📝 CHECKLIST MISE EN PRODUCTION

- [ ] Table `tax_rules` créée
- [ ] Seeds initiaux (France-Suisse)
- [ ] Service `TaxRulesService` avec cache
- [ ] Admin API protégée (ROLE_ADMIN)
- [ ] Dashboard admin (React)
- [ ] Preview system (test cases)
- [ ] Audit logging
- [ ] Monitoring alertes
- [ ] Documentation complète
- [ ] Formation équipe support

---

**Dernière mise à jour**: 27 Janvier 2026
