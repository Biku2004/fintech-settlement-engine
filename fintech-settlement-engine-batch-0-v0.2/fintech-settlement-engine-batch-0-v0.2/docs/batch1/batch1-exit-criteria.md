# Batch 1 Exit Criteria

## Passed

- [x] Approved Batch 0 v0.2 documentation preserved unchanged.
- [x] Java 25 Maven module structure created.
- [x] Money uses checked integer minor units.
- [x] Exact currency fraction-digit conversion implemented.
- [x] UUIDv7 generator and validation implemented.
- [x] Ledger account taxonomy and owner scope implemented.
- [x] Immutable entry and transaction models implemented.
- [x] Balanced posting validation implemented.
- [x] Deterministic entry order implemented.
- [x] Capture posting policy implemented.
- [x] Settlement posting policy implemented.
- [x] Refund shortfall policy implemented.
- [x] Reserve hold/release policy implemented.
- [x] Full reversal implemented.
- [x] Duplicate reversal rejected.
- [x] Idempotency and source-event deduplication semantics implemented.
- [x] Authoritative balance derivation implemented.
- [x] Future account lock order helper implemented.
- [x] Framework-free domain dependency verified.
- [x] Offline compile and self-test gate repeated five times.
- [x] Maven test source syntax compiled.
- [x] Deferred database/telemetry evidence documented honestly.

## External CI gates still required

- [ ] `mvn clean verify` under JDK 25 with dependency resolution.
- [ ] Actual JUnit/jqwik/ArchUnit engines execute successfully.

## Decision

**Batch 1 domain implementation is complete and may proceed to Batch 2 after the external JDK 25 Maven gate is run in a networked developer or CI environment.**
