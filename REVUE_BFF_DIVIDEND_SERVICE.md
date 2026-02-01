# Revue Code: BFF DividendService.java

**Date**: 2026-02-01
**Fichier**: `bff-gateway/src/main/java/com/taxdividend/bff/service/DividendService.java`

---

## 📋 Résumé Exécutif

### ✅ Points Positifs
1. ✅ Flux orchestré correctement : Agent → Backend Statement → Backend Dividends
2. ✅ Utilisation de Reactive (Mono) cohérente
3. ✅ Logs informatifs
4. ✅ Nettoyage du tempFile dans `doFinally()`
5. ✅ Gestion du userId via SecurityContext

### ❌ Points à Corriger
1. ❌ **Import en double** de `DividendData` (lignes 7 et 11)
2. ❌ **Callback hell** - Imbrication excessive (7 niveaux)
3. ❌ **Gestion d'erreurs manquante** - Pas de `.onErrorResume()`
4. ❌ **Données manquantes** - broker, periodStart, periodEnd à null
5. ❌ **Calcul approximatif** - withholdingRate avec division/multiplication
6. ❌ **doFinally silencieux** - Erreur de suppression ignorée
7. ❌ **Mapping incomplet** - reclaimableAmount absent de la réponse
8. ❌ **Pas de validation** - Fichier vide, format invalide

### ⚠️ Points d'Attention
- Variable `tempFile` non final (risque NPE dans doFinally)
- Agent peut retourner liste vide → response vide OK ?
- Réutilisation du tempFile pour 2 appels (Agent + Backend)

---

## 🔍 Analyse Détaillée

### 1. Import en Double ❌

```java
// Ligne 7
import com.taxdividend.bff.model.DividendData;

// Ligne 11
import com.taxdividend.bff.model.DividendData;
```

**Impact**: Compilation warning
**Fix**: Supprimer une des deux lignes

---

### 2. Callback Hell ❌

**Problème**: 7 niveaux d'imbrication

```java
Mono.fromCallable(...)
  .flatMap(tempFile -> filePart.transferTo(...)
    .then(Mono.defer(() ->
      parsingApi.parseDocument(...)
        .flatMap(agentResponse ->
          ReactiveSecurityContextHolder.getContext()
            .map(ctx -> ...)
            .flatMap(userId ->
              dividendStatementsApi.uploadDividendStatement(...)
                .flatMap(statement ->
                  dividendsApi.bulkImportDividends(...)
                    .map(importResult -> ...)  // Niveau 7 !
```

**Impact**:
- Difficile à lire
- Difficile à tester
- Gestion d'erreurs complexe
- Maintenance difficile

**Solution**: Extraire en méthodes privées

---

### 3. Gestion d'Erreurs Manquante ❌

**Problème**: Aucun `.onErrorResume()` ou `.doOnError()`

**Scénarios non gérés**:
- Agent ne répond pas (timeout)
- Backend unavailable (503)
- Fichier corrompu
- Statement déjà existe
- Bulk import échoue partiellement

**Conséquence**: Erreur 500 générique au client

**Solution Recommandée**:

```java
.onErrorResume(AgentException.class, e -> {
    log.error("Agent parsing failed", e);
    return Mono.error(new ParseException("Failed to parse PDF", e));
})
.onErrorResume(BackendException.class, e -> {
    log.error("Backend storage failed", e);
    return Mono.error(new StorageException("Failed to store statement", e));
})
```

---

### 4. Données Manquantes ❌

```java
broker = "Unknown"; // Default
periodStart = null; // ❌
periodEnd = null;   // ❌
```

**Problème**: Backend peut refuser un statement sans dates

**Solutions possibles**:

1. **Extraire du filename**: `statement_2024_Q1.pdf`
2. **Déduire des dividends**: min(paymentDate) → max(paymentDate)
3. **Demander à l'Agent** de retourner ces infos
4. **Accepter null côté backend** (statement "brouillon")

**Recommandation**: Option 2 (déduire des dividends)

```java
LocalDate periodStart = agentResponse.getData().stream()
    .map(DividendData::getPaymentDate)
    .min(Comparator.naturalOrder())
    .orElse(null);

LocalDate periodEnd = agentResponse.getData().stream()
    .map(DividendData::getPaymentDate)
    .max(Comparator.naturalOrder())
    .orElse(null);
```

---

### 5. Calcul Approximatif ❌

```java
item.setWithholdingRate(
    d.getWithholdingTax()
        .divide(d.getGrossAmount(), 4, RoundingMode.HALF_UP)
        .multiply(new BigDecimal(100))  // approx
);
```

**Problème**: Pourquoi recalculer si Agent devrait le fournir ?

**Questions**:
- L'Agent retourne-t-il `withholdingRate` ?
- Si non, pourquoi pas ?
- Backend recalcule-t-il ou utilise cette valeur ?

**Recommandation**:
- Agent devrait retourner `withholdingRate` directement
- Ou laisser Backend calculer (il le fait déjà dans TaxCalculationService)

---

### 6. doFinally Silencieux ⚠️

```java
.doFinally(signalType -> {
    if (tempFile.exists()) {
        tempFile.delete();  // ❌ Résultat ignoré
    }
})
```

**Problèmes**:
1. `tempFile` peut être null (si erreur avant création)
2. `delete()` peut échouer (permissions, lock)
3. Erreurs silencieuses

**Solution**:

```java
.doFinally(signalType -> {
    if (tempFile != null && tempFile.exists()) {
        boolean deleted = tempFile.delete();
        if (!deleted) {
            log.warn("Failed to delete temp file: {}", tempFile.getAbsolutePath());
        }
    }
})
```

---

### 7. Mapping Incomplet ❌

**Problème**: `reclaimableAmount` absent de la réponse

```java
DividendData dd = new DividendData();
dd.setSecurityName(i.getSecurityName());
dd.setIsin(i.getIsin());
dd.setGrossAmount(i.getGrossAmount());
dd.setCurrency(i.getCurrency());
dd.setPaymentDate(i.getPaymentDate());
dd.setWithholdingTax(i.getWithholdingTax());
dd.setSourceCountry(i.getSourceCountry());
// ❌ dd.setReclaimableAmount(...) manquant !
```

**Impact**: Frontend ne peut pas afficher le montant récupérable

**Solution**: Récupérer depuis `importResult.getTotalReclaimable()` ou depuis Backend

---

### 8. Pas de Validation ❌

**Validations manquantes**:
- Taille fichier (limite 10MB définie mais non utilisée)
- Format fichier (PDF uniquement ?)
- Nom fichier vide
- Agent retourne 0 dividends → Continuer quand même ?

**Solution**:

```java
if (filePart.filename() == null || filePart.filename().isEmpty()) {
    return Mono.error(new IllegalArgumentException("Filename is required"));
}

if (!filePart.filename().toLowerCase().endsWith(".pdf")) {
    return Mono.error(new IllegalArgumentException("Only PDF files are supported"));
}

// Dans la réponse de l'Agent
if (agentResponse.getData() == null || agentResponse.getData().isEmpty()) {
    return Mono.error(new ParseException("No dividends found in PDF"));
}
```

---

## 🎯 Code Refactoré Proposé

### Structure Recommandée

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class DividendService {

    // ... dépendances

    public Mono<ParseStatementResponse> parseDividendStatement(Part file) {
        log.info("Parsing dividend statement");

        return validateFile(file)
            .flatMap(this::createTempFile)
            .flatMap(tempFile -> processStatement(tempFile, (FilePart) file)
                .doFinally(signal -> cleanupTempFile(tempFile))
            );
    }

    private Mono<FilePart> validateFile(Part file) {
        if (!(file instanceof FilePart)) {
            return Mono.error(new IllegalArgumentException("Uploaded part is not a file"));
        }
        FilePart filePart = (FilePart) file;

        if (filePart.filename() == null || filePart.filename().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Filename is required"));
        }

        if (!filePart.filename().toLowerCase().endsWith(".pdf")) {
            return Mono.error(new IllegalArgumentException("Only PDF files are supported"));
        }

        return Mono.just(filePart);
    }

    private Mono<File> createTempFile(FilePart filePart) {
        return Mono.fromCallable(() ->
            File.createTempFile("upload_", "_" + filePart.filename())
        );
    }

    private Mono<ParseStatementResponse> processStatement(File tempFile, FilePart filePart) {
        return filePart.transferTo(tempFile.toPath())
            .then(parseWithAgent(tempFile))
            .flatMap(agentResponse -> createStatementInBackend(tempFile, agentResponse))
            .flatMap(this::importDividendsToBackend)
            .map(this::buildResponse)
            .onErrorResume(this::handleError);
    }

    private Mono<AgentParseResponse> parseWithAgent(File tempFile) {
        return parsingApi.parseDocument(tempFile, null)
            .doOnSuccess(response -> log.debug("Agent parsed {} dividends",
                response.getData() != null ? response.getData().size() : 0))
            .onErrorResume(e -> {
                log.error("Agent parsing failed", e);
                return Mono.error(new ParseException("Failed to parse PDF", e));
            });
    }

    private Mono<StatementWithDividends> createStatementInBackend(File tempFile, AgentParseResponse agentResponse) {
        return getUserId()
            .flatMap(userId -> {
                // Déduire les dates des dividends
                LocalDate periodStart = extractPeriodStart(agentResponse);
                LocalDate periodEnd = extractPeriodEnd(agentResponse);
                String broker = extractBroker(agentResponse); // ou "Unknown"

                return dividendStatementsApi.uploadDividendStatement(
                    userId, broker, periodStart, periodEnd, tempFile
                ).map(statement -> new StatementWithDividends(statement, agentResponse, userId));
            })
            .onErrorResume(e -> {
                log.error("Failed to create statement in backend", e);
                return Mono.error(new StorageException("Failed to store statement", e));
            });
    }

    private Mono<ImportResult> importDividendsToBackend(StatementWithDividends data) {
        if (data.agentResponse.getData() == null || data.agentResponse.getData().isEmpty()) {
            log.warn("No dividends to import for statement {}", data.statement.getId());
            return Mono.just(new ImportResult(data, null));
        }

        BulkImportDividendsRequest bulkRequest = buildBulkImportRequest(data);

        return dividendsApi.bulkImportDividends(data.userId, bulkRequest)
            .map(importResponse -> new ImportResult(data, importResponse))
            .onErrorResume(e -> {
                log.error("Failed to import dividends for statement {}", data.statement.getId(), e);
                return Mono.error(new ImportException("Failed to import dividends", e));
            });
    }

    private ParseStatementResponse buildResponse(ImportResult result) {
        ParseStatementResponse response = new ParseStatementResponse();

        if (result.importResponse != null) {
            List<DividendData> dividends = result.data.agentResponse.getData().stream()
                .map(this::mapToDividendData)
                .collect(Collectors.toList());
            response.setDividends(dividends);
        }

        return response;
    }

    private Mono<ParseStatementResponse> handleError(Throwable e) {
        if (e instanceof ParseException || e instanceof StorageException || e instanceof ImportException) {
            return Mono.error(e); // Already wrapped
        }
        log.error("Unexpected error during statement processing", e);
        return Mono.error(new RuntimeException("Failed to process statement", e));
    }

    private void cleanupTempFile(File tempFile) {
        if (tempFile != null && tempFile.exists()) {
            boolean deleted = tempFile.delete();
            if (!deleted) {
                log.warn("Failed to delete temp file: {}", tempFile.getAbsolutePath());
            }
        }
    }

    private Mono<UUID> getUserId() {
        return ReactiveSecurityContextHolder.getContext()
            .map(ctx -> UUID.fromString(ctx.getAuthentication().getName()))
            .switchIfEmpty(Mono.error(new SecurityException("User not authenticated")));
    }

    private LocalDate extractPeriodStart(AgentParseResponse response) {
        if (response.getData() == null || response.getData().isEmpty()) {
            return null;
        }
        return response.getData().stream()
            .map(d -> d.getPaymentDate())
            .filter(Objects::nonNull)
            .min(Comparator.naturalOrder())
            .orElse(null);
    }

    private LocalDate extractPeriodEnd(AgentParseResponse response) {
        if (response.getData() == null || response.getData().isEmpty()) {
            return null;
        }
        return response.getData().stream()
            .map(d -> d.getPaymentDate())
            .filter(Objects::nonNull)
            .max(Comparator.naturalOrder())
            .orElse(null);
    }

    private String extractBroker(AgentParseResponse response) {
        // TODO: Agent devrait retourner le broker
        return "Unknown";
    }

    private BulkImportDividendsRequest buildBulkImportRequest(StatementWithDividends data) {
        BulkImportDividendsRequest request = new BulkImportDividendsRequest();
        request.setStatementId(data.statement.getId());

        List<BulkImportDividendItem> items = data.agentResponse.getData().stream()
            .map(this::mapToBulkImportItem)
            .collect(Collectors.toList());

        request.setDividends(items);
        return request;
    }

    private BulkImportDividendItem mapToBulkImportItem(AgentDividendData d) {
        BulkImportDividendItem item = new BulkImportDividendItem();
        item.setSecurityName(d.getSecurityName());
        item.setIsin(d.getIsin());
        item.setGrossAmount(d.getGrossAmount());
        item.setCurrency(d.getCurrency());
        item.setPaymentDate(d.getPaymentDate());
        item.setWithholdingTax(d.getWithholdingTax());
        item.setWithholdingRate(d.getWithholdingRate()); // Agent devrait fournir
        item.setSourceCountry(d.getCountry());
        return item;
    }

    private DividendData mapToDividendData(AgentDividendData d) {
        DividendData dd = new DividendData();
        dd.setSecurityName(d.getSecurityName());
        dd.setIsin(d.getIsin());
        dd.setGrossAmount(d.getGrossAmount());
        dd.setCurrency(d.getCurrency());
        dd.setPaymentDate(d.getPaymentDate());
        dd.setWithholdingTax(d.getWithholdingTax());
        dd.setSourceCountry(d.getCountry());
        // dd.setReclaimableAmount(...); // À récupérer du backend
        return dd;
    }

    // Classes internes pour pipeline
    private record StatementWithDividends(
        com.taxdividend.bff.client.model.DividendStatement statement,
        AgentParseResponse agentResponse,
        UUID userId
    ) {}

    private record ImportResult(
        StatementWithDividends data,
        BulkImportDividendsResponse importResponse
    ) {}
}
```

---

## 📊 Comparaison Avant/Après

| Aspect | Avant | Après |
|--------|-------|-------|
| **Lignes de code** | 184 | ~250 (avec méthodes privées) |
| **Niveaux imbrication** | 7 | 2-3 max |
| **Lisibilité** | ⚠️ Difficile | ✅ Claire |
| **Testabilité** | ❌ Difficile | ✅ Facile (méthodes privées testables) |
| **Gestion erreurs** | ❌ Absente | ✅ Complète |
| **Validation** | ❌ Absente | ✅ Présente |
| **Logging** | ⚠️ Minimal | ✅ Complet |

---

## 🎯 Plan d'Action Recommandé

### Priorité 1 - Corrections Critiques (Aujourd'hui)
1. ✅ Supprimer import en double
2. ✅ Ajouter validation fichier
3. ✅ Améliorer gestion erreurs (onErrorResume)
4. ✅ Extraire broker et dates des dividends

### Priorité 2 - Refactoring (Cette Semaine)
5. ✅ Extraire méthodes privées (réduire callback hell)
6. ✅ Ajouter records pour pipeline
7. ✅ Améliorer logging
8. ✅ Compléter mapping (reclaimableAmount)

### Priorité 3 - Contrat Agent (Coordination)
9. ⚠️ Agent devrait retourner `withholdingRate`
10. ⚠️ Agent devrait retourner `broker`
11. ⚠️ Clarifier format réponse Agent (1 statement avec N dividends ?)

### Priorité 4 - Tests (Urgent)
12. ✅ Tests unitaires pour chaque méthode privée
13. ✅ Tests d'intégration avec Agent mocké
14. ✅ Tests d'intégration avec Backend mocké
15. ✅ Tests de gestion d'erreurs

---

## 📝 Checklist Qualité Pro

### Code
- [ ] Pas d'imports en double
- [ ] Pas de callback hell (max 3 niveaux)
- [ ] Gestion erreurs complète
- [ ] Validation des inputs
- [ ] Logging approprié
- [ ] Pas de variables mutables partagées
- [ ] Méthodes < 30 lignes
- [ ] Noms explicites

### Tests
- [ ] Couverture > 80%
- [ ] Tests unitaires (méthodes privées)
- [ ] Tests integration (flux complet)
- [ ] Tests erreurs (timeouts, 5xx, etc.)
- [ ] Tests validation

### Documentation
- [ ] JavaDoc sur méthodes publiques
- [ ] Commentaires sur logique complexe
- [ ] README avec exemples
- [ ] Diagrammes de séquence (si nécessaire)

---

## Verdict Final

**Code Actuel**: ⚠️ Fonctionne mais nécessite refactoring

**Recommandation**:
1. **Aujourd'hui**: Fix critiques (imports, validation, erreurs)
2. **Cette semaine**: Refactoring complet avec le code proposé
3. **Tests**: Priorité absolue après refactoring

**Temps estimé**:
- Fixes critiques: 1h
- Refactoring: 3h
- Tests: 4h
- **Total: ~1 journée**

Prêt à commencer ? Par quoi on attaque en premier ?
