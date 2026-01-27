# Functional Specifications

## 1. Core Tax Concepts (France-Switzerland Context)
### Forms Overview
- **Form 5000 (Attestation of Residence)**: Certifies that the beneficiary is a tax resident of Switzerland. Must be validated by the Swiss cantonal tax authority.
- **Form 5001 (Dividend Liquidation)**: Details specific dividend payments. Attached to Form 5000 to request application of the treaty rate (15%) instead of the French domestic rate (e.g., 25% or 12.8%).

### Mechanisms
1.  **Relief at Source (Amont)**: 
    - Forms provided *before* dividend payment.
    - Payer applies 15% rate immediately.
    - Requires proactive generation based on estimated dividends.
2.  **Tax Refund (Aval)**:
    - Standard procedure for most retail investors.
    - Full tax deducted initially.
    - Forms 5000/5001 submitted *after* payment to reclaim the difference.

## 2. User User Journeys
### A. Individual Investor (B2C)
1.  **Onboarding**: Create account, define tax residence (Swiss).
2.  **Import Data**: Upload PDF statements or connect via Broker API (Swissquote, IBKR).
3.  **Review**: System identifies French dividends and calculates reclaimable tax.
4.  **Generate**: User validates selection; System generates PDF Forms 5000/5001 pre-filled.
5.  **Instruction**: System provides a "What next" checklist (Print -> Send to Cantonal Tax Office -> Send to Payer).

### B. Fiduciary / Pro (B2B)
1.  **Client Management**: Create multiple client profiles (Identity, NIF).
2.  **Portfolio Linking**: Associate portfolios/accounts to clients.
3.  **Batch Processing**: Generate forms for multiple clients/dividends in bulk.
4.  **Status Tracking**: Track status of each claim (Draft, Sent, Refunded).

## 3. Data Requirements
### Beneficiary Entity
- Full Name / Company Name
- Full Address
- Tax Identification Number (NIF/AVS)
- Country of Residence

### Financial Data (Per Dividend)
- Security Name / ISIN
- Gross Amount
- Date of Payment
- Payer Institution (Bank/Broker)
