# Security Hardening Portfolio

## Implemented recommendation

We selected a central policy-validation choke point plus capability-protected financial object construction. This gives the kernel independent control over which account movements represent capture, refund, settlement, reserve, or reversal operations without introducing framework or persistence coupling.

## Next hardening gates

### 1. Authenticated ledger posting port

The future application layer must derive merchant and platform identity from authenticated claims and server-owned payment records. Public JSON must never select ledger account IDs, policy IDs, source types, or merchant scope directly.

### 2. PostgreSQL enforcement

Batch 4 must add unique constraints for posting idempotency and source events, append-only roles, update/delete denial, balanced-entry commit enforcement, deterministic account locks, and Testcontainers concurrency tests. In-memory maps are evidence of semantics, not a durability boundary.

### 3. Durable idempotency lifecycle

The production posting port needs transactional registration of `IN_PROGRESS`, `COMPLETED`, and conflict outcomes. Replays must not depend on process memory or be lost on restart.

### 4. Resource limits and retention

`LedgerKernel` intentionally retains every account, transaction, idempotency key, and source reference. It must never be exposed as a long-running unbounded production store. The database implementation needs retention/index/capacity rules and API request bounds.

### 5. Tamper evidence

The current SHA-256 checksum is a deterministic integrity fingerprint, not cryptographic proof against a database administrator who can rewrite both content and checksum. Stronger tamper evidence would require restricted roles plus an external append-only audit anchor, signed checkpoints, or equivalent controls.

### 6. Observability and incident evidence

Rejected policy, source, scope, idempotency, and reversal attempts should emit sanitized metrics and audit events without leaking sensitive financial payloads.
