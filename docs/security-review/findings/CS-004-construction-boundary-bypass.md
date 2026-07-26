# CS-004 — Direct Transaction Construction Bypassed the Kernel Boundary

## Severity

**Medium.** CWE-749 / CWE-284.

## Original vulnerable path

`LedgerTransactionFactory` had a public constructor and a public `create(PostingCommand)` method. `LedgerTransaction` also had a package-private constructor. Application code could therefore construct transaction objects without kernel account registration, source deduplication, idempotency, exact reversal checks, or the new posting-policy gate. Package-private access was also insufficient as a security boundary on a classpath because split-package code can use the same package name.

Affected original locations:

- `LedgerTransactionFactory.java:19-32`
- `LedgerTransaction.java:22-41`

## Impact

The original in-memory kernel remained authoritative for its own maps, so this route did not directly mutate that kernel. However, it exposed a dangerous financial object-construction primitive to current and future application code. A repository, outbox adapter, serializer, or query path that accepted such an object could persist or publish an unapproved transaction.

## Fix

Transaction construction now requires a `LedgerKernel.Access` capability whose constructor is private and whose instance is never exposed. `LedgerTransaction` has a private constructor. The factory stores the legitimate capability and can create a transaction only from `ValidatedPostingCommand`. Same-package spoofing was explicitly tested and rejected.

Fixed locations:

- `LedgerKernel.java:42-60`
- `LedgerTransactionFactory.java:32-50,123-133`
- `LedgerTransaction.java:23-58`

## Validation

Three negative compilation probes attempt external factory access, same-package capability construction, and same-package direct transaction construction. Each fails at a private access boundary. The security gate would fail the build if any probe compiled.

**Outcome: fixed.**
