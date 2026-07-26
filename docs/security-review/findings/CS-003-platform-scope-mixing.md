# CS-003 — Platform Accounts from Different Owners Could Be Mixed

## Severity

**Medium.** CWE-863 / CWE-668.

## Original vulnerable path

Merchant-scoped accounts were checked against `merchantId`, but platform-scoped accounts carried an arbitrary owner UUID and were not bound to a specific ledger-kernel platform. A transaction could therefore combine Processor Clearing from platform A with Platform Fee Revenue or Settlement Cash from platform B while still balancing.

Affected original locations:

- `LedgerKernel.java:48-60` — kernel had no platform identity and registration accepted all platform owners.
- `LedgerTransactionFactory.java:47-77` — merchant ownership checked, platform ownership not checked.

## Reproduction

The original probe registered platform accounts with two different owner IDs and submitted a balanced capture. The kernel returned `CREATED`.

## Impact

In a shared deployment, a faulty or malicious internal caller could cross a platform ownership boundary and create a financially valid-looking cross-scope transaction. This undermines tenant isolation and makes later database partitioning or service extraction unsafe.

## Fix

`LedgerKernel` now requires a `PlatformId`. Platform account registration rejects an owner that does not match it. The transaction factory additionally rejects multiple platform owners inside one posting as defense in depth.

Fixed locations:

- `PlatformId.java`
- `LedgerKernel.java:62-80`
- `LedgerTransactionFactory.java:66,78-80,105-107`

## Validation

The post-fix probe rejects foreign-platform registration with `InvalidLedgerAccountException`, and the transaction count remains zero. Unit and self-tests cover both registration and transaction-shape defenses.

**Outcome: fixed.**
