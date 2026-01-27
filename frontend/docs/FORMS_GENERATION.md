# Génération des Formulaires 5000/5001

⚠️ **IMPORTANT : Mock temporaire** - L'implémentation actuelle génère un **HTML simplifié** pour démonstration. Les vrais formulaires officiels PDF doivent être utilisés pour la production. Voir [OFFICIAL_FORMS_TODO.md](./OFFICIAL_FORMS_TODO.md) pour le plan d'implémentation.

## Vue d'ensemble

La fonctionnalité de génération de formulaires permet aux utilisateurs de créer automatiquement les formulaires fiscaux 5000 et 5001 nécessaires pour récupérer l'excédent d'impôt prélevé à la source selon la convention fiscale France-Suisse.

**État actuel:** Mock HTML pour validation du workflow
**Objectif:** Remplissage automatique des PDF officiels (stockés dans `/docs/officials/fr/`)

## Architecture

### Frontend (Implémenté)

```
components/forms/
├── FormGeneratorView.tsx      # Vue principale avec formulaire
├── FormDataSummary.tsx        # Résumé des dividendes agrégés
└── FormPreview.tsx            # Prévisualisation PDF

hooks/
└── useFormGeneration.ts       # Hook React Query (mock)

types/
├── form.types.ts              # Types pour les formulaires
└── dividend.types.ts          # Types pour les dividendes
```

### Workflow

1. **Agrégation** : L'utilisateur clique sur "Générer Formulaires 5000/5001" après un scan
2. **Saisie** : Formulaire avec informations personnelles (adresse, année fiscale)
3. **Résumé** : Affichage des données agrégées et calculs
4. **Génération** : Création du PDF (actuellement mock HTML)
5. **Prévisualisation** : Affichage dans iframe
6. **Téléchargement** : Export du PDF final

## Données agrégées

### Formulaire 5001 - Données requises

```typescript
interface Form5001Data {
  // Informations personnelles
  taxpayerName: string;
  taxId: string;              // NIF/AVS
  address: string;
  city: string;
  postalCode: string;
  country: string;

  // Période fiscale
  taxYear: number;

  // Liste des dividendes
  dividends: DividendEntry[];

  // Totaux
  totalGrossAmount: number;
  totalWithholdingTax: number;
  totalTreatyAmount: number;
  totalReclaimableAmount: number;
}
```

### Calculs

```
Pour chaque dividende:
  - Montant brut (EUR)
  - Retenue française (12.8% ou 25%)
  - Retenue conventionnelle (15%)
  - Montant récupérable = Retenue FR - Retenue conventionnelle

Totaux:
  - Somme des montants bruts
  - Somme des retenues françaises
  - Somme des retenues conventionnelles
  - Somme des montants récupérables
```

## Mock actuel

L'implémentation actuelle utilise un **mock HTML** pour simuler la génération PDF:

```typescript
// useFormGeneration.ts
const generateMockPDF = async (formData: Form5001Data): Promise<Blob> => {
  const htmlContent = `
    <!DOCTYPE html>
    <html>
      <head>
        <style>/* Styles pour le formulaire */</style>
      </head>
      <body>
        <h1>FORMULAIRE 5001</h1>
        <!-- Données du contribuable -->
        <!-- Table des dividendes -->
        <!-- Calculs et totaux -->
      </body>
    </html>
  `;

  return new Blob([htmlContent], { type: 'text/html' });
};
```

### Limites du mock

- ❌ Pas de vrai PDF (juste HTML)
- ❌ Pas de formulaire officiel 5000/5001 conforme
- ❌ Pas de signature électronique
- ❌ Pas de sauvegarde en base de données

## Backend à implémenter (Spring Boot BFF)

### Endpoints requis

```java
@RestController
@RequestMapping("/api/forms")
public class FormController {

    // Générer les formulaires
    @PostMapping("/generate")
    public ResponseEntity<FormResponse> generateForm(@RequestBody FormRequest request) {
        // 1. Valider les données
        // 2. Générer PDF avec Apache PDFBox ou iText
        // 3. Sauvegarder en base
        // 4. Retourner URL de preview
    }

    // Télécharger le PDF
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadForm(@PathVariable String id) {
        // 1. Récupérer le PDF depuis le stockage
        // 2. Retourner avec headers appropriés
    }
}
```

### Technologies recommandées

**Option 1: Apache PDFBox (Recommandée)**
```xml
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>3.0.0</version>
</dependency>
```

**Option 2: iText**
```xml
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>8.0.2</version>
</dependency>
```

**Option 3: Service Python externe (reportlab/weasyprint)**
- Appel via HTTP/gRPC depuis Spring Boot
- Plus flexible pour le design complexe
- Requiert un microservice séparé

### Stockage

```java
@Entity
public class TaxForm {
    @Id
    private String id;
    private String userId;
    private Integer taxYear;
    private String pdfPath;  // S3, GCS, ou filesystem
    private LocalDateTime generatedAt;
    private String status;   // DRAFT, GENERATED, SUBMITTED

    @OneToMany
    private List<DividendEntry> dividends;
}
```

## Formulaires officiels

### Formulaire 5000
- **Demande de remboursement de l'excédent d'impôt**
- Informations du contribuable
- Montant total réclamé
- Signature et date

### Formulaire 5001
- **Liste détaillée des dividendes** (annexe au 5000)
- Une ligne par dividende reçu
- Colonnes : Titre, ISIN, Date, Montant brut, Retenue, Récupérable

## Points d'attention

### Juridique
- ⚠️ Les formulaires générés doivent être conformes aux modèles officiels
- ⚠️ Signature électronique ou physique requise
- ⚠️ Conservation des justificatifs (relevés bancaires)

### Technique
- Validation stricte des données (NIF/AVS valide)
- Conversion de devises (CHF → EUR)
- Agrégation par année fiscale
- Gestion des dividendes multiples du même titre
- Horodatage et traçabilité

### UX
- Prévisualisation avant génération finale
- Édition possible des informations personnelles
- Export en PDF de qualité pour impression
- Historique des formulaires générés

## Prochaines étapes

### Phase 1: Backend BFF ✅ (À faire)
1. Créer les endpoints Spring Boot
2. Implémenter la génération PDF (PDFBox)
3. Ajouter la validation des données
4. Configurer le stockage (S3/GCS)

### Phase 2: Modèles officiels
1. Obtenir les templates des formulaires 5000/5001
2. Créer les layouts PDF conformes
3. Tester la conformité avec l'administration fiscale

### Phase 3: Améliorations
1. Signature électronique
2. Envoi automatique par email
3. Suivi du statut (soumis, accepté, remboursé)
4. Notifications

## Test du mock

Pour tester la fonctionnalité actuelle:

```bash
npm run dev
```

1. Se connecter
2. Scanner un relevé de dividendes
3. Cliquer sur "Générer Formulaires 5000/5001"
4. Remplir l'adresse et sélectionner l'année
5. Cliquer sur "Générer les formulaires"
6. Voir l'aperçu HTML (remplacera par PDF réel)

## Exemple de données

```typescript
const exampleFormData: Form5001Data = {
  taxpayerName: "John Doe",
  taxId: "1234567890123",
  address: "123 Rue de la République",
  city: "Paris",
  postalCode: "75001",
  country: "France",
  taxYear: 2024,
  dividends: [
    {
      securityName: "AIR LIQUIDE SA",
      isin: "FR0000120073",
      paymentDate: "2024-05-15",
      grossAmount: 1250.00,
      currency: "EUR",
      withholdingTax: 160.00,  // 12.8%
      treatyAmount: 187.50,    // 15%
      reclaimableAmount: 0.00, // Pas de récupération avec PFU
      frenchRate: 0.128
    }
  ],
  totalGrossAmount: 1250.00,
  totalWithholdingTax: 160.00,
  totalTreatyAmount: 187.50,
  totalReclaimableAmount: 0.00
};
```

## Notes importantes

⚠️ **PFU vs Progressif**:
- Avec PFU (12.8%), pas de récupération possible (12.8% < 15%)
- Avec barème progressif (25%), récupération de 10% (25% - 15%)
- L'utilisateur doit choisir le régime fiscal approprié

💡 **Conseil**: Toujours vérifier avec un expert fiscal avant de soumettre les formulaires à l'administration.
