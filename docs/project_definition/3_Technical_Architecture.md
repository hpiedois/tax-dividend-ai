# Technical Architecture

## 1. System Overview
A modular SaaS application based on a Microservices architecture (evolved from a modular Monolith).
- **Frontend**: React (TypeScript) SPA.
- **Backend**: Spring Boot (Java), split into bounded contexts.
- **Database**: PostgreSQL (Multi-tenant schema).
- **Communication**: REST APIs (Sync) and RabbitMQ/Kafka (Async for events like `DIVIDENDS_IMPORTED`).

## 2. Microservices Breakdown
### 2.1 Identity Service
- **Role**: Manages users, tenants, and tax profiles.
- **Key Entities**: `Tenant`, `AppUser`, `TaxClient` (The fiscal entity).

### 2.2 Portfolio Service
- **Role**: Ingests and stores financial data.
- **Key Entities**: `Broker`, `Security`, `PortfolioAccount`, `DividendEvent`, `DividendPayment`.
- **Features**: PDF/CSV Import parsing.

### 2.3 Tax Engine Service
- **Role**: Core domain logic.
- **Key Entities**: `TaxConvention`, `TaxRules`, `TaxRefundCase` (The "Dossier").
- **Logic**: Applies convention rules (Fr-CH) to calculate refundable amounts.

### 2.4 Document Service
- **Role**: Generates physical documents.
- **Key Entities**: `DocumentTemplate` (PDF forms), `GeneratedDocument`.
- **Logic**: Fills templates with data from the Tax Engine.

## 3. Data Model (Key Tables)
### Identity
- `tenant`: High-level grouping (e.g., Fiduciary Firm).
- `tax_client`: The individual or company for whom tax is reclaimed.

### Financial
- `dividend_event`: A corporate action (Company pays X on date Y).
- `dividend_payment`: The actual receipt by a client (Client received Z, Tax withheld W).

### Fiscal
- `tax_refund_case`: Aggregates multiple payments into a single administrative claim.
- `tax_refund_case_item`: Link between a payment and a case.

## 4. Security
- **Encryption**: TLS in transit, volume encryption at rest. PII (NIF, Address) protection.
- **Tenant Isolation**: Strict logical separation of data (Discriminator column `tenant_id`).
