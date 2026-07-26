# Validation and Rating

## Ordered verification gates

| Gate | Result | Evidence |
|---|---|---|
| Applicability and strict compilation | Passed | 60 production sources compile with `-Xlint:all,-serial -Werror` |
| Original exploit closure | Passed | post-fix probe rejects forged policy, wrong source type, and foreign platform account; zero transactions |
| Change-aware bypass review | Passed | same-package capability and transaction-constructor probes fail compilation |
| Legitimate behavior preservation | Passed | capture, zero-fee capture, refund, settlement, reserve, reversal, idempotency, balances, snapshots |
| Concurrency/property checks | Passed | 32 concurrent duplicates; 2,000 randomized captures |
| Repeatability | Passed | five full verification runs |
| Maven/JDK 25 engine execution | Not available | environment has OpenJDK 21 and no Maven/dependency network |
| Database enforcement | Deferred | PostgreSQL/jOOQ/Flyway/Testcontainers are outside Batch 1 |

## Score rationale

The after-score is high for a framework-independent domain kernel because the important financial construction and scope boundaries are now centralized and directly tested. It is not a production-readiness score. A real service still needs identity propagation, authorization, durable constraints, immutable database permissions, observability, bounded retention, backups, and recovery evidence.

| Category | Before | After | Principal-engineer assessment |
|---|---:|---:|---|
| Security | 6.4 | 9.1 | All validated Batch 1 integrity bypasses closed; production identity/database controls remain |
| Domain correctness | 8.5 | 9.3 | Balance plus policy/source/scope/reversal rules |
| Architecture | 8.2 | 9.1 | One owned policy gate and one construction capability |
| Testing | 8.4 | 9.2 | Exploit, regression, property, concurrency, and negative compilation evidence |
| Maintainability | 8.6 | 9.0 | Central validator adds explicit rules without framework coupling |
| Production readiness | 4.8 | 6.3 | Improved core, still intentionally in-memory and unauthenticated |
| Batch 1 overall | 8.0 | 9.1 | Ready to serve as the kernel for Batch 2, not ready for financial production |
