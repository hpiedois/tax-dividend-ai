# Formulaires Officiels 5000/5001 - TODO

## État actuel

### Mock HTML simplifié
Actuellement, la génération produit un **document HTML simplifié** qui simule le formulaire 5001. Ce n'est **PAS un formulaire officiel** et ne peut pas être soumis à l'administration fiscale.

### Formulaires officiels disponibles
Les vrais formulaires PDF sont stockés dans `/docs/officials/fr/` :

```
5000-sd_*.pdf   → Formulaire de demande de remboursement (4 versions)
5001-sd_*.pdf   → Liste détaillée des dividendes (4 versions)
```

## Problèmes à résoudre

### 1. Format PDF requis
- ❌ **Actuel** : Génération HTML dans une iframe
- ✅ **Requis** : Génération PDF conforme aux modèles officiels

### 2. Remplissage de formulaires
Les formulaires officiels sont des PDF avec des champs à remplir. Deux approches possibles :

#### Option A: PDF Form Filling (Recommandée)
Utiliser une bibliothèque pour remplir les champs du PDF existant :

**Frontend (JavaScript):**
```bash
npm install pdf-lib
```

```typescript
import { PDFDocument } from 'pdf-lib';

async function fillOfficialForm(formData: Form5001Data) {
  // Charger le PDF officiel depuis /docs/officials/fr/5001-sd_4532.pdf
  const existingPdfBytes = await fetch('/forms/5001-template.pdf').then(res => res.arrayBuffer());
  const pdfDoc = await PDFDocument.load(existingPdfBytes);

  const form = pdfDoc.getForm();

  // Remplir les champs (noms exacts à vérifier dans le PDF)
  form.getTextField('nom').setText(formData.taxpayerName);
  form.getTextField('nif').setText(formData.taxId);
  // ... etc

  const pdfBytes = await pdfDoc.save();
  return new Blob([pdfBytes], { type: 'application/pdf' });
}
```

**Backend (Spring Boot - Meilleure option):**
```java
// Apache PDFBox ou iText
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

@Service
public class FormFillingService {
    public byte[] fillForm5001(Form5001Data data) throws IOException {
        // Charger le template depuis resources/
        PDDocument document = PDDocument.load(
            getClass().getResourceAsStream("/forms/5001-template.pdf")
        );

        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();

        // Remplir les champs
        acroForm.getField("nom").setValue(data.getTaxpayerName());
        acroForm.getField("nif").setValue(data.getTaxId());
        // ... liste des dividendes (boucle)

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.save(out);
        document.close();

        return out.toByteArray();
    }
}
```

#### Option B: PDF Generation from Scratch
Créer un PDF qui ressemble visuellement aux formulaires officiels :

**Inconvénients:**
- ❌ Layout complexe à reproduire pixel-perfect
- ❌ Mise à jour difficile si les formulaires changent
- ❌ Risque de non-conformité avec l'administration

### 3. Identification des champs PDF

Il faut d'abord analyser les PDF officiels pour connaître les noms exacts des champs :

```bash
# Avec PDFBox (Java)
java -jar pdfbox-app.jar ExportFDF docs/officials/fr/5001-sd_4532.pdf

# Ou avec pdf-lib (Node.js)
import { PDFDocument } from 'pdf-lib';

const pdfBytes = await fs.readFile('docs/officials/fr/5001-sd_4532.pdf');
const pdfDoc = await PDFDocument.load(pdfBytes);
const form = pdfDoc.getForm();
const fields = form.getFields();

fields.forEach(field => {
  console.log(`Field: ${field.getName()} (${field.constructor.name})`);
});
```

## Plan d'implémentation

### Phase 1: Analyse des formulaires ⏳
1. Extraire les noms de champs des PDF officiels
2. Créer un mapping TypeScript `FormFieldNames`
3. Documenter quels champs correspondent à quelles données

### Phase 2: Choix de la technologie ⏳
**Recommandation: Backend Spring Boot + Apache PDFBox**

Avantages:
- ✅ Meilleure sécurité (pas de PDF sensibles côté client)
- ✅ PDFBox très mature et fiable
- ✅ Contrôle total sur le processus
- ✅ Possibilité d'ajouter signature électronique

### Phase 3: Copie des templates ⏳
```bash
# Copier les formulaires officiels dans le backend
cp docs/officials/fr/5001-sd_4532.pdf backend/src/main/resources/forms/
cp docs/officials/fr/5000-sd_4482.pdf backend/src/main/resources/forms/
```

### Phase 4: Implémentation backend ⏳
1. Service de remplissage de formulaires
2. Endpoints REST
3. Tests avec données réelles

### Phase 5: Intégration frontend ⏳
1. Remplacer le mock HTML par appel API
2. Téléchargement du PDF rempli
3. Prévisualisation dans iframe

## Exemple de structure attendue

### Form 5001 - Champs probables
```typescript
interface Form5001Fields {
  // Page 1 - Identité
  nom_prenom: string;           // Nom et prénom
  adresse: string;              // Adresse complète
  code_postal: string;
  ville: string;
  pays: string;
  nif: string;                  // Numéro d'identification fiscale

  // Page 2 - Année fiscale
  annee_revenus: string;        // Ex: "2024"

  // Tableau des dividendes (lignes répétées)
  dividende_1_date: string;
  dividende_1_emetteur: string;
  dividende_1_montant: string;
  dividende_1_retenue: string;
  // ... jusqu'à dividende_N

  // Totaux
  total_brut: string;
  total_retenu: string;
  total_reclamable: string;
}
```

### Form 5000 - Champs probables
```typescript
interface Form5000Fields {
  // Référence au 5001
  annexe_5001: boolean;         // Case à cocher

  // Montant total réclamé
  montant_total: string;

  // Signature et date
  lieu_signature: string;
  date_signature: string;
  signature: string;            // Image ou champ vide
}
```

## Notes importantes

⚠️ **Conformité légale:**
- Les formulaires doivent être **exactement** conformes aux modèles officiels
- Toute modification pourrait entraîner un rejet par l'administration fiscale
- Il vaut mieux remplir les PDF existants que de créer de nouveaux documents

⚠️ **Signature:**
- Les formulaires officiels requièrent une signature
- Options :
  - Signature électronique (API tierce)
  - Impression + signature manuscrite
  - Export PDF pour signature ultérieure

⚠️ **Validation:**
- Vérifier les contraintes de chaque champ (format, longueur, type)
- Valider les totaux et calculs
- S'assurer que tous les champs obligatoires sont remplis

## Prochaines étapes immédiates

1. **Analyser les PDF** : Extraire la liste des champs
2. **Créer un PoC** : Remplir un seul champ pour valider la méthode
3. **Documenter le mapping** : Créer une doc complète des champs
4. **Implémenter le backend** : Service de remplissage complet
5. **Remplacer le mock** : Supprimer le HTML actuel

## Ressources

- Apache PDFBox: https://pdfbox.apache.org/
- pdf-lib: https://pdf-lib.js.org/
- iText: https://itextpdf.com/ (commercial pour certains usages)
- Formulaires officiels: `/docs/officials/fr/`
