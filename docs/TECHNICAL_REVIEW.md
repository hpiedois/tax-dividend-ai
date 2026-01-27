# Revue Technique Complète - Tax Dividend AI
**Date**: 24 Janvier 2026
**Version**: MVP Frontend v0.0.0

---

## 📋 Résumé Exécutif

Tax Dividend AI est un MVP frontend prometteur avec une architecture propre mais nécessite une stratégie de mise en production claire. Le code est de qualité, mais plusieurs points critiques doivent être adressés avant le déploiement et la connexion au backend.

**Points Forts** ✅
- Architecture frontend moderne et maintenable
- UI/UX soignée avec design system cohérent
- Internationalisation complète (4 langues)
- TypeScript strict et typage solide
- Code relativement propre (~1165 lignes)

**Points Critiques** ⚠️
- Aucun test (0% de couverture)
- Erreurs ESLint non corrigées
- Pas de CI/CD
- Pas de stratégie de déploiement définie
- Pas de backend (tout est mock)
- Aucune sécurité implémentée

---

## 1. REVUE TECHNIQUE DÉTAILLÉE

### 1.1 Qualité du Code

#### ✅ **Points Positifs**

**TypeScript & Typage**
- Configuration stricte activée (`strict: true`)
- Pas de `any` explicite détecté
- Interfaces bien définies (`DividendData`, `MockCase`)
- Types génériques utilisés correctement (`HTMLMotionProps`)

**Organisation du Code**
- Séparation claire des responsabilités (components/lib/locales)
- Composants fonctionnels avec hooks
- Pas de prop drilling excessif
- Utilisation appropriée de Context API

**Performance**
- React 19 avec compilation optimale
- Vite pour un HMR ultra-rapide
- Framer Motion avec animations performantes
- Tailwind JIT pour CSS minimal

#### ⚠️ **Points à Améliorer**

**Erreurs ESLint** (3 erreurs)
```
src/App.tsx:63-64 - Déclarations lexicales dans case block
src/components/theme-provider.tsx:65 - Export non-component
```
→ **Impact**: Moyen - Affecte le Fast Refresh React
→ **Priorité**: Haute - À corriger avant production

**Absence de Tests**
- Aucun framework de test configuré
- Pas de tests unitaires
- Pas de tests d'intégration
- Pas de tests E2E

→ **Impact**: Critique pour production
→ **Priorité**: Haute

**Gestion d'Erreurs**
```typescript
// App.tsx - ligne 37
catch (error) {
    console.error("Error parsing", error);
}
// ❌ Erreur loggée mais pas affichée à l'utilisateur
```

**Sécurité**
- Pas de validation des fichiers uploadés (type MIME, taille)
- Pas de sanitization des inputs
- localStorage non chiffré
- Pas de CSP headers

**Accessibilité**
- Manque d'attributs ARIA
- Contrôle clavier incomplet
- Pas de gestion des lecteurs d'écran

### 1.2 Architecture Frontend

#### Structure Actuelle

```
frontend/
├── src/
│   ├── components/
│   │   ├── auth/          ✅ Bonne séparation
│   │   ├── layout/        ✅ Shell réutilisable
│   │   ├── ui/            ✅ Primitives cohérentes
│   │   ├── views/         ✅ Pages logiques
│   │   └── upload/        ✅ Workflow isolé
│   ├── lib/               ✅ Utilitaires centralisés
│   └── locales/           ✅ i18n structuré
```

**Score Architecture**: 7/10

#### Points Forts
- Composants atomiques bien définis
- Pas de couplage fort entre composants
- État local centralisé dans App.tsx
- Pattern Provider/Consumer pour thème

#### Faiblesses Architecturales

**1. État Monolithique dans App.tsx**
```typescript
// App.tsx contient TOUT l'état applicatif
const [isLoggedIn, setIsLoggedIn] = useState(false);
const [currentView, setCurrentView] = useState<ViewState>('dashboard');
const [scanStep, setScanStep] = useState<'upload' | 'scanning' | 'result'>('upload');
const [scanResults, setScanResults] = useState<DividendData[]>([]);
```
→ **Problème**: Difficile à scaler, re-renders excessifs
→ **Solution**: State management (voir section 3)

**2. Pas de Routing**
```typescript
// Navigation par switch/case
{currentView === 'dashboard' && <DashboardView />}
{currentView === 'history' && <HistoryView />}
```
→ **Problème**: Pas d'URLs, pas de deep linking, pas de history
→ **Solution**: React Router v6+

**3. Mock Data Hardcodés**
```typescript
export const MOCK_HISTORY: MockCase[] = [
    { id: '1', date: '2024-05-15', security: 'AIR LIQUIDE SA', ... },
];
```
→ **Problème**: Pas de séparation env dev/prod
→ **Solution**: API Layer avec MSW pour tests

**4. Pas de Validation**
```typescript
// Aucune validation de formulaire
const handleFilesSelect = async (files: File[]) => {
    // Pas de vérification du type MIME
    // Pas de vérification de la taille
}
```

### 1.3 Performance & Optimisation

#### Bundle Size (estimé)
- **React + React-DOM**: ~140KB gzippé
- **Framer Motion**: ~35KB gzippé
- **i18next**: ~25KB gzippé
- **Tailwind CSS**: ~15KB (JIT optimisé)
- **Total estimé**: ~220KB gzippé

✅ **Acceptable pour un MVP**

#### Opportunités d'Optimisation

**1. Code Splitting**
```typescript
// Actuellement : tout chargé au démarrage
import { DashboardView } from './components/views/DashboardView';

// Recommandé : lazy loading
const DashboardView = lazy(() => import('./components/views/DashboardView'));
```

**2. Image Optimization**
- Aucune image actuellement
- Prévoir compression/WebP pour futures images

**3. Memoization**
```typescript
// Composants réutilisables devraient être memoizés
export const Button = memo(function Button({ ... }) { ... });
```

---

## 2. REVUE FONCTIONNELLE

### 2.1 User Flows Implémentés

#### Flow 1: Authentification ✅
```
Landing → Login Form → Mock Delay (1s) → Dashboard
```
**État**: Fonctionnel mais mock uniquement
**Manque**:
- Gestion de session
- Token JWT
- Refresh token
- Logout

#### Flow 2: Upload & Scan ✅
```
Dashboard → Scan Button → DropZone → Scanning → Results
```
**État**: UX excellente, logique mockée
**Manque**:
- Vrai parsing PDF
- Validation fichiers
- Progress bar réel
- Gestion d'erreurs

#### Flow 3: Historique ✅
```
History View → Liste des cas → Filtres (non implémentés)
```
**État**: Affichage OK, interactions limitées
**Manque**:
- Tri/filtrage
- Pagination
- Détails par cas
- Export CSV/PDF

#### Flow 4: Paramètres ⚠️
```
Settings View → (Vide actuellement)
```
**État**: Placeholder uniquement
**À implémenter**:
- Profil utilisateur
- Préférences
- Gestion NIF/AVS
- Coordonnées bancaires

### 2.2 Fonctionnalités Manquantes Critiques

**Pour B2C (Individus)**
1. ❌ Génération PDF Forms 5000/5001
2. ❌ Profil utilisateur complet (NIF, adresse)
3. ❌ Calcul précis selon convention fiscale
4. ❌ Instructions étape-par-étape post-génération
5. ❌ Sauvegarde des scans
6. ❌ Export des données

**Pour B2B (Fiduciaires)**
1. ❌ Gestion multi-clients
2. ❌ Batch processing
3. ❌ Suivi de statut avancé
4. ❌ Reporting
5. ❌ API pour intégrations

### 2.3 Logique Métier

#### Calcul Tax Reclaim
```typescript
// mock-parser.ts - LOGIQUE SIMPLIFIÉE
const withholdingTax = Number((grossAmount * 0.128).toFixed(2));
const reclaimableAmount = Number((grossAmount * 0.15).toFixed(2));
```

**Problèmes Identifiés**:
1. ❌ Taux fixe 12.8% (devrait dépendre du type de compte)
2. ❌ Ne prend pas en compte les abattements
3. ❌ Pas de distinction PEA/CTO
4. ❌ Pas de gestion multi-devises réelle
5. ❌ Calcul incorrect: reclaimable devrait être `withheld - (gross * 0.15)`

**Formule Correcte**:
```typescript
// Taux français standard: 12.8% (PFU) ou 25% (barème progressif)
// Taux convention France-Suisse: 15%
// Montant réclamable = (tauxFrançais - 15%) * montantBrut

const frenchRate = 0.25; // ou 0.128 selon option fiscale
const treatyRate = 0.15;
const withheld = grossAmount * frenchRate;
const shouldWithhold = grossAmount * treatyRate;
const reclaimable = withheld - shouldWithhold;
```

---

## 3. PROPOSITIONS D'AMÉLIORATION

### 3.1 Corrections Immédiates (Sprint 1)

#### 🔴 Priorité Critique

**1. Corriger les Erreurs ESLint**
```typescript
// App.tsx - Wrapper les case avec des blocks
case 'scan':
  {
    const scanContent = ( // Wrap avec {}
      scanStep === 'upload' ? ...
    );
    return scanContent;
  }

// theme-provider.tsx - Exporter useTheme séparément
// Créer src/hooks/useTheme.ts
export { useTheme } from './hooks/useTheme';
```

**2. Ajouter Validation Fichiers**
```typescript
const ALLOWED_TYPES = ['application/pdf'];
const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

const validateFile = (file: File): string | null => {
  if (!ALLOWED_TYPES.includes(file.type)) {
    return 'Seuls les fichiers PDF sont acceptés';
  }
  if (file.size > MAX_FILE_SIZE) {
    return 'Fichier trop volumineux (max 10MB)';
  }
  return null;
};
```

**3. Gestion d'Erreurs UI**
```typescript
const [error, setError] = useState<string | null>(null);

// Dans handleFilesSelect
try {
  const result = await parseDividendPDF(files[i]);
  results.push(result);
} catch (error) {
  setError(error instanceof Error ? error.message : 'Erreur inconnue');
}
```

#### 🟡 Priorité Haute

**4. Setup Testing Infrastructure**
```bash
npm install -D vitest @testing-library/react @testing-library/jest-dom jsdom
```

```typescript
// vitest.config.ts
import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: './src/test/setup.ts',
  },
});
```

**5. Ajouter React Router**
```bash
npm install react-router-dom
```

```typescript
// App.tsx
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';

<BrowserRouter>
  <Routes>
    <Route path="/login" element={<LoginScreen />} />
    <Route path="/dashboard" element={<PrivateRoute><DashboardView /></PrivateRoute>} />
    <Route path="/history" element={<PrivateRoute><HistoryView /></PrivateRoute>} />
    <Route path="/scan" element={<PrivateRoute><ScanView /></PrivateRoute>} />
    <Route path="/settings" element={<PrivateRoute><SettingsView /></PrivateRoute>} />
    <Route path="/" element={<Navigate to="/dashboard" replace />} />
  </Routes>
</BrowserRouter>
```

### 3.2 Améliorations Architecture (Sprint 2-3)

#### **1. State Management avec Zustand**

Pourquoi Zustand vs Redux/Context ?
- ✅ Plus simple que Redux (pas de boilerplate)
- ✅ Meilleures perfs que Context (re-renders sélectifs)
- ✅ TypeScript-first
- ✅ DevTools intégré
- ✅ ~1KB gzipped

```bash
npm install zustand
```

```typescript
// src/stores/authStore.ts
import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      token: null,
      isAuthenticated: false,
      login: async (email, password) => {
        const { user, token } = await authAPI.login(email, password);
        set({ user, token, isAuthenticated: true });
      },
      logout: () => set({ user: null, token: null, isAuthenticated: false }),
    }),
    { name: 'auth-storage' }
  )
);

// src/stores/scanStore.ts
interface ScanState {
  step: 'upload' | 'scanning' | 'result';
  results: DividendData[];
  processingCount: { current: number; total: number };
  setStep: (step: ScanState['step']) => void;
  addResult: (result: DividendData) => void;
  reset: () => void;
}

export const useScanStore = create<ScanState>((set) => ({
  step: 'upload',
  results: [],
  processingCount: { current: 0, total: 0 },
  setStep: (step) => set({ step }),
  addResult: (result) => set((state) => ({
    results: [...state.results, result]
  })),
  reset: () => set({
    step: 'upload',
    results: [],
    processingCount: { current: 0, total: 0 }
  }),
}));
```

#### **2. API Layer avec React Query**

```bash
npm install @tanstack/react-query axios
```

```typescript
// src/lib/api/client.ts
import axios from 'axios';
import { useAuthStore } from '@/stores/authStore';

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8000/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor pour ajouter le token
apiClient.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// src/lib/api/dividends.ts
export const dividendsAPI = {
  parsePDF: async (file: File): Promise<DividendData> => {
    const formData = new FormData();
    formData.append('file', file);
    const { data } = await apiClient.post('/dividends/parse', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return data;
  },

  getHistory: async (): Promise<MockCase[]> => {
    const { data } = await apiClient.get('/dividends/history');
    return data;
  },
};

// src/hooks/useDividends.ts
import { useQuery, useMutation } from '@tanstack/react-query';

export const useParsePDF = () => {
  return useMutation({
    mutationFn: dividendsAPI.parsePDF,
    onSuccess: (data) => {
      // Ajouter au store
      useScanStore.getState().addResult(data);
    },
  });
};

export const useDividendHistory = () => {
  return useQuery({
    queryKey: ['dividends', 'history'],
    queryFn: dividendsAPI.getHistory,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
};
```

#### **3. Form Management avec React Hook Form**

```bash
npm install react-hook-form zod @hookform/resolvers
```

```typescript
// src/components/auth/LoginForm.tsx
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';

const loginSchema = z.object({
  email: z.string().email('Email invalide'),
  password: z.string().min(8, 'Minimum 8 caractères'),
});

type LoginForm = z.infer<typeof loginSchema>;

export function LoginForm() {
  const { register, handleSubmit, formState: { errors } } = useForm<LoginForm>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = (data: LoginForm) => {
    useAuthStore.getState().login(data.email, data.password);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <Input {...register('email')} error={errors.email?.message} />
      <Input {...register('password')} type="password" error={errors.password?.message} />
      <Button type="submit">Connexion</Button>
    </form>
  );
}
```

### 3.3 Sécurité (Sprint 4)

#### **1. Validation & Sanitization**

```typescript
// src/lib/validation.ts
import DOMPurify from 'dompurify';

export const sanitizeInput = (input: string): string => {
  return DOMPurify.sanitize(input);
};

export const validateISIN = (isin: string): boolean => {
  return /^[A-Z]{2}[A-Z0-9]{9}[0-9]$/.test(isin);
};
```

#### **2. CSP Headers**

```typescript
// vite.config.ts
export default defineConfig({
  plugins: [
    react(),
    {
      name: 'html-transform',
      transformIndexHtml(html) {
        return html.replace(
          '<head>',
          `<head>
            <meta http-equiv="Content-Security-Policy"
                  content="default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:; font-src 'self' data:;">
          `
        );
      },
    },
  ],
});
```

#### **3. Environment Variables**

```bash
# .env.development
VITE_API_URL=http://localhost:8000/api
VITE_ENV=development

# .env.production
VITE_API_URL=https://api.taxdividend.ai
VITE_ENV=production
```

```typescript
// src/config/env.ts
export const config = {
  apiUrl: import.meta.env.VITE_API_URL,
  environment: import.meta.env.VITE_ENV,
  isDev: import.meta.env.DEV,
  isProd: import.meta.env.PROD,
} as const;
```

---

## 4. STRATÉGIE BACKEND & INFRASTRUCTURE

### 4.1 Architecture Backend Recommandée

#### **Option 1: Python FastAPI (Recommandé pour MVP)** ⭐

**Pourquoi FastAPI ?**
- ✅ Excellent pour traitement PDF/ML
- ✅ Performance comparable à Node.js
- ✅ Typage strict (Pydantic)
- ✅ Auto-documentation OpenAPI
- ✅ Async natif
- ✅ Écosystème ML (pdfplumber, PyPDF2, OpenAI)

**Stack Technique**:
```
┌─────────────────────────────────────────┐
│         Frontend (Vite + React)          │
└─────────────────┬───────────────────────┘
                  │ HTTPS/REST
┌─────────────────▼───────────────────────┐
│      FastAPI (Python 3.11+)             │
│  ┌──────────────────────────────────┐   │
│  │ Routes: /auth, /dividends, /forms│   │
│  │ Middleware: CORS, Auth, Rate Limit   │
│  └──────────────────────────────────┘   │
└─────────────────┬───────────────────────┘
                  │
    ┌─────────────┼─────────────┐
    │             │             │
┌───▼────┐  ┌────▼─────┐  ┌───▼────┐
│PostgreSQL│ │  Redis   │  │  S3    │
│ (Users,  │ │  (Cache, │  │ (PDFs, │
│ Cases)   │ │  Queue)  │  │ Forms) │
└──────────┘ └──────────┘  └────────┘
```

**Structure Backend**:
```
backend/
├── app/
│   ├── main.py                 # FastAPI app
│   ├── core/
│   │   ├── config.py           # Settings
│   │   ├── security.py         # JWT, passwords
│   │   └── database.py         # SQLAlchemy
│   ├── api/
│   │   ├── deps.py             # Dependencies
│   │   └── v1/
│   │       ├── auth.py         # Login, register
│   │       ├── dividends.py    # PDF parsing
│   │       ├── forms.py        # Generate 5000/5001
│   │       └── users.py        # Profile management
│   ├── models/                 # SQLAlchemy models
│   ├── schemas/                # Pydantic schemas
│   ├── services/
│   │   ├── pdf_parser.py       # PDF extraction
│   │   ├── tax_calculator.py   # Tax logic
│   │   └── form_generator.py   # PDF generation
│   └── workers/                # Background tasks (Celery)
├── tests/
├── alembic/                    # DB migrations
├── requirements.txt
└── Dockerfile
```

**Exemple de Code**:
```python
# app/main.py
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI(title="Tax Dividend AI API")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# app/api/v1/dividends.py
from fastapi import APIRouter, UploadFile, Depends
from app.services.pdf_parser import parse_dividend_pdf
from app.core.security import get_current_user

router = APIRouter()

@router.post("/parse")
async def parse_pdf(
    file: UploadFile,
    current_user: User = Depends(get_current_user)
):
    """Parse a dividend PDF statement"""
    content = await file.read()
    result = await parse_dividend_pdf(content)
    return result

# app/services/pdf_parser.py
import pdfplumber
from typing import Dict

async def parse_dividend_pdf(content: bytes) -> Dict:
    with pdfplumber.open(io.BytesIO(content)) as pdf:
        text = pdf.pages[0].extract_text()
        # Extract ISIN, amounts, dates using regex/ML
        return {
            "security_name": extract_security(text),
            "isin": extract_isin(text),
            "gross_amount": extract_amount(text),
            # ...
        }
```

#### **Option 2: Node.js + TypeScript (Alternative)**

**Pourquoi Node.js ?**
- ✅ Même langage frontend/backend
- ✅ Écosystème npm riche
- ✅ Performance excellente
- ⚠️ Moins adapté pour ML/PDF lourd

**Stack**: Express/Fastify + Prisma + PostgreSQL

### 4.2 Base de Données

#### **PostgreSQL** (Recommandé)

**Schema Proposé**:
```sql
-- Users & Authentication
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    tax_id VARCHAR(50), -- NIF/AVS
    address TEXT,
    country_code CHAR(2) DEFAULT 'CH',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Dividend Cases
CREATE TABLE dividend_cases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    security_name VARCHAR(255) NOT NULL,
    isin CHAR(12) NOT NULL,
    gross_amount DECIMAL(10, 2) NOT NULL,
    currency CHAR(3) DEFAULT 'EUR',
    payment_date DATE NOT NULL,
    withholding_tax DECIMAL(10, 2),
    reclaimable_amount DECIMAL(10, 2),
    status VARCHAR(20) DEFAULT 'pending', -- pending, submitted, approved, refunded
    created_at TIMESTAMP DEFAULT NOW()
);

-- Generated Forms
CREATE TABLE generated_forms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id UUID REFERENCES dividend_cases(id) ON DELETE CASCADE,
    form_type VARCHAR(10), -- '5000' or '5001'
    file_url TEXT, -- S3/Storage URL
    generated_at TIMESTAMP DEFAULT NOW()
);

-- Audit Log
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    action VARCHAR(50),
    resource_type VARCHAR(50),
    resource_id UUID,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_cases_user ON dividend_cases(user_id);
CREATE INDEX idx_cases_status ON dividend_cases(status);
CREATE INDEX idx_forms_case ON generated_forms(case_id);
```

### 4.3 Déploiement & Infrastructure

#### **Option A: Firebase/Google Cloud (Recommandé pour MVP)** ⭐

**Architecture Firebase**:
```
Frontend:
├── Firebase Hosting (CDN global)
├── Firebase Authentication (Email + Google OAuth)
└── Firebase Analytics

Backend:
├── Cloud Run (FastAPI containerisé)
│   └── Auto-scaling 0-N instances
├── Cloud SQL (PostgreSQL)
├── Cloud Storage (PDFs, Forms)
└── Cloud Functions (Webhooks, Async tasks)

Monitoring:
├── Cloud Logging
├── Cloud Monitoring
└── Error Reporting
```

**Coûts Estimés (MVP avec 100 utilisateurs/mois)**:
- Hosting: Gratuit (10 GB/mois)
- Authentication: Gratuit (50k MAU)
- Cloud Run: ~$10-20/mois (pay-per-request)
- Cloud SQL: ~$25/mois (db-f1-micro)
- Storage: ~$2/mois (100 GB)
- **Total: ~$40-50/mois**

**Setup Firebase**:
```bash
# Installation
npm install -g firebase-tools
firebase login
firebase init

# firebase.json
{
  "hosting": {
    "public": "frontend/dist",
    "ignore": ["firebase.json", "**/.*", "**/node_modules/**"],
    "rewrites": [
      {
        "source": "**",
        "destination": "/index.html"
      }
    ]
  }
}

# Déploiement
cd frontend && npm run build
firebase deploy --only hosting
```

**Dockerfile Backend**:
```dockerfile
# backend/Dockerfile
FROM python:3.11-slim

WORKDIR /app

# Install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy app
COPY ./app ./app

# Run
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8080"]
```

**Cloud Run Deployment**:
```bash
# Build & Push
gcloud builds submit --tag gcr.io/PROJECT_ID/tax-dividend-api

# Deploy
gcloud run deploy tax-dividend-api \
  --image gcr.io/PROJECT_ID/tax-dividend-api \
  --platform managed \
  --region europe-west1 \
  --allow-unauthenticated \
  --set-env-vars DATABASE_URL=postgresql://...
```

#### **Option B: Docker Compose (Dev Local)**

```yaml
# docker-compose.yml
version: '3.8'

services:
  frontend:
    build: ./frontend
    ports:
      - "5173:5173"
    volumes:
      - ./frontend:/app
      - /app/node_modules
    environment:
      - VITE_API_URL=http://localhost:8000/api

  backend:
    build: ./backend
    ports:
      - "8000:8000"
    volumes:
      - ./backend:/app
    environment:
      - DATABASE_URL=postgresql://user:pass@db:5432/taxdividend
      - REDIS_URL=redis://redis:6379
    depends_on:
      - db
      - redis

  db:
    image: postgres:15-alpine
    ports:
      - "5432:5432"
    environment:
      - POSTGRES_USER=user
      - POSTGRES_PASSWORD=pass
      - POSTGRES_DB=taxdividend
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

volumes:
  postgres_data:
```

**Commandes**:
```bash
# Démarrer tous les services
docker-compose up -d

# Voir les logs
docker-compose logs -f backend

# Reconstruire après modifications
docker-compose up -d --build

# Arrêter
docker-compose down
```

#### **Option C: Kubernetes (Production Scale)**

Pour une production avec >10k utilisateurs:

```yaml
# k8s/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: tax-dividend-api
spec:
  replicas: 3
  selector:
    matchLabels:
      app: tax-dividend-api
  template:
    metadata:
      labels:
        app: tax-dividend-api
    spec:
      containers:
      - name: api
        image: gcr.io/PROJECT/tax-dividend-api:latest
        ports:
        - containerPort: 8080
        env:
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: url
        resources:
          requests:
            cpu: 100m
            memory: 128Mi
          limits:
            cpu: 500m
            memory: 512Mi
        livenessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 30
        readinessProbe:
          httpGet:
            path: /ready
            port: 8080
```

#### **Option D: Podman (Alternative Docker)**

Podman est compatible Docker mais sans daemon (plus sécurisé).

```bash
# Même syntaxe que Docker
podman build -t tax-dividend-api ./backend
podman run -p 8000:8000 tax-dividend-api

# Générer fichiers Kubernetes depuis Compose
podman-compose --file docker-compose.yml up
podman generate kube tax-dividend-api > k8s/deployment.yaml
```

**Avantages Podman**:
- ✅ Rootless par défaut (plus sécurisé)
- ✅ Compatible Kubernetes nativement
- ✅ Pas de daemon (moins de ressources)
- ⚠️ Écosystème moins mature que Docker

### 4.4 CI/CD Pipeline

#### **GitHub Actions** (Recommandé)

```yaml
# .github/workflows/ci-cd.yml
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  test-frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Setup Node
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          cache: 'npm'
          cache-dependency-path: frontend/package-lock.json

      - name: Install dependencies
        run: cd frontend && npm ci

      - name: Lint
        run: cd frontend && npm run lint

      - name: Type check
        run: cd frontend && npm run build

      - name: Run tests
        run: cd frontend && npm test

  test-backend:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: postgres
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
    steps:
      - uses: actions/checkout@v3

      - name: Setup Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.11'
          cache: 'pip'

      - name: Install dependencies
        run: cd backend && pip install -r requirements.txt

      - name: Run tests
        run: cd backend && pytest

      - name: Coverage
        run: cd backend && pytest --cov=app --cov-report=xml

  deploy-staging:
    needs: [test-frontend, test-backend]
    if: github.ref == 'refs/heads/develop'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Deploy Frontend to Firebase
        run: |
          npm install -g firebase-tools
          cd frontend && npm ci && npm run build
          firebase deploy --only hosting:staging --token ${{ secrets.FIREBASE_TOKEN }}

      - name: Deploy Backend to Cloud Run
        uses: google-github-actions/deploy-cloudrun@v1
        with:
          service: tax-dividend-api-staging
          image: gcr.io/${{ secrets.GCP_PROJECT }}/api:${{ github.sha }}

  deploy-production:
    needs: [test-frontend, test-backend]
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    environment: production
    steps:
      # Similar to staging but with production settings
```

---

## 5. ROADMAP TECHNIQUE RECOMMANDÉE

### **Phase 1: Stabilisation MVP (2 semaines)**
- [ ] Corriger erreurs ESLint
- [ ] Ajouter validation fichiers
- [ ] Setup testing (Vitest)
- [ ] Écrire tests critiques (auth, upload)
- [ ] Ajouter gestion d'erreurs UI
- [ ] Documentation API

### **Phase 2: Backend Foundation (3-4 semaines)**
- [ ] Setup FastAPI + PostgreSQL
- [ ] Authentification JWT
- [ ] API REST complète
- [ ] Parser PDF réel (pdfplumber)
- [ ] Migration mock → API
- [ ] Tests backend (pytest)

### **Phase 3: Features Core (4 semaines)**
- [ ] Génération Forms 5000/5001 (reportlab/weasyprint)
- [ ] Profil utilisateur complet
- [ ] Calculs fiscaux précis
- [ ] Storage S3/Cloud Storage
- [ ] Email notifications
- [ ] Export CSV/Excel

### **Phase 4: Production Ready (2-3 semaines)**
- [ ] CI/CD GitHub Actions
- [ ] Déploiement Firebase/Cloud Run
- [ ] Monitoring & Logging
- [ ] Backup automatiques
- [ ] Load testing
- [ ] Documentation utilisateur

### **Phase 5: Scale & B2B (6-8 semaines)**
- [ ] Multi-tenancy
- [ ] Batch processing
- [ ] Dashboard admin
- [ ] Billing Stripe
- [ ] API publique
- [ ] Webhooks

---

## 6. RECOMMANDATIONS FINALES

### 🎯 **Priorités Immédiates** (Cette Semaine)

1. **Corriger ESLint** → 30 min
2. **Ajouter validation fichiers** → 1h
3. **Setup Vitest** → 2h
4. **Écrire 5-10 tests basiques** → 4h
5. **Gestion erreurs UI** → 2h

### 🏗️ **Choix Technologiques Recommandés**

| Domaine | Technologie | Justification |
|---------|-------------|---------------|
| Backend | **FastAPI** | ML/PDF, Performance, Typage |
| Database | **PostgreSQL** | Relationnel, ACID, Extensions |
| State | **Zustand** | Simple, Performant, TypeScript |
| Routing | **React Router v6** | Standard, Mature |
| Forms | **React Hook Form + Zod** | Performance, DX |
| API | **React Query** | Cache, Optimistic updates |
| Testing | **Vitest + Testing Library** | Fast, Vite intégré |
| Deploy Frontend | **Firebase Hosting** | CDN, Gratuit, Simple |
| Deploy Backend | **Cloud Run** | Auto-scale, Pay-per-use |
| Storage | **Cloud Storage** | S3-compatible, Intégration |
| CI/CD | **GitHub Actions** | Intégré, Gratuit |

### 💰 **Budget Infrastructure Estimé**

**Année 1 (0-1000 utilisateurs)**:
- Hosting: $0-50/mois
- Cloud Run: $20-100/mois
- Database: $25-50/mois
- Storage: $5-20/mois
- Monitoring: $0-30/mois
- **Total: $50-250/mois** (~€50-230/mois)

**Année 2 (1k-10k utilisateurs)**:
- ~$500-1500/mois (~€460-1380/mois)

### 📊 **Métriques de Succès**

**Technique**:
- ✅ Test coverage > 70%
- ✅ Lighthouse score > 90
- ✅ Temps de build < 60s
- ✅ API latency p95 < 500ms
- ✅ Uptime > 99.5%

**Fonctionnel**:
- ✅ Parsing PDF accuracy > 95%
- ✅ Form generation < 3s
- ✅ User onboarding < 5 min
- ✅ Support 4 langues

---

## 7. CONCLUSION

**État Actuel**: MVP frontend solide mais incomplet pour production

**Forces**:
- Code propre et moderne
- UX/UI de qualité
- Architecture extensible
- i18n native

**Risques**:
- Aucun test
- Pas de backend
- Sécurité non implémentée
- Logique métier simplifiée

**Prochaine Étape Critique**:
Décider entre développement backend Python ou Node.js, puis commencer Phase 1 (Stabilisation) immédiatement.

**Recommandation Finale**:
👉 **FastAPI + PostgreSQL + Firebase/Cloud Run** pour un MVP production-ready en 3 mois.

---

**Questions ?** Besoin de clarifications sur une section spécifique ?
