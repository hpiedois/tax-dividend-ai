# Tax Dividend AI

**Automate cross-border dividend tax reclaims with AI-powered form generation.**

Tax Dividend AI is a SaaS platform that helps cross-border investors reclaim withholding taxes on dividends. The platform automates the complex process of generating and filing tax reclaim forms, starting with France-Switzerland double taxation treaties.

---

## 🎯 Problem Statement

Cross-border investors face significant challenges in reclaiming withholding taxes:

- **Complexity**: Tax treaty forms (e.g., French Forms 5000/5001) are difficult to understand for individuals
- **Manual Process**: Paper-based workflows, risk of missed deadlines, and calculation errors
- **Broker Limitations**: Mainstream brokers (Swissquote, IBKR) don't support tax reclaims for retail clients
- **Financial Loss**: Without proper reclaim procedures, investors lose up to 10-15% of dividend returns to excess foreign withholding taxes

## 💡 Solution

Tax Dividend AI automates the entire tax reclaim workflow:

1. **Import** dividend statements from broker PDFs or CSV files
2. **Analyze** using AI to extract security details, amounts, and payment dates
3. **Calculate** reclaimable amounts based on double taxation treaties
4. **Generate** pre-filled tax forms (5000/5001) ready for submission
5. **Guide** users through the submission process with step-by-step instructions

### Target Markets

- **B2C**: Individual Swiss investors with French securities
- **B2B**: Fiduciaries, family offices, and wealth managers managing multiple client portfolios

---

## ✨ Features

### Current (MVP)

- ✅ Multi-language support (French, English, German, Italian)
- ✅ Dark mode with glass-morphism design
- ✅ Drag-and-drop PDF upload with file scanning simulation
- ✅ Dividend data extraction and validation
- ✅ Real-time processing progress tracking
- ✅ Dashboard with portfolio statistics
- ✅ Transaction history view
- ✅ Responsive design for mobile and desktop

### Roadmap

- 🔄 Backend API integration
- 🔄 Real PDF parsing with AI/ML models
- 🔄 Form 5000/5001 PDF generation
- 🔄 User authentication and profiles
- 🔄 Multi-client management (B2B)
- 🔄 Status tracking for submitted claims
- 🔄 Support for additional country pairs (Germany-Switzerland, etc.)
- 🔄 Broker API integrations (Swissquote, IBKR)

---

## 🚀 Getting Started

### Prerequisites

- Node.js 18+ and npm
- Modern web browser (Chrome, Firefox, Safari, Edge)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/tax-dividend-ai.git
   cd tax-dividend-ai
   ```

2. **Install dependencies**
   ```bash
   cd frontend
   npm install
   ```

3. **Start the development server**
   ```bash
   npm run dev
   ```

4. **Open your browser**
   Navigate to `http://localhost:5173`

### Building for Production

```bash
cd frontend
npm run build
npm run preview  # Preview production build locally
```

---

## 🛠️ Tech Stack

### Frontend

- **Framework**: React 19.2 with TypeScript 5.9
- **Build Tool**: Vite 7.2 (fast HMR and ES modules)
- **Styling**: Tailwind CSS 3.4 with custom design system
- **Animations**: Framer Motion for smooth transitions
- **i18n**: i18next with browser language detection
- **State**: React hooks + Context API (no external state library)

### Backend (Planned)

- Python FastAPI or Node.js (Express/Fastify)
- PostgreSQL database
- PDF processing: pdfplumber, Tesseract OCR
- AI/ML: OpenAI GPT-4 or custom models for data extraction

---

## 📁 Project Structure

```
tax-dividend-ai/
├── frontend/              # React TypeScript application
│   ├── src/
│   │   ├── components/    # UI components organized by domain
│   │   ├── lib/           # Utilities and mock data
│   │   ├── locales/       # i18n translation files
│   │   └── App.tsx        # Main application component
│   ├── public/            # Static assets
│   └── package.json
├── docs/                  # Business case and specifications
│   ├── project_definition/
│   └── officials/         # Sample French tax forms (PDFs)
└── specs/                 # Future technical specifications
```

---

## 🌍 Tax Domain Context

### France-Switzerland Double Taxation Treaty

The platform currently focuses on the France-Switzerland tax treaty:

- **Standard French withholding**: 12.8% - 25% on dividends
- **Treaty rate**: 15% for Swiss tax residents
- **Reclaimable amount**: Difference between domestic rate and treaty rate

### Forms

- **Form 5000 (Attestation de Résidence)**: Certifies Swiss tax residency (validated by cantonal tax authorities)
- **Form 5001 (Bordereau de Liquidation)**: Lists specific dividend payments for reclaim

### Procedures

1. **Relief at Source (Allègement à la Source)**: Forms submitted before payment → 15% withheld directly
2. **Tax Refund (Remboursement)**: Forms submitted after payment → Reclaim excess withholding (current MVP focus)

---

## 🧪 Development

### Commands

```bash
# Start dev server with hot reload
npm run dev

# Type check and build
npm run build

# Lint code
npm run lint

# Preview production build
npm run preview
```

### Mock Data

The current MVP uses mock data for development:
- **PDF parsing**: Simulated with `lib/mock-parser.ts` (1.5-2.5s delay per file)
- **Historical data**: Sample transactions in `lib/mock-db.ts`
- **French ISINs**: Real company names and ISINs for realistic testing

---

## 📄 Documentation

- **[CLAUDE.md](./CLAUDE.md)**: Development guide for Claude Code AI
- **[Business Case](./docs/project_definition/1_Business_Case.md)**: Market analysis and monetization strategy
- **[Functional Specifications](./docs/project_definition/2_Functional_Specifications.md)**: Detailed feature requirements

---

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- All text must be internationalized (use i18next, no hardcoded strings)
- Support dark mode for all new components
- Use Framer Motion for animations
- Follow the glass-morphism design pattern
- Write TypeScript with strict mode enabled

---

## 📝 License

This project is proprietary software. All rights reserved.

---

## 📧 Contact

For questions, feedback, or business inquiries, please contact:
- **Email**: [your-email@example.com]
- **Website**: [your-website.com]

---

## 🙏 Acknowledgments

- Built with React, Vite, and Tailwind CSS
- Icons by Lucide React
- Animations powered by Framer Motion
- Sample tax forms courtesy of French tax administration (impots.gouv.fr)
