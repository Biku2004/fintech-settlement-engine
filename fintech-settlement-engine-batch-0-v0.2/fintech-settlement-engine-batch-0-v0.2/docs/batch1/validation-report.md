# Batch 1 Validation Report

## Verdict

**DOMAIN KERNEL COMPLETE — OFFLINE VERIFICATION PASSED**

Batch 1 implements the Money and Ledger Domain Kernel without introducing persistence or framework coupling.

## Inventory

- Production Java files: 57
- Production Java lines: 1,655
- Test/self-test Java files: 7
- Test/self-test Java lines: 690
- Maven modules: 4 (`money`, `identity`, `ledger-domain`, `ledger-domain-tests`)
- Preserved Batch 0 documentation differences: 0

## Verification evidence

1. Strict production compilation passed.
2. Executable self-test passed with 29 named checks.
3. Seeded randomized verification passed for 2,000 capture postings.
4. Test-source compilation passed for 6 Maven test files.
5. Static architecture and type checks passed.
6. POM XML validation passed.
7. Dependency summary contains only approved shared modules and `java.base`.
8. Five consecutive verification runs passed.

## Scope accuracy

The in-memory `LedgerKernel` proves domain outcomes and concurrency semantics. It is not described as production storage. PostgreSQL atomicity, grants, triggers, jOOQ repositories, and Testcontainers evidence remain deferred to their planned persistence batch.

## Known evidence gap

The active environment provides OpenJDK 21 and no Maven installation or dependency network. Consequently, a real JDK 25 Maven/JUnit/jqwik/ArchUnit run could not be executed here. Test source was nevertheless syntax-compiled against API stubs, and all production code compiled and executed on JDK 21, making it compatible with the Java 25 target at the source-language level.
