# Test Strategy and Traceability v0.2

Tests prove invariants and recovery, not line coverage.

## Tooling decisions

- JUnit 5 for unit/integration orchestration.
- jqwik for generated money, balance, capture/refund, and reversal properties.
- Testcontainers PostgreSQL for constraints, roles, locks, Flyway, and recovery fixtures.
- provider simulator process for network/timeout/callback/query contracts; WireMock only for narrow adapter units.
- ArchUnit plus Spring Modulith verification for boundaries.
- REST Assured or MockMvc for API behaviour; OpenAPI contract tests added with implementation.
- Gatling or k6 in Batch 12; OWASP-oriented security integration in Batch 11.

## Determinism and test data

Use injected `Clock`, deterministic UUIDv7 test generator, seeded property tests, isolated merchant fixtures, no real secrets/card data, and explicit timezone/currency fixtures. Failed random seeds are recorded.

## Layers and gates

Unit/domain, property/invariant, architecture, PostgreSQL repository, API, provider, schema compatibility, end-to-end, failure injection, security, migration, recovery/replay, and load tests are required according to mapped batch.

Batch 1–4 release gates include mapped P/L/S tests, empty/upgrade migrations, architecture verification, lost-response/duplicate callback E2E, ledger balance verification, telemetry redaction, and recovery/replay smoke evidence when executable.

## Naming

Tests include IDs, for example `L001_LedgerTransactionMustBalanceTest` and `FM_CAP_001_LostCaptureResponseTest`.

- `invariant-test-traceability.csv` maps every invariant to a concrete scenario.
- `failure-scenario-traceability.csv` maps crash/ambiguity/replay scenarios.
