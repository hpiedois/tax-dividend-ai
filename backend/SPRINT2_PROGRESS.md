# Sprint 2 - Contract-First Alignment - Progress

**Date**: 2026-01-31
**Status**: Day 1-3 ✅ COMPLETED

---

## ✅ Day 1-3: OpenAPI Spec + Implementation

### 1. OpenAPI Specification Updated

Modified `specs/backend/openapi.yaml` to add **10 missing endpoints**:

#### DividendsApi (3 additions)
- ✅ Enhanced `GET /dividends` with query params:
  - `startDate` (LocalDate) - Filter from date
  - `endDate` (LocalDate) - Filter to date
  - `status` (String enum) - Filter by status (UNSUBMITTED, SUBMITTED, APPROVED, PAID)
- ✅ Added `POST /dividends/{userId}/calculate-all`
  - Recalculate tax for all user dividends
  - Returns `TaxCalculationBatchResultDTO`

#### FormsApi (2 additions)
- ✅ Added `GET /forms/{id}/download-url`
  - Returns pre-signed URL for MinIO/S3 download
  - Query param: `expiresIn` (Integer, default 3600 seconds)
  - Returns: `GetFormDownloadUrl200Response` (new DTO with url + expiresAt)
- ✅ Added `POST /forms/{id}/regenerate`
  - Regenerate expired form (after 30 days)
  - Returns: `GeneratedForm`

#### TaxRulesApi (1 enhancement)
- ✅ Enhanced `GET /tax-rules` with query params:
  - `sourceCountry` (String, pattern: `^[A-Z]{2}$`)
  - `residenceCountry` (String, pattern: `^[A-Z]{2}$`)
  - `active` (Boolean) - Filter active rules only
  - `reliefAtSource` (Boolean) - Filter by relief at source availability
  - `refundProcedure` (Boolean) - Filter by refund procedure availability

### 2. OpenAPI Generation

- ✅ Regenerated interfaces with `mvn clean generate-sources`
- ✅ New DTO created: `GetFormDownloadUrl200Response`
- ✅ All method signatures updated in generated APIs

### 3. Controllers Updated

#### DividendController
- ✅ Enhanced `listDividends` to accept new filter parameters
- ✅ Implemented `calculateAllUserDividends` (from OpenAPI interface)
- ✅ **DELETED** custom endpoints (replaced by OpenAPI methods):
  - `/calculate-all` → replaced by `/dividends/{userId}/calculate-all`
  - `/by-date-range` → replaced by enhanced `/dividends?startDate=&endDate=`
  - `/unsubmitted` → replaced by enhanced `/dividends?status=UNSUBMITTED`

#### FormController
- ✅ Implemented `getFormDownloadUrl` (from OpenAPI interface)
  - Converts seconds to hours for service call
  - Returns DTO with URL + expiration timestamp
- ✅ Implemented `regenerateForm` (from OpenAPI interface)
  - Verifies ownership before regeneration
  - Returns regenerated form metadata
- ✅ **DELETED** custom endpoints (not in spec):
  - `/by-status` → removed (should use `/forms?formType=` filter instead)
  - `/generate-all-unsubmitted` → removed (BFF logic, not backend)

#### TaxRuleController
- ✅ Enhanced `getAllTaxRules` to accept filter parameters
  - Applies country filters (sourceCountry, residenceCountry)
  - Applies active status filter
  - Applies boolean filters (reliefAtSource, refundProcedure)

### 4. Services Updated

#### DividendService Interface
- ✅ Enhanced `listDividends(UUID, Pageable, LocalDate, LocalDate, String)`
- ✅ Kept legacy methods `getDividendsByDateRange` and `getUnsubmittedDividends` for backward compatibility

#### DividendServiceImpl
- ✅ Implemented filter logic:
  - Date range filtering: `findByUserIdAndPaymentDateBetween`
  - Status filtering: `UNSUBMITTED` → `findByUserIdAndFormIsNull`
  - Default: `findByUserId` (all dividends)
- ⚠️ Note: Status filtering for SUBMITTED/APPROVED/PAID requires `form_submissions` table (Sprint 3)

#### TaxRuleService Interface
- ✅ Enhanced `getAllTaxRules(String, String, Boolean, Boolean, Boolean)`
- ✅ Deprecated old filter methods (marked with `@Deprecated`):
  - `getRulesBetweenCountries`
  - `getActiveRules`
  - `getExpiredRules`
  - `hasTaxTreaty`
  - `getRulesBySourceCountry`
  - `getRulesByResidenceCountry`
  - `getRulesWithReliefAtSource`
  - `getRulesWithRefundProcedure`

#### TaxRuleServiceImpl
- ✅ Implemented intelligent filter logic:
  - Priority 1: Both countries → `findBySourceCountryAndResidenceCountry`
  - Priority 2: Source country only → `findBySourceCountry`
  - Priority 3: Residence country only → `findByResidenceCountry`
  - Priority 4: Active status → `findActiveRules` or `findExpiredRules`
  - Priority 5: No filters → `findAll`
  - Additional filters: Stream filtering for `reliefAtSource` and `refundProcedure`

### 5. Compilation

- ✅ **BUILD SUCCESS** - All changes compile successfully

---

## 📊 Summary

| Category | Added | Enhanced | Deleted |
|----------|-------|----------|---------|
| OpenAPI Endpoints | 3 | 2 | 0 |
| DTOs Generated | 1 | 0 | 0 |
| Controller Methods | 3 | 2 | 5 |
| Service Methods | 0 | 2 | 0 |
| **TOTAL** | **7** | **6** | **5** |

---

## 🔄 Next Steps (Sprint 2 Remaining)

### Day 4: Delete Obsolete Code
According to SPRINT2_DECISIONS.md, delete **19 endpoints** marked for removal:

#### HealthController (7 endpoints) - DELETE ALL
- `/health/database` - Use Actuator `/actuator/health` instead
- `/health/storage` - Use Actuator `/actuator/health` instead
- `/health/tax-rules` - Not a health check
- `/health/services` - Use Actuator `/actuator/health/live` and `/ready` instead
- `/health/info` - Use Actuator `/actuator/info` instead
- `/health/database` (metrics) - Use Prometheus metrics
- `/health/database` (failure handling) - Not an endpoint

**Action**: Delete entire `HealthController.java` or remove custom health methods

#### TaxRuleController (9 endpoints) - Already deprecated
The custom filter methods are already marked `@Deprecated` in the service interface.
Can be removed in a future cleanup (Day 4 or after Sprint 2).

#### FormController (3 endpoints) - Already deleted ✅
- `/by-status` - DONE
- `/generate-all-unsubmitted` - DONE

#### DividendController (3 endpoints) - Already deleted ✅
- `/calculate-all` - DONE
- `/by-date-range` - DONE
- `/unsubmitted` - DONE

### Day 5: Fix Tests + Documentation
- Update controller tests with new method signatures
- Verify all endpoint tests pass
- Update `TEST_STATUS.md`
- Update `README.md` with new endpoints

---

## ✅ Blockers Resolved

1. ✅ Service method signatures mismatched - FIXED
2. ✅ Boolean getter method names (`isXxx()` vs `getXxx()`) - FIXED
3. ✅ OpenAPI generation successful
4. ✅ Compilation successful

---

**Ready for Day 4: Delete obsolete code**
