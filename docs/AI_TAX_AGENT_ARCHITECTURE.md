# 🤖 AGENT IA AUTONOME - RÈGLES FISCALES

**Date**: 27 Janvier 2026
**Objectif**: Agent IA qui scrape et maintient automatiquement les règles fiscales

---

## 🎯 VISION

### Concept

Un **agent IA autonome** qui :
- 🔍 Surveille les sites officiels (impots.gouv.fr, admin.ch, etc.)
- 📄 Détecte les changements réglementaires
- 🧠 Extrait les nouvelles règles (taux, dates, conditions)
- ✅ Valide les données extraites
- 🔔 Notifie l'équipe pour validation humaine
- 📊 Propose des mises à jour automatiques

### Avantages vs Approche Manuelle

| Critère | Manuel | Agent IA |
|---------|--------|----------|
| **Détection changements** | Humain lit BOI | ✅ Automatique 24/7 |
| **Rapidité** | Semaines | ✅ Heures |
| **Précision extraction** | Erreurs humaines | ✅ Validation multi-sources |
| **Scalabilité** | 1 pays = 1 personne | ✅ 50+ pays en parallèle |
| **Coût** | €50k/an (fiscaliste) | ✅ €5k/an (API + compute) |
| **Historique** | Manuel | ✅ Versioning auto |
| **Validation** | 1 expert | ✅ Multi-sources + expert |

---

## 🏗️ ARCHITECTURE

### Vue d'Ensemble

```
┌─────────────────────────────────────────────────────────────────┐
│                     AI TAX AGENT SYSTEM                          │
└─────────────────────────────────────────────────────────────────┘
                              │
          ┌───────────────────┼───────────────────┐
          │                   │                   │
          ▼                   ▼                   ▼
┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
│  Web Scrapers    │ │  Document Parser │ │  LLM Analyzer    │
│                  │ │                  │ │                  │
│ - impots.gouv.fr │ │ - PDF extraction │ │ - Claude/GPT-4   │
│ - admin.ch       │ │ - HTML parsing   │ │ - Validation     │
│ - EUR-Lex        │ │ - Table extract  │ │ - Summarization  │
└────────┬─────────┘ └────────┬─────────┘ └────────┬─────────┘
         │                    │                    │
         └────────────────────┼────────────────────┘
                              ▼
                    ┌──────────────────┐
                    │  Change Detector │
                    │                  │
                    │ - Diff previous  │
                    │ - Classify       │
                    │ - Priority score │
                    └────────┬─────────┘
                             ▼
                    ┌──────────────────┐
                    │ Validation Layer │
                    │                  │
                    │ - Multi-source   │
                    │ - Confidence %   │
                    │ - Human review   │
                    └────────┬─────────┘
                             ▼
                    ┌──────────────────┐
                    │  Tax Rules DB    │
                    │                  │
                    │ - Pending rules  │
                    │ - Approved rules │
                    │ - Audit log      │
                    └──────────────────┘
```

---

## 🔧 COMPOSANTS TECHNIQUES

### 1. Web Scrapers (Surveillance Sites)

#### Sources Officielles

**France**:
```python
SOURCES = {
    "france": [
        {
            "name": "BOFIP",
            "url": "https://bofip.impots.gouv.fr/",
            "type": "documentation",
            "check_frequency": "daily",
            "selectors": {
                "updates": ".bofip-updates",
                "articles": ".bofip-article"
            }
        },
        {
            "name": "Légifrance",
            "url": "https://www.legifrance.gouv.fr/",
            "type": "legal",
            "check_frequency": "daily",
            "search_keywords": [
                "dividendes",
                "retenue à la source",
                "convention fiscale"
            ]
        },
        {
            "name": "Loi de Finances",
            "url": "https://www.impots.gouv.fr/loi-de-finances",
            "type": "annual",
            "check_frequency": "weekly",
            "active_months": [11, 12, 1]  # Nov-Dec-Jan
        }
    ],
    "switzerland": [
        {
            "name": "Admin.ch",
            "url": "https://www.admin.ch/gov/fr/accueil.html",
            "type": "government",
            "check_frequency": "daily"
        }
    ],
    "eu": [
        {
            "name": "EUR-Lex",
            "url": "https://eur-lex.europa.eu/",
            "type": "directives",
            "check_frequency": "weekly"
        }
    ]
}
```

#### Scraper Implementation

```python
# backend/ai-agent/scrapers/base_scraper.py

from abc import ABC, abstractmethod
from typing import List, Dict
import httpx
from bs4 import BeautifulSoup
from datetime import datetime

class BaseScraper(ABC):
    def __init__(self, config: Dict):
        self.config = config
        self.name = config['name']
        self.url = config['url']

    async def scrape(self) -> List[Dict]:
        """Main scraping method"""
        try:
            # 1. Fetch page
            html = await self._fetch_page()

            # 2. Parse content
            soup = BeautifulSoup(html, 'html.parser')

            # 3. Extract updates
            updates = self._extract_updates(soup)

            # 4. Filter new only
            new_updates = self._filter_new(updates)

            return new_updates

        except Exception as e:
            logger.error(f"Scraping failed for {self.name}: {e}")
            return []

    async def _fetch_page(self) -> str:
        """Fetch with retries and rate limiting"""
        async with httpx.AsyncClient() as client:
            response = await client.get(
                self.url,
                headers={'User-Agent': 'TaxDividendAI-Bot/1.0'},
                timeout=30
            )
            response.raise_for_status()
            return response.text

    @abstractmethod
    def _extract_updates(self, soup: BeautifulSoup) -> List[Dict]:
        """Specific extraction logic per source"""
        pass

    def _filter_new(self, updates: List[Dict]) -> List[Dict]:
        """Filter out already processed"""
        # Check against DB
        processed_ids = set(db.get_processed_update_ids())
        return [u for u in updates if u['id'] not in processed_ids]


# backend/ai-agent/scrapers/bofip_scraper.py

class BOFIPScraper(BaseScraper):

    def _extract_updates(self, soup: BeautifulSoup) -> List[Dict]:
        updates = []

        # Find update section
        update_section = soup.select_one('.bofip-updates')
        if not update_section:
            return updates

        # Extract each update
        for item in update_section.select('.update-item'):
            update = {
                'id': item.get('data-id'),
                'title': item.select_one('.title').text.strip(),
                'date': self._parse_date(item.select_one('.date').text),
                'url': item.select_one('a')['href'],
                'category': item.get('data-category'),
                'summary': item.select_one('.summary').text.strip()
            }

            # Check if relevant
            if self._is_relevant(update):
                updates.append(update)

        return updates

    def _is_relevant(self, update: Dict) -> bool:
        """Check if update is about dividends/withholding tax"""
        keywords = [
            'dividende',
            'retenue à la source',
            'prélèvement forfaitaire',
            'convention fiscale',
            'PFU'
        ]

        text = (update['title'] + ' ' + update['summary']).lower()
        return any(kw.lower() in text for kw in keywords)
```

---

### 2. Document Parser (Extraction Données)

#### PDF/HTML Extraction

```python
# backend/ai-agent/parsers/document_parser.py

from typing import Dict, Optional
import pdfplumber
import re
from decimal import Decimal

class TaxDocumentParser:

    def parse_document(self, url: str, content: bytes) -> Dict:
        """Parse document and extract tax rules"""

        if url.endswith('.pdf'):
            return self._parse_pdf(content)
        else:
            return self._parse_html(content)

    def _parse_pdf(self, content: bytes) -> Dict:
        """Extract from PDF (BOI, Loi de Finances)"""
        with pdfplumber.open(io.BytesIO(content)) as pdf:
            full_text = ""
            tables = []

            for page in pdf.pages:
                full_text += page.extract_text()
                tables.extend(page.extract_tables())

            # Extract structured data
            return self._extract_rules(full_text, tables)

    def _extract_rules(self, text: str, tables: List) -> Dict:
        """Extract tax rates, dates, conditions"""

        rules = {
            'rates': self._extract_rates(text, tables),
            'dates': self._extract_dates(text),
            'conditions': self._extract_conditions(text),
            'references': self._extract_references(text)
        }

        return rules

    def _extract_rates(self, text: str, tables: List) -> List[Dict]:
        """Extract tax rates using regex + tables"""
        rates = []

        # Pattern 1: "taux de X%" or "taux : X%"
        pattern1 = r'taux\s*(?:de|:)?\s*(\d+(?:[.,]\d+)?)\s*%'
        matches = re.finditer(pattern1, text, re.IGNORECASE)

        for match in matches:
            rate_str = match.group(1).replace(',', '.')
            rate = Decimal(rate_str) / 100

            # Context around match
            context_start = max(0, match.start() - 200)
            context_end = min(len(text), match.end() + 200)
            context = text[context_start:context_end]

            rates.append({
                'value': rate,
                'context': context,
                'confidence': 0.8
            })

        # Pattern 2: From tables
        for table in tables:
            if self._looks_like_rate_table(table):
                rates.extend(self._extract_from_table(table))

        return rates

    def _extract_dates(self, text: str) -> List[Dict]:
        """Extract effective dates"""
        dates = []

        # "à compter du DD/MM/YYYY"
        pattern = r'(?:à compter du|à partir du|depuis le)\s*(\d{1,2}[/\-]\d{1,2}[/\-]\d{4})'
        matches = re.finditer(pattern, text, re.IGNORECASE)

        for match in matches:
            date_str = match.group(1)
            date = self._parse_date(date_str)

            dates.append({
                'date': date,
                'context': text[match.start()-100:match.end()+100],
                'type': 'effective_from'
            })

        return dates
```

---

### 3. LLM Analyzer (Validation Intelligence)

#### Utilisation Claude/GPT-4

```python
# backend/ai-agent/llm/analyzer.py

from anthropic import Anthropic
from typing import Dict, List

class TaxRuleLLMAnalyzer:

    def __init__(self):
        self.client = Anthropic(api_key=settings.ANTHROPIC_API_KEY)

    async def analyze_update(self, document: Dict) -> Dict:
        """Analyze document with Claude"""

        prompt = self._build_prompt(document)

        response = await self.client.messages.create(
            model="claude-3-5-sonnet-20241022",
            max_tokens=4000,
            temperature=0,  # Deterministic for rules
            system="""You are a tax law expert specializing in France-Switzerland
            cross-border taxation. Extract tax rules from official documents with
            precision. Always cite sources and express confidence levels.""",
            messages=[
                {"role": "user", "content": prompt}
            ]
        )

        # Parse structured output
        return self._parse_llm_response(response.content[0].text)

    def _build_prompt(self, document: Dict) -> str:
        return f"""
Analyze this official tax document and extract any changes to withholding tax rules.

Document:
Title: {document['title']}
Date: {document['date']}
Source: {document['url']}
Content:
{document['content']}

Extract the following information in JSON format:

{{
  "changes_detected": boolean,
  "rules": [
    {{
      "type": "WITHHOLDING_RATE" | "TREATY_RATE" | "EXEMPTION",
      "country_source": "FR" | "DE" | etc.,
      "country_residence": "CH",
      "rate": decimal,
      "effective_from": "YYYY-MM-DD",
      "effective_until": "YYYY-MM-DD" | null,
      "account_type": "CTO" | "PEA" | null,
      "tax_option": "PFU" | "PROGRESSIVE" | null,
      "conditions": string[],
      "source_article": string,
      "confidence": 0-1
    }}
  ],
  "summary": string,
  "requires_human_review": boolean,
  "reasoning": string
}}

Important:
- Only extract clear, explicit rules
- If ambiguous, set requires_human_review=true
- Cite specific articles/paragraphs
- Express confidence (0-1) based on clarity
"""

    def _parse_llm_response(self, text: str) -> Dict:
        """Parse JSON from LLM response"""
        # Extract JSON block
        import json
        json_match = re.search(r'\{.*\}', text, re.DOTALL)
        if json_match:
            return json.loads(json_match.group())
        else:
            raise ValueError("No JSON found in LLM response")

    async def validate_rule(self, rule: Dict) -> Dict:
        """Cross-check rule against known sources"""

        # 1. Check consistency with existing rules
        existing = db.get_similar_rules(rule)

        # 2. Ask LLM to compare
        prompt = f"""
Compare this proposed new rule with existing rules:

New Rule:
{json.dumps(rule, indent=2)}

Existing Rules:
{json.dumps(existing, indent=2)}

Questions:
1. Is this a genuine change or duplicate?
2. Does it conflict with existing rules?
3. Is the effective date logical?
4. What's your confidence this is correct?

Return JSON:
{{
  "is_duplicate": boolean,
  "conflicts": string[],
  "validation_score": 0-1,
  "recommendations": string[]
}}
"""

        response = await self.client.messages.create(
            model="claude-3-5-sonnet-20241022",
            max_tokens=2000,
            messages=[{"role": "user", "content": prompt}]
        )

        return self._parse_llm_response(response.content[0].text)
```

---

### 4. Change Detector (Détection Modifications)

```python
# backend/ai-agent/detector/change_detector.py

from typing import List, Dict
from datetime import datetime, timedelta

class TaxRuleChangeDetector:

    async def detect_changes(self) -> List[Dict]:
        """Run all scrapers and detect changes"""

        changes = []

        # 1. Scrape all sources
        for source_config in SOURCES['france']:
            scraper = self._get_scraper(source_config)
            updates = await scraper.scrape()

            for update in updates:
                change = await self._process_update(update)
                if change:
                    changes.append(change)

        # 2. Prioritize
        changes = self._prioritize(changes)

        # 3. Store
        await self._store_changes(changes)

        # 4. Notify
        await self._notify_team(changes)

        return changes

    async def _process_update(self, update: Dict) -> Optional[Dict]:
        """Process single update"""

        # 1. Fetch full document
        document = await self._fetch_document(update['url'])

        # 2. Parse
        parsed = parser.parse_document(update['url'], document)

        # 3. Analyze with LLM
        analysis = await llm_analyzer.analyze_update({
            **update,
            **parsed
        })

        if not analysis['changes_detected']:
            return None

        # 4. Validate
        validation = await llm_analyzer.validate_rule(analysis['rules'][0])

        # 5. Create change object
        change = {
            'id': generate_id(),
            'detected_at': datetime.now(),
            'source': update['url'],
            'rules': analysis['rules'],
            'validation': validation,
            'requires_human_review': (
                analysis['requires_human_review'] or
                validation['validation_score'] < 0.8
            ),
            'priority': self._calculate_priority(analysis, validation)
        }

        return change

    def _calculate_priority(self, analysis: Dict, validation: Dict) -> int:
        """Priority score (1-10)"""
        score = 5

        # High impact rules
        if 'WITHHOLDING_RATE' in str(analysis):
            score += 2

        # High confidence
        if validation['validation_score'] > 0.9:
            score += 2

        # Effective soon
        for rule in analysis['rules']:
            if rule['effective_from']:
                days_until = (rule['effective_from'] - datetime.now().date()).days
                if days_until < 30:
                    score += 2

        return min(10, score)

    async def _notify_team(self, changes: List[Dict]):
        """Notify via Slack/Email"""

        high_priority = [c for c in changes if c['priority'] >= 8]

        if high_priority:
            await slack_client.send_message(
                channel='#tax-rules-alerts',
                text=f"🚨 {len(high_priority)} high-priority tax rule changes detected!",
                blocks=[
                    {
                        "type": "section",
                        "text": {
                            "type": "mrkdwn",
                            "text": self._format_change(c)
                        },
                        "accessory": {
                            "type": "button",
                            "text": {"type": "plain_text", "text": "Review"},
                            "url": f"https://admin.taxdividend.ai/rules/review/{c['id']}"
                        }
                    }
                    for c in high_priority
                ]
            )
```

---

### 5. Validation Layer (Multi-Sources)

```python
# backend/ai-agent/validation/multi_source_validator.py

class MultiSourceValidator:

    async def validate_rule(self, rule: Dict) -> Dict:
        """Cross-check rule against multiple sources"""

        validations = []

        # 1. Source 1: BOFIP
        bofip_result = await self._check_bofip(rule)
        validations.append(bofip_result)

        # 2. Source 2: Légifrance
        legifrance_result = await self._check_legifrance(rule)
        validations.append(legifrance_result)

        # 3. Source 3: Historical consistency
        historical_result = self._check_historical(rule)
        validations.append(historical_result)

        # 4. Source 4: LLM cross-check
        llm_result = await llm_analyzer.validate_rule(rule)
        validations.append(llm_result)

        # 5. Aggregate scores
        aggregate_score = sum(v['score'] for v in validations) / len(validations)

        return {
            'rule': rule,
            'validations': validations,
            'aggregate_score': aggregate_score,
            'requires_human_review': aggregate_score < 0.85,
            'recommendation': self._get_recommendation(aggregate_score)
        }

    def _get_recommendation(self, score: float) -> str:
        if score >= 0.95:
            return "AUTO_APPROVE"
        elif score >= 0.85:
            return "APPROVE_WITH_NOTIFICATION"
        elif score >= 0.7:
            return "HUMAN_REVIEW_REQUIRED"
        else:
            return "REJECT"
```

---

## 🎯 WORKFLOW COMPLET

### Scénario: Loi de Finances 2025 Change PFU

```
┌────────────────────────────────────────────────────────────┐
│ 1. DÉTECTION (Automated)                                   │
├────────────────────────────────────────────────────────────┤
│ Date: 2024-12-15                                           │
│ Agent scrapes impots.gouv.fr                               │
│ Détecte: "Loi de Finances 2025 - Article 12"              │
│ → New document published                                   │
└────────────────────────────────────────────────────────────┘
                        ↓
┌────────────────────────────────────────────────────────────┐
│ 2. EXTRACTION (LLM)                                        │
├────────────────────────────────────────────────────────────┤
│ Claude extracts:                                           │
│   - "PFU rate changes from 12.8% to 13%"                  │
│   - "Effective from 2025-01-01"                           │
│   - Source: Article 12, paragraph 3                       │
│   - Confidence: 0.92                                      │
└────────────────────────────────────────────────────────────┘
                        ↓
┌────────────────────────────────────────────────────────────┐
│ 3. VALIDATION (Multi-Source)                              │
├────────────────────────────────────────────────────────────┤
│ Cross-check:                                               │
│   ✓ BOFIP update confirms (0.95)                          │
│   ✓ Légifrance text matches (0.90)                        │
│   ✓ Historical trend plausible (0.88)                     │
│   ✓ LLM re-validation (0.93)                              │
│ → Aggregate score: 0.915                                   │
└────────────────────────────────────────────────────────────┘
                        ↓
┌────────────────────────────────────────────────────────────┐
│ 4. HUMAN REVIEW (Notification)                            │
├────────────────────────────────────────────────────────────┤
│ Slack message to #tax-rules:                              │
│ "🔔 Tax rule change detected (Priority: 9/10)"            │
│                                                            │
│ PFU Rate Change:                                           │
│ • Old: 12.8%                                              │
│ • New: 13%                                                │
│ • Effective: 2025-01-01                                   │
│ • Confidence: 91.5%                                       │
│ • Impact: ~2,345 users                                    │
│                                                            │
│ [Review in Admin] [Auto-Approve] [Reject]                 │
└────────────────────────────────────────────────────────────┘
                        ↓
┌────────────────────────────────────────────────────────────┐
│ 5. APPROVAL (Human Click)                                 │
├────────────────────────────────────────────────────────────┤
│ Admin reviews in dashboard:                               │
│ • Sees full document excerpt                              │
│ • Checks validation scores                                │
│ • Views impact analysis                                   │
│ • Clicks "Approve"                                        │
└────────────────────────────────────────────────────────────┘
                        ↓
┌────────────────────────────────────────────────────────────┐
│ 6. DEPLOYMENT (Automatic)                                 │
├────────────────────────────────────────────────────────────┤
│ System:                                                    │
│ 1. Creates new tax_rules entry                            │
│ 2. Sets effective_from = 2025-01-01                       │
│ 3. Updates old rule effective_until = 2024-12-31          │
│ 4. Clears cache                                           │
│ 5. Logs audit trail                                       │
│ → LIVE on 2025-01-01 00:00:00                             │
└────────────────────────────────────────────────────────────┘
```

---

## 📊 DASHBOARD ADMIN

### UI pour Validation Humaine

```typescript
// Admin Dashboard - Pending Rules Review

interface PendingRuleReview {
  id: string;
  detectedAt: Date;
  priority: number;
  source: string;
  rule: {
    type: string;
    rate: number;
    effectiveFrom: Date;
    conditions: string[];
  };
  validation: {
    aggregateScore: number;
    sources: {
      name: string;
      score: number;
      status: 'confirmed' | 'uncertain' | 'conflicting';
    }[];
  };
  impact: {
    affectedUsers: number;
    estimatedReclaimChange: number;
  };
  documentExcerpt: string;
  llmReasoning: string;
}
```

**UI Components**:
```tsx
<PendingRuleCard>
  <Header priority={rule.priority}>
    🔔 Tax Rule Change Detected
  </Header>

  <RulePreview>
    <Badge>{rule.rule.type}</Badge>
    <RateChange
      old={currentRule.rate}
      new={rule.rule.rate}
      effectiveFrom={rule.rule.effectiveFrom}
    />
  </RulePreview>

  <ValidationScores>
    <ScoreBar label="BOFIP" score={0.95} status="confirmed" />
    <ScoreBar label="Légifrance" score={0.90} status="confirmed" />
    <ScoreBar label="Historical" score={0.88} status="confirmed" />
    <ScoreBar label="LLM" score={0.93} status="confirmed" />
    <OverallScore score={0.915} />
  </ValidationScores>

  <ImpactAnalysis>
    <Metric label="Affected Users" value={2345} />
    <Metric label="Avg Impact" value="+€5.23/user" />
  </ImpactAnalysis>

  <DocumentExcerpt collapsible>
    {rule.documentExcerpt}
    <Link href={rule.source}>View Full Document →</Link>
  </DocumentExcerpt>

  <Actions>
    <Button variant="success" onClick={handleApprove}>
      ✓ Approve & Deploy
    </Button>
    <Button variant="warning" onClick={handleModify}>
      ✏️ Modify
    </Button>
    <Button variant="danger" onClick={handleReject}>
      ✗ Reject
    </Button>
  </Actions>
</PendingRuleCard>
```

---

## 💰 COÛTS ESTIMÉS

### Infrastructure

| Composant | Service | Coût/Mois |
|-----------|---------|-----------|
| **Scraping** | Cloud Run (scheduled) | €10 |
| **LLM** | Claude API (~1M tokens/mois) | €100 |
| **Storage** | PostgreSQL + documents | €25 |
| **Monitoring** | Sentry + logging | €20 |
| **Total** | | **€155/mois** |

### Comparaison

| Approche | Coût/An | Couverture | Réactivité |
|----------|---------|------------|------------|
| **Fiscaliste humain** | €50,000 | 1 pays | Semaines |
| **Agent IA** | €1,860 | 50+ pays | Heures |
| **Économie** | **€48,140** | **50x** | **100x** |

---

## 🚨 RISQUES & MITIGATIONS

### Risques Identifiés

| Risque | Probabilité | Impact | Mitigation |
|--------|-------------|--------|------------|
| **LLM hallucination** | Moyenne | Critique | Multi-source validation + human review |
| **Scraping bloqué** | Faible | Élevé | Rotation IPs, respecter robots.txt |
| **Faux positifs** | Moyenne | Moyen | Confidence threshold >85% |
| **Changements non détectés** | Faible | Critique | Multiple sources + newsletter officielle |
| **Compliance legal** | Faible | Critique | Disclaimer, human validation finale |

### Garde-Fous

1. **Human-in-the-Loop**: Aucune règle appliquée sans validation humaine
2. **Confidence Threshold**: <85% → rejet automatique
3. **Multi-Source**: Minimum 3 sources confirment
4. **Audit Trail**: Tous les changements tracés
5. **Rollback**: 1-click rollback si erreur

---

## 📅 ROADMAP IMPLÉMENTATION

### Phase 1: MVP Agent (4 semaines)

**Semaine 1-2: Scrapers**
- [ ] Scraper BOFIP
- [ ] Scraper Légifrance
- [ ] Change detector
- [ ] Database schema
- [ ] Cron job scheduler

**Semaine 3: LLM Integration**
- [ ] Claude API integration
- [ ] Prompt engineering
- [ ] JSON parsing
- [ ] Confidence scoring

**Semaine 4: Admin UI**
- [ ] Pending rules dashboard
- [ ] Approval workflow
- [ ] Manual rule creation
- [ ] Audit logs

### Phase 2: Multi-Source Validation (2 semaines)

- [ ] Multi-source validator
- [ ] Aggregate scoring
- [ ] Historical consistency check
- [ ] Impact analysis

### Phase 3: Automation (2 semaines)

- [ ] Auto-approval (>95% confidence)
- [ ] Notification system (Slack)
- [ ] Email alerts
- [ ] Monitoring dashboard

### Phase 4: Scale (4 semaines)

- [ ] Add Germany sources
- [ ] Add Italy sources
- [ ] Add Belgium sources
- [ ] Multi-language support

**Total: 12 semaines** pour système complet

---

## 🎯 DÉCISION REQUISE

### Comparaison Approches

| Approche | Coût | Délai | Scalabilité | Risque |
|----------|------|-------|-------------|--------|
| **Option 1: Manuel** | €50k/an | Semaines | Faible | Bas |
| **Option 2: DB + Admin** | €3k/an | Jours | Moyenne | Bas |
| **Option 3: Agent IA** | €2k/an | Heures | Haute | Moyen |

### Recommandation Hybride ⭐

```
Phase MVP (Maintenant):
└─ Option 2: DB + Admin Panel
   → Stable, contrôlé, production-ready

Phase 2 (Mois 6-12):
└─ Option 3: Agent IA en parallèle
   → Beta testing, validation humaine systématique
   → Suggère changements, n'applique pas

Phase 3 (Année 2):
└─ Option 3: Agent IA autonome
   → Auto-approval rules >95% confidence
   → Multi-pays (50+ couvert)
```

---

## 📚 RESSOURCES

### Technologies

- **Web Scraping**: Playwright, BeautifulSoup, httpx
- **LLM**: Claude API, OpenAI API
- **PDF Parsing**: pdfplumber, PyPDF2
- **Scheduling**: Celery, APScheduler
- **Monitoring**: Sentry, Prometheus

### Sources Officielles

- [BOFIP](https://bofip.impots.gouv.fr/)
- [Légifrance](https://www.legifrance.gouv.fr/)
- [Admin.ch](https://www.admin.ch/)
- [EUR-Lex](https://eur-lex.europa.eu/)

### Compliance

- Respecter robots.txt
- Rate limiting (max 1 req/sec)
- User-Agent identifiable
- Opt-out option pour sites
- GDPR compliance (données publiques seulement)

---

## ✅ CONCLUSION

### Faisabilité: ✅ HAUTE

**Technologies matures**:
- Web scraping: Standard
- LLM: Claude/GPT-4 excellent sur textes légaux
- Validation: Multi-source efficace

### ROI: ✅ EXCELLENT

- €48k économie/an vs fiscaliste
- Scalable 50+ pays
- 100x plus rapide

### Risque: 🟡 MOYEN (Mitigable)

- Human-in-the-loop obligatoire
- Multi-source validation
- Audit trail complet

### Recommandation: **GO avec Phase Hybride**

1. **Maintenant**: DB + Admin Panel (stable)
2. **Mois 6**: Agent IA en beta (suggère seulement)
3. **Année 2**: Agent IA autonome (50+ pays)

---

**Prêt à implémenter ?** 🚀

Prochaines étapes:
1. Valider approche hybride
2. Prioriser Phase 1 (DB) ou Phase 2 (Agent IA)
3. Budget alloué pour LLM API
4. Démarrer POC Agent IA (2 semaines)
