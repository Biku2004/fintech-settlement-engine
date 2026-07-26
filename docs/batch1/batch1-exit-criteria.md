# Batch 1 Exit Criteria — v0.3.1

## Passed in this package

- [x] Money uses integer minor units and checked arithmetic.
- [x] Currency mismatches, unsupported scales, invalid precision, and overflow are rejected.
- [x] Ledger entries are immutable positive magnitudes.
- [x] Every accepted transaction has debit and credit totals equal.
- [x] Account roles, merchant scopes, and platform scopes are enforced.
- [x] Posting policy identity/version, source type, role/direction shape, and cardinality are validated before mutation.
- [x] Posting idempotency and source-event deduplication are deterministic and thread-safe in the in-memory proof.
- [x] Full reversals are exact inverses; duplicate/self/reversal-of-reversal attempts are rejected.
- [x] Direct factory, capability, and transaction-constructor bypasses fail compilation, including split-package probes.
- [x] Original exploit inputs are rejected with no transaction created.
- [x] 33 deterministic checks, 2,000 randomized postings, and 32 concurrent duplicates pass.
- [x] The complete offline gate passes five consecutive runs.
- [x] Security findings, refactor, residual risk, and ratings are documented.

## Required external CI gate

- [ ] Run `mvn clean verify` using JDK 25 with real JUnit, jqwik, and ArchUnit engines.

## Deferred to persistence/service batches

- [ ] PostgreSQL/jOOQ/Flyway repositories and migrations.
- [ ] Unique constraints, deterministic locks, append-only roles, and mutation-blocking triggers.
- [ ] Durable idempotency and source-event registration across restarts.
- [ ] Authentication and principal-to-merchant/platform authorization.
- [ ] Testcontainers concurrency, rollback, and database-permission tests.
- [ ] Metrics, audit events, bounded retention, recovery, and operational evidence.

## Decision

Batch 1 is accepted as a security-reviewed domain kernel. It is not accepted as a production financial service.
