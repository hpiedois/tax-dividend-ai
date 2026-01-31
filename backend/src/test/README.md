# Backend Test Suite

> **Coverage Target**: 80%
> **Framework**: JUnit 5 + Mockito + AssertJ

## Test Structure

```
src/test/java/com/taxdividend/backend/
├── service/              - Service layer unit tests (Mockito)
│   ├── TaxCalculationServiceTest.java      ✅ (17 tests)
│   ├── StorageServiceTest.java             ✅ (14 tests)
│   ├── AuditServiceTest.java               ✅ (14 tests)
│   └── PdfGenerationServiceTest.java       ✅ (13 tests)
├── controller/           - REST controller tests (MockMvc)
│   ├── DividendControllerTest.java         ✅ (11 tests)
│   ├── FormControllerTest.java             ✅ (17 tests)
│   ├── TaxRuleControllerTest.java          ✅ (17 tests)
│   └── HealthControllerTest.java           ✅ (16 tests)
├── repository/           - Repository integration tests (Testcontainers)
│   └── TODO
└── config/               - Configuration tests
    └── TODO
```

## Running Tests

### All Tests
```bash
cd backend
./mvnw test
```

### Specific Test Class
```bash
./mvnw test -Dtest=TaxCalculationServiceTest
```

### Single Test Method
```bash
./mvnw test -Dtest=TaxCalculationServiceTest#shouldCalculateReclaimableAmountCorrectly
```

### With Coverage Report
```bash
./mvnw test jacoco:report
# Open target/site/jacoco/index.html
```

## Test Conventions

### Naming
- Test classes: `<ClassName>Test.java`
- Test methods: `should<DoSomething>When<Condition>`
- Display names: Use `@DisplayName` with descriptive sentences

### Structure
All tests follow **AAA pattern**:
```java
@Test
@DisplayName("Should calculate reclaimable amount correctly")
void shouldCalculateReclaimableAmountCorrectly() {
    // Given (Arrange)
    // ... setup test data
    
    // When (Act)
    // ... execute the method under test
    
    // Then (Assert)
    // ... verify the results
}
```

### Assertions
Use **AssertJ** for fluent assertions:
```java
assertThat(result).isNotNull();
assertThat(result.getSuccess()).isTrue();
assertThat(result.getReclaimableAmount())
    .isEqualByComparingTo(new BigDecimal("15.00"));
```

### Mocking
Use **Mockito** annotations:
```java
@Mock
private DividendRepository dividendRepository;

@InjectMocks
private TaxCalculationServiceImpl taxCalculationService;
```

## Test Coverage by Component

| Component | Tests | Status |
|-----------|-------|--------|
| **TaxCalculationService** | 17 | ✅ Complete |
| **StorageService** | 14 | ✅ Complete |
| **AuditService** | 14 | ✅ Complete |
| **PdfGenerationService** | 13 | ✅ Complete |
| **DividendController** | 11 | ✅ Complete |
| **FormController** | 17 | ✅ Complete |
| **TaxRuleController** | 17 | ✅ Complete |
| **HealthController** | 16 | ✅ Complete |
| Repositories (integration) | 0 | ⏳ TODO |

**Current Total**: 119 tests ✅
**Target**: ~150 tests (with repositories)

## Key Test Scenarios

### TaxCalculationService
- ✅ Correct reclaimable amount calculation
- ✅ No treaty found handling
- ✅ Batch calculation with partial failures
- ✅ Zero withholding tax handling
- ✅ Negative amount prevention
- ✅ Decimal precision (2 decimals)

### StorageService
- ✅ File upload to MinIO
- ✅ Bucket creation if not exists
- ✅ Pre-signed URL generation
- ✅ File deletion (single/batch)
- ✅ Unique S3 key generation
- ✅ Filename sanitization

### AuditService
- ✅ Action logging (login, logout, etc.)
- ✅ Security events (failed logins)
- ✅ Rate limiting detection
- ✅ Old logs cleanup
- ✅ User statistics

### PdfGenerationService
- ✅ Form 5000 generation
- ✅ Form 5001 generation
- ✅ Bundle (ZIP) generation
- ✅ Form regeneration
- ✅ Dividend linking
- ✅ Expiration date setting

### DividendController (11 tests)
- ✅ List user's dividends
- ✅ Get dividend by ID (404 handling)
- ✅ Calculate tax (single/batch/all)
- ✅ Date range filtering
- ✅ Unsubmitted dividends
- ✅ User ownership validation
- ✅ Delete dividend with audit

### FormController (17 tests)
- ✅ Generate Form 5000, 5001, BUNDLE
- ✅ List user's forms (with filters)
- ✅ Get form metadata
- ✅ Download form (PDF/ZIP with proper headers)
- ✅ Pre-signed URL generation
- ✅ Regenerate expired form
- ✅ Delete form and storage file
- ✅ Generate for all unsubmitted dividends
- ✅ User ownership validation
- ✅ Error handling (generation failure, storage error)

### TaxRuleController (17 tests)
- ✅ List all tax rules
- ✅ Get rule by ID
- ✅ Find applicable rule
- ✅ Rules between countries
- ✅ Active/expired rules
- ✅ Check if treaty exists
- ✅ Filter by source/residence country
- ✅ Relief at source rules
- ✅ Refund procedure rules
- ✅ Treaty rate details (TreatyRateResponse)
- ✅ Uppercase country code conversion

### HealthController (16 tests)
- ✅ Deep health check (all components)
- ✅ Database health with metrics (user/dividend/form counts)
- ✅ Storage health check (MinIO)
- ✅ Tax rules data check (total/active counts)
- ✅ Services readiness
- ✅ Application info (version, Java version)
- ✅ Liveness probe (Kubernetes)
- ✅ Readiness probe (Kubernetes)
- ✅ Error scenarios (DB down, storage unavailable, no tax rules)
- ✅ 503 status when not ready

## Integration Tests (TODO)

Use **Testcontainers** for repository tests:

```java
@Testcontainers
@SpringBootTest
class DividendRepositoryIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>("postgres:16-alpine");
    
    @Test
    void shouldFindByUserId() {
        // Real database integration test
    }
}
```

## Performance Tests (TODO)

- Response time < 500ms for all endpoints
- Batch calculations < 2s for 100 dividends
- PDF generation < 3s per form

## Best Practices

### ✅ DO
- Write tests before fixing bugs
- Test edge cases (null, empty, negative)
- Use meaningful test data
- Mock external dependencies
- Verify all interactions
- Use `@DisplayName` for readability

### ❌ DON'T
- Test framework code (Spring, JPA)
- Test getters/setters
- Use real external services in unit tests
- Ignore flaky tests
- Copy-paste test code

## Dependencies

```xml
<!-- JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<!-- Mockito -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- AssertJ -->
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- Spring Boot Test -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Testcontainers (for integration tests) -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
```

## Continuous Integration

Tests run automatically on:
- Every commit (pre-commit hook)
- Every pull request
- Before deployment

Minimum 80% coverage required to merge.

---

**Next Steps:**
1. ✅ Service layer tests (DONE)
2. ✅ Controller tests (IN PROGRESS)
3. ⏳ Repository integration tests
4. ⏳ End-to-end API tests
5. ⏳ Performance tests
