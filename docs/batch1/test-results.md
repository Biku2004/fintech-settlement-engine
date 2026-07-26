# Batch 1 Test Results — v0.3.1

## Executed verification

Command:

```bash
./scripts/verify-batch1.sh
```

Result: **PASS**, repeated five consecutive times after the security refactor.

### Executed self-tests

- 33 named deterministic checks passed.
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
- Forged policy commands, wrong source types, mixed platform owners, and oversized reversals were rejected before mutation.

### Security exploit and compile gates

- The original exploit probe created three unauthorized transactions against v0.3.0.
- The post-fix probe rejected every runtime exploit input and reported `transactions=0`.
- External factory access failed compilation.
- Same-package construction of `LedgerKernel.Access` failed compilation.
- Same-package direct construction of `LedgerTransaction` failed compilation.

### Compilation and static gates

- 60 production Java files compiled with `-Xlint:all,-serial -Werror`.
- 7 Maven test-source files compiled against offline API stubs.
- POM XML parsing passed for all modules.
- Static checks found no Spring, jOOQ, or JPA dependency in the domain.
- Static checks found no `double` or `float` domain type.
- Static checks require the central policy validator, platform-bound kernel, private transaction constructor, and security review package.
- `jdeps` reports only Java base and approved shared module dependencies.

## Not executed in this environment

- Real Maven dependency resolution.
- JUnit Platform engine execution.
- jqwik engine execution.
- ArchUnit engine execution.
- JDK 25 bytecode compilation.
- PostgreSQL/jOOQ/Flyway/Testcontainers tests.

Those remain explicit CI and persistence gates rather than being represented as passed.
