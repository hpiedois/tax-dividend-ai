# Documentation Archivée

**Date d'archivage**: 28 Janvier 2026

Ce dossier contient les documents obsolètes ou contradictoires qui ont été remplacés par des décisions architecturales actées.

---

## Pourquoi ces docs ont été archivés ?

**Problème**: Trop de documents avec des recommandations contradictoires sur les technologies backend:
- Certains recommandaient **FastAPI (Python)**
- D'autres recommandaient **Spring Boot (Java)**
- D'autres encore proposaient **Node.js/Express**

**Solution**: Décision actée dans `/DECISIONS.md` → **Spring Boot** pour BFF Gateway + Backend Services

---

## Documents Archivés

### Root (/)

| Document | Raison | Remplacement |
|----------|--------|--------------|
| `COMPREHENSIVE_PROJECT_REVIEW.md` (68K) | Redondant avec TECHNICAL_REVIEW.md, obsolète | DECISIONS.md + ARCHITECTURE.md |
| `EXECUTIVE_SUMMARY.md` | Recommandations contradictoires | DECISIONS.md |
| `MULTI_REPO_STRATEGY.md` | Stratégie non retenue pour Phase 0-1 | DECISIONS.md (mono-repo) |
| `PHASE_0_KICKOFF.md` | Plan Phase 0 complété | ACTIONS_COMPLETED.md |
| `PRODUCTION_ROADMAP.md` | Roadmap obsolète | project_definition/4_Roadmap.md |

### Docs (/docs)

| Document | Raison | Remplacement |
|----------|--------|--------------|
| `TECHNICAL_REVIEW.md` (32K) | Recommandait FastAPI, contradictoire | DECISIONS.md (Spring Boot) |
| `BACKEND_MIGRATION_PLAN.md` | Proposait 2 options (Spring Boot OU Node.js) | DECISIONS.md + ARCHITECTURE.md |
| `FRONTEND_IMPROVEMENTS.md` | Suggestions d'amélioration intégrées | ACTIONS_COMPLETED.md |

---

## Documents Conservés (Source de Vérité)

### Racine (/)
- ✅ `DECISIONS.md` - **Toutes les décisions architecturales actées**
- ✅ `CLAUDE.md` - Guide développement pour Claude Code
- ✅ `README.md` - Documentation publique du projet

### Documentation (/docs)
- ✅ `ARCHITECTURE.md` - Architecture technique détaillée (Spring Boot)
- ✅ `ACTIONS_COMPLETED.md` - Historique des actions + prochaines étapes
- ✅ `AI_TAX_AGENT_ARCHITECTURE.md` - Architecture agent IA (Phase future)
- ✅ `TAX_RULES_MANAGEMENT.md` - Gestion règles fiscales (Phase future)

### Spécifications (/docs/project_definition)
- ✅ `1_Business_Case.md`
- ✅ `2_Functional_Specifications.md`
- ✅ `3_Technical_Architecture.md`
- ✅ `4_Roadmap.md`
- ✅ `5_Frontend_Strategy.md`

---

## Si vous avez besoin d'une info dans ces docs archivés

Les informations pertinentes ont été consolidées dans:
1. `/DECISIONS.md` → Décisions techniques actées
2. `/docs/ARCHITECTURE.md` → Architecture complète
3. `/docs/ACTIONS_COMPLETED.md` → Historique + prochaines étapes

Si vous cherchez une information spécifique qui n'est pas dans ces documents, vous pouvez consulter les archives ici.

---

**Note**: Ces documents ne sont PAS supprimés, juste archivés. Ils peuvent être restaurés si nécessaire, mais ne doivent plus être utilisés comme référence.
