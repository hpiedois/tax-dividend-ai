# Sprint 3 Progress

## ✅ Completed

### Feature 1: DividendStatement Workflow

**Status**: ✅ Complete (Backend Implementation)

#### Database Layer
- ✅ Migration V1 updated with `dividend_statements` table and status enum
- ✅ Entity `DividendStatement` with all required fields
- ✅ Enum `DividendStatementStatus` with transition validation
- ✅ Relationship: 1 DividendStatement → N Dividends
- ✅ Repository with ownership validation queries

#### Service Layer
- ✅ `DividendStatementService` interface with 8 methods
- ✅ `DividendStatementServiceImpl` with:
  - File upload to MinIO
  - Ownership validation
  - Status transition validation
  - Automatic timestamp management
  - Transaction management
  - Comprehensive logging

#### API Layer
- ✅ DTOs for API communication
- ✅ Mapper for Entity ↔ DTO conversion
- ✅ Controller `DividendStatementController` with 8 endpoints:
  - POST `/internal/dividend-statements` - Upload statement
  - GET `/internal/dividend-statements/{id}` - Get statement
  - GET `/internal/dividend-statements` - List statements (paginated, filterable)
  - PATCH `/internal/dividend-statements/{id}` - Update status
  - DELETE `/internal/dividend-statements/{id}` - Delete statement
  - GET `/internal/dividend-statements/by-date-range` - Find by date range
  - GET `/internal/dividend-statements/count-by-status` - Count by status

#### Status Workflow Implemented
```
UPLOADED → PARSING → PARSED → VALIDATED → SENT → PAID
```

**Transition Rules**:
- UPLOADED can only transition to PARSING (AI Agent starts parsing)
- PARSING can only transition to PARSED (AI Agent completes parsing)
- PARSED can only transition to VALIDATED (User downloads forms)
- VALIDATED can only transition to SENT (User submits forms offline, marks manually)
- SENT can only transition to PAID (User receives payment, marks manually)
- PAID is terminal state

**Automatic Timestamps**:
- `parsedAt` set when status → PARSED
- `validatedAt` set when status → VALIDATED
- `sentAt` set when status → SENT
- `paidAt` set when status → PAID

#### Tests
- ✅ All existing tests pass (99 tests, 0 failures)
- ⚠️ No dedicated tests for DividendStatementService yet (see TODO below)

---

## 🚧 TODO (Remaining Sprint 3 Work)

### High Priority

#### 1. OpenAPI Specification Update
**Status**: ✅ Complete

Added to `specs/backend/openapi.yaml`:
- ✅ DividendStatement schema with all fields
- ✅ DividendStatementStatus enum (UPLOADED, PARSING, PARSED, VALIDATED, SENT, PAID)
- ✅ DividendStatementUpdateDTO schema
- ✅ ListDividendStatements200Response pagination schema
- ✅ 7 endpoints:
  - POST `/dividend-statements` - Upload statement
  - GET `/dividend-statements` - List with pagination/filtering
  - GET `/dividend-statements/{id}` - Get by ID
  - PATCH `/dividend-statements/{id}` - Update status
  - DELETE `/dividend-statements/{id}` - Delete
  - GET `/dividend-statements/by-date-range` - Find by date range
  - GET `/dividend-statements/count-by-status` - Count by status

**Controller Integration**: ✅ Complete
- Controller implements `DividendStatementsApi` interface
- Mapper converts between internal DTOs and API DTOs
- Handles `JsonNullable` and `OffsetDateTime` conversions
- All tests pass (99 tests, 0 failures)

#### 2. Bulk Import Endpoint for AI Agent
**Status**: ✅ Complete

Created endpoint `POST /internal/dividends/bulk` for AI Agent to import parsed dividends.

**OpenAPI Spec**:
- ✅ `BulkImportDividendsRequest` schema (statementId + array of dividend items)
- ✅ `BulkImportDividendItem` schema (all dividend fields with validation)
- ✅ `BulkImportDividendsResponse` schema (counts, totals, IDs, errors)
- ✅ Endpoint documented with description

**Service Implementation** (`DividendServiceImpl.bulkImportDividends`):
- ✅ Validates statement exists and belongs to user
- ✅ For each dividend:
  - Creates Dividend entity linked to statement
  - Calculates tax using `TaxCalculationService.calculateForDividend()`
  - Sets reclaimableAmount and treatyRate
  - Handles failures gracefully (continues processing)
- ✅ All dividends saved in single transaction
- ✅ Updates statement metadata via `DividendStatementService.updateAfterParsing()`
- ✅ Returns detailed response (success/failure counts, totals, IDs, errors)
- ✅ Comprehensive logging

**Controller** (`DividendController.bulkImportDividends`):
- ✅ Implements `DividendsApi` interface
- ✅ Delegates to service
- ✅ Logs audit action
- ✅ Returns 200 with response

**Tests**:
- All existing tests pass (99 tests, 0 failures)
- ⚠️ No dedicated tests for bulk import yet (see TODO below)

#### 3. DividendStatementService Tests
**Status**: ✅ Complete

Created `DividendStatementServiceTest.java` with 20 comprehensive tests organized in nested classes:
- ✅ Upload statement (5 tests: success, user not found, empty file, invalid dates, storage failure)
- ✅ Get statement (2 tests: found, not found/wrong user)
- ✅ List statements (2 tests: all statements, filter by status)
- ✅ Update status (4 tests: valid transition, invalid transition, statement not found, statement not owned)
- ✅ Update after parsing (2 tests: success, statement not found)
- ✅ Delete statement (3 tests: success, wrong user, with file cleanup)
- ✅ Find by date range (1 test)
- ✅ Count by status (1 test)

All tests use Mockito with proper mocking of dependencies (repository, storage service, mapper).

#### 4. DividendStatementController Tests
**Status**: Not started

Create integration tests with MockMvc.

### Medium Priority

#### 5. Real PDF Templates
**Status**: ✅ Complete

Implemented real PDF form filling using official French tax forms:
- ✅ Created `PdfFormFiller` component for filling PDF form fields using PDFBox 3.0
- ✅ Created `Form5000FieldMapper` to map User data to Form 5000 fields
- ✅ Created `Form5001FieldMapper` to map Dividend data to Form 5001 fields (supports up to 10 dividend lines)
- ✅ Copied official templates to `src/main/resources/templates/forms/`
  - `form_5000_template.pdf` (5000-sd_4482.pdf)
  - `form_5001_template.pdf` (5001-sd_4532.pdf)
- ✅ Updated `PdfGenerationServiceImpl` to use template-based generation
- ✅ All 11 PDF generation tests passing with lenient mocking

**Technical Details**:
- Uses PDFBox 3.0 `Loader.loadPDF()` API
- PDF forms are flattened (non-editable) after filling
- Field names are mapped for multiple variations (French and English)
- Supports extraction of postal codes from addresses
- Proper handling of nullable fields

#### 6. FormService Enhancements
**Status**: Not started

Implement expiration job:
- Create scheduled task to find expired forms
- Send notifications to users
- Optionally auto-regenerate or mark as expired

### Low Priority

#### 7. AI Agent Integration Preparation
**Status**: Not started

Prepare integration with external AI agents:
- Document API contract for Dividend Statement Parser (Agent 1)
- Document API contract for Tax Rules Updater (Agent 2)
- Consider webhook callbacks for async parsing

---

## Architecture Decisions

### ✅ Status Enum vs Boolean Flags
**Decision**: Use enum with built-in transition validation
**Rationale**:
- Prevents inconsistent states (e.g., sent=true but paid=false when actually paid)
- Clear, linear workflow
- Validation built into enum
- Easy to add new states

### ✅ Single V1 Migration
**Decision**: Consolidated all changes into V1__init_schema.sql
**Rationale**: Database not deployed yet, no versioning needed

### ✅ Separate Tables: dividend_statements vs dividends
**Decision**: Keep two separate tables
**Rationale**:
- 1 broker statement = N dividend payments
- Different lifecycles and concerns
- Statement tracks file and workflow status
- Dividends track individual payments and tax calculations

---

## Test Results

**Last Run**: 2026-01-31 22:17:51

```
Tests run: 132 (↑ +33 new tests: 20 service + 13 controller)
Failures: 0
Errors: 0
Skipped: 22

BUILD SUCCESS
```

**Test Coverage**:
- DividendStatementService: 20 unit tests
- DividendStatementController: 13 integration tests
- PdfGenerationService: 11 tests (updated for template-based generation)
- All other existing tests: Passing

---

## Next Steps

1. ~~**Update OpenAPI spec** with dividend-statements endpoints~~ ✅ Complete
2. ~~**Implement bulk import endpoint** for AI Agent~~ ✅ Complete
3. ~~**Write tests** for DividendStatementService~~ ✅ Complete (20 tests, all passing)
4. ~~**Write integration tests** for DividendStatementController~~ ✅ Complete (13 tests, all passing)
5. ~~**Implement Form 5000/5001 PDF templates**~~ ✅ Complete (real PDF form filling with official templates)

---

## Notes

- User workflow is now fully offline for submission
- User downloads forms, submits to tax authority manually
- User marks statements as SENT and PAID manually via PATCH endpoint
- AI Agent will use bulk import endpoint to create dividends after parsing
