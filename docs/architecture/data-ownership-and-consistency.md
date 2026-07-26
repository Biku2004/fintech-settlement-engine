# Data Ownership and Consistency

## Stage 1 PostgreSQL schemas

### `identity`

Owns merchants, users/service principals, roles, and memberships.

### `payment`

Owns payment intents, authorization attempts, capture attempts, refund records, provider events, public idempotency records, payment timeline facts, and payment outbox rows.

### `ledger`

Owns ledgers, accounts, transactions, entries, posting idempotency, reversal links, balance versions, and derived snapshots.

### `settlement`

Later owns policies, cycles, batches, items, payout instructions, attempts, and checkpoints.

### `reconciliation`

Later owns imports, rows, runs, matches, exceptions, and resolution actions.

### `audit`

Owns append-only privileged and security audit records.

## Rules

1. Table ownership is exclusive even when schemas share a database.
2. Repositories are module-private.
3. Cross-module reads use application queries or approved read models.
4. Cross-module writes use typed commands.
5. Reporting read models may denormalize but are never authoritative.
6. Redis, when added, is never authoritative for payment or ledger state.
7. Outbox rows are written in the same transaction as the state change.
8. Publication is at least once; consumers must be idempotent.
9. Event order is required only per aggregate or defined partition key, never globally.
10. Database migrations support rolling compatibility after service extraction.

## Stage 1 consistency decision

Capture success, payment transition, ledger posting, and outbox insertion are atomic because they are local to one deployable and PostgreSQL instance.

## Extraction consequence

If Ledger becomes a separate service, capture acceptance and ledger posting can no longer rely on one database transaction. The design must then use an idempotent ledger command, durable event delivery, explicit pending-posting state, and reconciliation. Extraction is therefore an architecture change, not a folder move.
