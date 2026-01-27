# Project Roadmap

## Phase 0: Scoping (1-2 Weeks)
- **Goal**: Validate scope and key user flows.
- **Tasks**:
    - Finalize specs for France-Switzerland dividends.
    - Define Persona (Retail vs Fiduciary).
    - Prototype the form generation (manual test with one real case).

## Phase 1: MVP Monolith (4-6 Weeks)
- **Goal**: Functional "Happy Path" for a single user.
- **Deliverables**:
    - Core Spring Boot Backend (Modules: Identity, Import, TaxEngine, PDF).
    - React Frontend: Dashboard, Import CSV, Download PDF.
    - Support for Form 5000 & 5001 generation.
    - Manual Import of Swissquote/IBKR data.

## Phase 2: Microservices & Scale (4-6 Weeks)
- **Goal**: Architecture readiness for complex cases.
- **Deliverables**:
    - Split into microservices (Identity, Portfolio, Tax, Document).
    - API Gateway & Keycloak Auth.
    - Event Bus (RabbitMQ) for async processing.
    - Docker/Kubernetes deployment manifests.

## Phase 3: B2B & Expansion (6-10 Weeks)
- **Goal**: Commercial viability for pros.
- **Deliverables**:
    - Multi-tenant support (Fiduciary Dashboard).
    - API for external partners (OpenAPI).
    - Add 2nd Source Country (e.g., Germany) to prove engine genericity.
    - Bulk operations.
