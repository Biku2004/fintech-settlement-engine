# Batch 1 Validation Report — v0.3.1

## Verdict

**SECURITY-REVIEWED DOMAIN KERNEL COMPLETE — OFFLINE VERIFICATION PASSED**

Batch 1 implements and hardens the Money and Ledger Domain Kernel without introducing persistence or framework coupling.

## Inventory

- Production Java files: 60
- Production Java lines: 1,989
- Test/self-test Java files: 8
- Test/self-test Java lines: 872
- Maven test-source files: 7
- Maven modules: 4 (`money`, `identity`, `ledger-domain`, `ledger-domain-tests`)
- Validated security findings fixed: 4
- Preserved Batch 0 documentation differences unrelated to Batch 1: 0

## Verification evidence

1. Strict production compilation passed.
2. Executable self-test passed with 33 named checks.
3. Seeded randomized verification passed for 2,000 capture postings.
4. Test-source compilation passed for 7 Maven test files.
5. Original exploit inputs were reproduced against v0.3.0 and rejected after the patch.
6. Negative compilation probes closed direct and split-package construction bypasses.
7. Static architecture, framework-independence, and type checks passed.
8. POM XML and dependency-pin validation passed.
9. Five consecutive complete verification runs passed after the final refactor.

## Security result

The original implementation treated generic balance validation as sufficient authorization for a posting and exposed transaction-construction APIs outside the kernel. v0.3.1 adds policy/source/account-shape validation, platform ownership, bounded reversal input, validated-command proof, and capability-protected private transaction construction.

Detailed evidence is in `docs/security-review/`.

## Scope accuracy

The in-memory `LedgerKernel` proves domain outcomes, construction boundaries, and process-local concurrency semantics. It is not production storage. PostgreSQL atomicity, durable idempotency, grants, triggers, jOOQ repositories, authenticated principal mapping, audit emission, and Testcontainers evidence remain deferred.

## Known evidence gap

The active environment provides OpenJDK 21 and no Maven installation or dependency network. A real JDK 25 Maven/JUnit/jqwik/ArchUnit run could not be executed. Test sources were syntax-compiled against API stubs, while all production code compiled and executed under Java 21 compatibility mode. A real JDK 25 `mvn clean verify` remains a required external gate.
