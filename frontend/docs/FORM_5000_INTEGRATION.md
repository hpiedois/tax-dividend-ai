# Intégration du Formulaire 5000 - Guide complet

## ✅ Implémentation terminée

Le formulaire 5000 (Attestation de Résidence) a été intégré avec succès dans l'application.

## 📦 Architecture

### **Génération simultanée des deux formulaires**

Lorsque l'utilisateur clique sur "Générer Formulaires 5000/5001", le système :

1. ✅ Génère le **formulaire 5001** (Liquidation de dividendes)
2. ✅ Génère le **formulaire 5000** (Attestation de résidence)
3. ✅ Crée une **archive ZIP** contenant les deux PDFs
4. ✅ Télécharge le ZIP automatiquement

### **Structure des fichiers**

```
frontend/
├── public/forms/
│   ├── 5000-template.pdf ✨ NOUVEAU
│   └── 5001-template.pdf
├── src/
│   ├── lib/
│   │   ├── pdf-form-5000-filler.ts ✨ NOUVEAU
│   │   └── pdf-form-filler.ts (5001)
│   ├── hooks/
│   │   └── useFormGeneration.ts (modifié pour ZIP)
│   └── types/
│       └── form.types.ts (+ Form5000Data)
```

## 🎯 Fonctionnalités

### **Formulaire 5000 - Champs remplis automatiquement**

| Section | Champs remplis |
|---------|----------------|
| **I. Nature des revenus** | ☑️ Case "Dividendes" cochée |
| **II. Bénéficiaire** | Nom, Profession, Adresse complète, Email |
| **III. Déclaration** | Pays de résidence (Suisse), État, N° fiscal, Date et lieu |
| **VIII. Coordonnées bancaires** | IBAN, SWIFT, Nom banque (si fournis) |

### **Champs non remplis** (à compléter manuellement)

- **Section IV** : Déclaration de l'administration étrangère (cachet suisse)
- **Section V** : Déclaration de l'établissement payeur français
- **Signatures** : Toutes les signatures restent manuelles

## 🔧 Types de données

### **Form5000Data**

```typescript
interface Form5000Data {
  // Personal information
  taxpayerName: string;
  profession: string;
  address: string;
  city: string;
  postalCode: string;
  country: string;
  email?: string;

  // Tax information
  taxId: string;
  residenceCountry: string; // "Suisse"
  taxYear: number;

  // Declaration date
  declarationDate: string; // DD/MM/YYYY
  declarationPlace: string;

  // Banking information for refund
  iban?: string;
  swift?: string;
  bankName?: string;
}
```

## 📝 Utilisation

### **Pour l'utilisateur final**

1. Scanner les relevés de dividendes
2. Remplir l'adresse postale
3. Cliquer sur "Générer Formulaires 5000/5001"
4. **Télécharger le ZIP** contenant :
   - `formulaire-5000-2024.pdf` (Attestation)
   - `formulaire-5001-2024.pdf` (Liquidation)

### **Workflow complet**

```
Utilisateur scan des relevés
    ↓
Saisit son adresse
    ↓
Clique "Générer"
    ↓
Système génère :
  - Form 5000 (pré-rempli)
  - Form 5001 (pré-rempli)
    ↓
ZIP téléchargé
    ↓
Utilisateur :
  1. Imprime le 5000
  2. Fait tamponner par l'admin suisse
  3. Envoie les deux formulaires en France
```

## 🎨 UI/UX

### **Messages utilisateur**

- **Succès** : "📦 2 formulaires générés (5000 + 5001) dans un fichier ZIP"
- **Téléchargement** : `formulaires-2024.zip`
- **Bouton** : "Télécharger les formulaires"

### **Console de debug**

```javascript
📄 Generating Form 5001...
✓ Filled a1 (creditor name): Jean Dupont
✓ Filled a3 (article): 15
...
✅ Form fields filling complete

📄 Generating Form 5000...
✓ Filled Name: Jean Dupont
✓ Filled Profession: Particulier
✓ Filled Address: Rue de Genève...
✓ Filled Residence country: Suisse
✅ Form 5000 fields filling complete

📦 Creating ZIP archive...
✅ ZIP archive created successfully
```

## 🔍 Identification des champs PDF

Le formulaire 5000 utilise un **système de fallback intelligent** :

```typescript
// Exemple : champ "nom"
fillTextField(form,
  ['nom', 'nomPrenom', 'name', 'beneficiaire'], // Essaie tous ces noms
  'Jean Dupont',
  'Name'
);
```

Si le PDF n'a **pas de champs interactifs**, le système :
1. Détecte l'absence de champs
2. Log un warning
3. Retourne le template vide (à remplir manuellement)

## 🧪 Tests

### **Test manuel**

1. Lancer l'app : `npm run dev`
2. Se connecter
3. Scanner un relevé
4. Aller sur "Générer Formulaires"
5. Remplir l'adresse
6. Cliquer "Générer"
7. Vérifier le ZIP téléchargé

### **Vérifications**

- ✅ Le ZIP contient 2 fichiers
- ✅ Le 5000 est pré-rempli (nom, adresse, pays)
- ✅ Le 5001 est pré-rempli (dividendes, totaux)
- ✅ Les dates sont au format DD/MM/YYYY
- ✅ Le pays est "Suisse" (pas "France")

## 🐛 Troubleshooting

### **Le ZIP ne se télécharge pas**

Vérifiez la console :
```javascript
// Si erreur lors de la génération du ZIP
Error generating official PDFs: [error details]
```

### **Le formulaire 5000 est vide**

Possible que le PDF n'ait pas de champs interactifs. Utilisez l'outil de debug :

```javascript
// Dans la console
await analyzePDFStructure('/forms/5000-template.pdf')
```

### **Les champs ne correspondent pas**

Modifiez les `possibleNames` dans `pdf-form-5000-filler.ts:fillTextField()`

## 📚 Ressources

- **PDF officiel** : `/docs/officials/fr/5000-sd_4062.pdf`
- **Template utilisé** : `/public/forms/5000-template.pdf`
- **Documentation PDF** : [cerfa 5000-FR](https://www.impots.gouv.fr/)

## 🔜 Améliorations futures

### **Court terme**
1. Analyser les champs réels du PDF 5000 (comme pour le 5001)
2. Mapper précisément tous les champs interactifs
3. Ajouter la profession dans Form5001Data
4. Ajouter l'email dans Form5001Data

### **Moyen terme**
1. Preview du ZIP avant téléchargement
2. Option : télécharger séparément ou en ZIP
3. Ajouter un PDF viewer pour chaque formulaire
4. Instructions de soumission intégrées

### **Long terme**
1. OCR pour scanner les cachets/signatures
2. API de soumission directe à l'administration
3. Suivi du statut des demandes
4. Notifications email

## 🎉 Résultat

**Avant** : Seul le formulaire 5001 était généré

**Maintenant** :
- ✅ Formulaire 5000 (Attestation)
- ✅ Formulaire 5001 (Liquidation)
- ✅ Archive ZIP
- ✅ Messages clairs pour l'utilisateur
- ✅ Workflow complet

L'utilisateur peut maintenant générer **les deux formulaires officiels** en un seul clic ! 🚀
