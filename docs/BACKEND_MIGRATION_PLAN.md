# Plan de migration Backend - Génération de PDF

## 📋 Pourquoi migrer vers le backend ?

### **Problèmes de l'approche frontend actuelle**

| Problème | Impact | Solution Backend |
|----------|--------|------------------|
| **Sécurité** | Templates PDF exposés publiquement | Templates protégés côté serveur |
| **Performance** | Limité par le navigateur | Serveur dédié, multi-threading |
| **Stockage** | Aucun historique | Base de données + S3 |
| **Signature électronique** | Quasi impossible | Intégration DocuSign/Adobe Sign |
| **Validation** | Côté client (contournable) | Règles métier serveur |
| **Audit/Logs** | Aucun | Traçabilité complète |
| **Batch processing** | Impossible | Génération pour plusieurs clients |

## 🏗️ Architecture recommandée

### **Option A : Spring Boot (Recommandé pour production)**

```
┌─────────────────────────────────────────────────────────────┐
│                      Frontend (React)                        │
│                  localhost:5173 (dev)                        │
└─────────────────────┬───────────────────────────────────────┘
                      │ HTTP/REST
                      ↓
┌─────────────────────────────────────────────────────────────┐
│                 BFF Gateway (Spring Boot)                    │
│                   localhost:8080                             │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Controllers                                          │  │
│  │  - POST /api/forms/generate                          │  │
│  │  - GET  /api/forms/{id}/download                     │  │
│  │  - GET  /api/forms/{id}/status                       │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Services                                             │  │
│  │  - PDFGenerationService (Apache PDFBox)              │  │
│  │  - ValidationService                                  │  │
│  │  - StorageService (S3 / MinIO)                       │  │
│  │  - SignatureService (DocuSign API)                   │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Repositories                                         │  │
│  │  - FormRepository (PostgreSQL)                        │  │
│  │  - UserRepository                                     │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ↓
        ┌─────────────────────────────┐
        │  External Services          │
        │  - AWS S3 (PDFs)            │
        │  - DocuSign API             │
        │  - PostgreSQL               │
        └─────────────────────────────┘
```

### **Option B : Node.js/Express (Plus rapide à démarrer)**

```
┌─────────────────────────────────────────────────────────────┐
│                   BFF Gateway (Node.js)                      │
│                   localhost:3000                             │
│                                                              │
│  Routes:                                                     │
│  - POST /api/forms/generate                                 │
│  - GET  /api/forms/:id/download                             │
│                                                              │
│  Services:                                                   │
│  - pdfService.ts (pdf-lib)                                  │
│  - storageService.ts (S3)                                   │
│  - validationService.ts                                     │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

## 📂 Structure du projet Backend (Spring Boot)

```
backend/
├── src/main/java/com/taxdividend/
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── S3Config.java
│   │   └── SwaggerConfig.java
│   │
│   ├── controller/
│   │   ├── FormController.java
│   │   └── HealthController.java
│   │
│   ├── service/
│   │   ├── PDFGenerationService.java
│   │   ├── Form5000Service.java
│   │   ├── Form5001Service.java
│   │   ├── ValidationService.java
│   │   ├── StorageService.java
│   │   └── SignatureService.java
│   │
│   ├── repository/
│   │   ├── FormRepository.java
│   │   └── UserRepository.java
│   │
│   ├── model/
│   │   ├── Form5000Data.java
│   │   ├── Form5001Data.java
│   │   ├── GeneratedForm.java
│   │   └── User.java
│   │
│   ├── dto/
│   │   ├── FormGenerationRequest.java
│   │   ├── FormGenerationResponse.java
│   │   └── ValidationError.java
│   │
│   └── exception/
│       ├── FormGenerationException.java
│       └── ValidationException.java
│
├── src/main/resources/
│   ├── templates/
│   │   ├── 5000-template.pdf
│   │   └── 5001-template.pdf
│   ├── application.yml
│   └── application-prod.yml
│
├── pom.xml
└── README.md
```

## 🔧 Technologies recommandées

### **Spring Boot Stack**

```xml
<!-- pom.xml -->
<dependencies>
    <!-- PDF Generation -->
    <dependency>
        <groupId>org.apache.pdfbox</groupId>
        <artifactId>pdfbox</artifactId>
        <version>3.0.1</version>
    </dependency>

    <!-- Storage (AWS S3) -->
    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>s3</artifactId>
        <version>2.20.0</version>
    </dependency>

    <!-- Database -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>

    <!-- Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt</artifactId>
        <version>0.12.3</version>
    </dependency>

    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
</dependencies>
```

## 🔄 Flow de génération (Backend)

```java
@PostMapping("/api/forms/generate")
public ResponseEntity<FormGenerationResponse> generateForms(
    @Valid @RequestBody FormGenerationRequest request,
    @AuthenticationPrincipal UserDetails user
) {
    // 1. Validation
    validationService.validate(request);

    // 2. Génération PDFs
    byte[] pdf5000 = form5000Service.generate(request.getForm5000Data());
    byte[] pdf5001 = form5001Service.generate(request.getForm5001Data());

    // 3. Création ZIP
    byte[] zipFile = createZip(pdf5000, pdf5001);

    // 4. Upload S3
    String s3Key = storageService.upload(zipFile, user.getUsername());

    // 5. Sauvegarde en BDD
    GeneratedForm form = new GeneratedForm();
    form.setUserId(user.getId());
    form.setS3Key(s3Key);
    form.setStatus(FormStatus.GENERATED);
    formRepository.save(form);

    // 6. URL signée (expire dans 1h)
    String downloadUrl = storageService.generatePresignedUrl(s3Key, 3600);

    return ResponseEntity.ok(new FormGenerationResponse(
        form.getId(),
        downloadUrl,
        "formulaires-2024.zip"
    ));
}
```

## 📊 Schéma de base de données

```sql
-- Users
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    full_name VARCHAR(255),
    tax_id VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW()
);

-- Generated Forms
CREATE TABLE generated_forms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    s3_key VARCHAR(500) NOT NULL,
    file_name VARCHAR(255),
    tax_year INTEGER,
    status VARCHAR(50), -- GENERATED, SIGNED, SUBMITTED
    created_at TIMESTAMP DEFAULT NOW(),
    expires_at TIMESTAMP
);

-- Dividends
CREATE TABLE dividends (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    form_id UUID REFERENCES generated_forms(id),
    security_name VARCHAR(255),
    isin VARCHAR(12),
    payment_date DATE,
    gross_amount DECIMAL(10,2),
    currency VARCHAR(3),
    withholding_tax DECIMAL(10,2),
    reclaimable_amount DECIMAL(10,2)
);

-- Form submissions
CREATE TABLE form_submissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    form_id UUID REFERENCES generated_forms(id),
    submitted_at TIMESTAMP,
    submission_method VARCHAR(50), -- EMAIL, API, POSTAL
    tracking_number VARCHAR(100),
    status VARCHAR(50) -- PENDING, APPROVED, REJECTED
);
```

## 🚀 Plan de migration (4 semaines)

### **Semaine 1 : Setup & Infrastructure**
- ✅ Initialiser projet Spring Boot
- ✅ Configurer PostgreSQL
- ✅ Configurer S3 (MinIO en local)
- ✅ API de santé basique
- ✅ CI/CD (GitHub Actions)

### **Semaine 2 : Génération PDF côté serveur**
- ✅ Service de génération Form 5000
- ✅ Service de génération Form 5001
- ✅ Création ZIP
- ✅ Tests unitaires
- ✅ Migration des templates

### **Semaine 3 : Storage & Security**
- ✅ Upload vers S3
- ✅ URLs présignées
- ✅ Authentification JWT
- ✅ Validation des données
- ✅ Logs & Audit

### **Semaine 4 : Intégration Frontend**
- ✅ Adapter le hook `useFormGeneration`
- ✅ Gestion des erreurs
- ✅ Loading states
- ✅ Tests E2E
- ✅ Documentation

## 📝 Code Frontend (après migration)

```typescript
// frontend/src/hooks/useFormGeneration.ts
const generateFormWithBackend = async (request: FormGenerationRequest): Promise<FormGenerationResponse> => {
  const response = await fetch('/api/forms/generate', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getAuthToken()}`
    },
    body: JSON.stringify(request)
  });

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message);
  }

  return response.json();
};
```

## 🎯 Avantages immédiats après migration

1. ✅ **Sécurité** : Templates protégés, validation serveur
2. ✅ **Performance** : Génération plus rapide, pas de limite browser
3. ✅ **Stockage** : Historique complet des formulaires
4. ✅ **Audit** : Logs de toutes les générations
5. ✅ **Scalabilité** : Support multi-utilisateurs B2B
6. ✅ **Maintenance** : Code Java/TypeScript côté serveur plus facile à maintenir

## 🔜 Features possibles (après backend)

1. **Signature électronique** : Intégration DocuSign
2. **Soumission automatique** : API de l'administration fiscale
3. **Suivi de statut** : Notifications email
4. **Export formats** : XML, JSON, CSV
5. **Batch processing** : Génération pour plusieurs clients (B2B)
6. **Analytics** : Statistiques de réclamation

## 📚 Ressources

- **Apache PDFBox** : https://pdfbox.apache.org/
- **AWS S3 SDK Java** : https://docs.aws.amazon.com/sdk-for-java/
- **Spring Boot** : https://spring.io/projects/spring-boot
- **DocuSign API** : https://developers.docusign.com/

---

**Recommandation** : Commencer par Spring Boot pour une solution production-ready. Node.js est acceptable pour un MVP rapide, mais Spring Boot offre de meilleures garanties pour la conformité fiscale et réglementaire.
