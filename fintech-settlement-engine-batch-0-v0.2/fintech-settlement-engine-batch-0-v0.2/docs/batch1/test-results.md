# Batch 1 Test Results

## Executed verification

Command:

```bash
./scripts/verify-batch1.sh
```

Result: **PASS**, repeated five consecutive times.

### Executed self-tests

- 29 named deterministic checks passed.
- 2,000 seeded randomized capture postings passed debit/credit equality checks.
- 32 concurrent duplicate submissions produced exactly one created transaction.
- Same idempotency key/same effect returned the original transaction.
- Same idempotency key/changed effect was rejected.
- Same source event/same effect returned the original transaction.
- Same source event/changed effect was rejected.
- Full reversal restored all involved account balances to zero.
- A second full reversal was rejected.
- USD, JPY, and KWD boundary conversions were verified.
- UUIDv7 version, variant, and embedded Unix-millisecond timestamp were verified.

### Compilation gates

- 57 production Java files compiled with `-Xlint:all,-serial -Werror`.
- 6 Maven test-source files compiled against offline API stubs.
- POM XML parsing passed for all modules.
- Static checks found no Spring, jOOQ, or JPA dependency in the domain.
- Static checks found no `double` or `float` domain type.
- `jdeps` reports only Java base, shared money, and shared identity dependencies.

## Not executed in this environment

- Real Maven dependency resolution.
- JUnit Platform engine execution.
- jqwik engine execution.
- ArchUnit engine execution.
- JDK 25 bytecode compilation.
- PostgreSQL/jOOQ/Flyway/Testcontainers tests.

Those are explicitly recorded as later CI/persistence gates, not represented as passed.
