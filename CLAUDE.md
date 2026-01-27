# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Tax Dividend AI** is a SaaS platform that automates tax reclaim processes for cross-border investors. The target use case is Swiss residents holding French securities who need to reclaim withholding taxes using Forms 5000 (Attestation of Residence) and 5001 (Dividend Liquidation).

The project supports two market segments:
- **B2C**: Individual investors importing broker statements to generate pre-filled tax forms
- **B2B**: Fiduciaries and wealth managers processing reclaims for multiple clients

Currently, the repository contains only the frontend MVP with mock data. The architecture is designed to support future backend integration.

## Development Commands

All commands must be run from the `frontend/` directory:

```bash
# Development server with HMR
npm run dev

# Build for production
npm run build

# Lint the codebase
npm run lint

# Preview production build
npm run preview
```

Note: There is no testing infrastructure configured yet.

## Architecture

### Project Structure

This is a **frontend-only monorepo-ready structure**:
- `/frontend` - React + TypeScript + Vite application
- `/docs` - Business case, functional specs, and official French tax forms (PDFs)
- `/specs` - Empty directory for future specifications

### Tech Stack

- **Build**: Vite 7.2.4 with minimal configuration
- **Framework**: React 19.2.0 with TypeScript 5.9.3 (strict mode)
- **Styling**: Tailwind CSS 3.4.17 with custom brand colors and glass-morphism effects
- **Animations**: Framer Motion for all UI animations
- **i18n**: i18next with browser language detection (supports: FR, EN, DE, IT)
- **No routing library**: State-based view switching in App.tsx
- **No global state manager**: Local component state only

### State Management

All application state lives in `App.tsx` using React hooks:

1. **Authentication**: `isLoggedIn` (boolean, no token/session management)
2. **View Routing**: `currentView` (`'dashboard' | 'history' | 'settings' | 'scan'`)
3. **Scan Workflow**:
   - `scanStep` (`'upload' | 'scanning' | 'result'`)
   - `scanResults` (array of DividendData)
   - `processingCount` (progress tracking)

Global concerns use Context API:
- **Theme**: `ThemeProvider` (persisted to localStorage)
- **i18n**: i18next browser language detector

### Component Organization

```
src/components/
├── auth/           - LoginScreen (mock authentication)
├── layout/         - Shell, ThemeToggle, LanguageSwitcher
├── ui/             - Reusable primitives (Button, Card, Input)
├── views/          - Page-level components (Dashboard, History, Settings)
├── upload/         - File upload workflow (DropZone, ScanningOverlay)
└── theme-provider.tsx
```

**Component Patterns**:
- All UI components use Framer Motion for animations
- Style variants use `clsx` + `twMerge` for Tailwind class merging
- Views receive callbacks as props (e.g., `onStartScan`)
- Full internationalization via `useTranslation()` hook

### Data Flow

**File Processing Pipeline**:
1. User drops PDF files in DropZone
2. `App.handleFilesSelect` sets `scanStep = 'scanning'`
3. For each file: `parseDividendPDF()` from `lib/mock-parser.ts` (1.5-2.5s delay)
4. Mock parser returns simulated `DividendData` (French ISINs and company names)
5. Results aggregated, `scanStep = 'result'`
6. Display summary card and individual result cards

**Mock Data**:
- `lib/mock-parser.ts` - PDF parsing simulation with random dividend data
- `lib/mock-db.ts` - Historical transactions (`MOCK_HISTORY`) and dashboard stats (`MOCK_STATS`)

### Key Interfaces

```typescript
interface DividendData {
  securityName: string;
  isin: string;
  grossAmount: number;
  currency: string;
  paymentDate: string;
  withholdingTax: number;
  reclaimableAmount: number;
}

interface MockCase {
  id: string;
  date: string;
  security: string;
  grossAmount: number;
  reclaimedAmount: number;
  status: 'pending' | 'approved' | 'completed';
}
```

## Configuration Details

### TypeScript

- Strict mode enabled in `tsconfig.app.json`
- No unused locals/parameters allowed
- Bundler module resolution
- Targets ES2022

### Tailwind

Custom extensions in `tailwind.config.js`:
- **Brand colors**: Sky-blue palette (`brand-50` to `brand-900`)
- **Surface colors**: Custom neutral palette for dark mode
- **Fonts**: Outfit (headings), Inter (body)
- **Glass shadow**: `shadow-glass` utility
- **Dark mode**: Class-based strategy

### i18n Setup

Languages are auto-detected from browser/localStorage with English fallback. Translation files are in `src/locales/`:
- `fr.json` (French - primary)
- `en.json` (English)
- `de.json` (German)
- `it.json` (Italian)

Usage: `const { t } = useTranslation()` then `{t('key.path')}`

## Tax Domain Context

The application deals with **France-Switzerland double taxation treaties**:

- **Form 5000**: Attestation of Swiss tax residence (validated by cantonal authorities)
- **Form 5001**: Dividend liquidation details (specific payments to reclaim)
- **Relief at Source**: Forms submitted before dividend payment (15% rate applied)
- **Tax Refund**: Forms submitted after payment to reclaim excess withholding

The current MVP focuses on the **Tax Refund (Aval)** workflow for individual investors.

## Future Integration Points

The architecture supports easy integration of:
- **Backend API**: Replace `mock-parser.ts` with real API calls
- **Routing**: Install React Router and migrate state-based views
- **State Management**: Add Redux/Zustand without restructuring
- **Testing**: Add Vitest + React Testing Library
- **Form Validation**: Add React Hook Form
- **PDF Generation**: Library needed for generating Forms 5000/5001

## Important Notes

- All text content must be internationalized (no hardcoded strings)
- Dark mode support is required for all new components
- Use Framer Motion for any animations to maintain consistency
- Follow the glass-morphism design pattern (backdrop-blur, gradients)
- State-based routing means no URL changes - consider this when adding features
- Mock data uses real French ISINs and company names for realism
