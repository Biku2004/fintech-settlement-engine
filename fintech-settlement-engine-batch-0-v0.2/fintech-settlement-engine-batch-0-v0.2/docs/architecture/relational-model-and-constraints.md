# Relational Model and Constraint Plan

## Schemas

```text
payment   payment intents, attempts, provider events, API idempotency, timeline
ledger    accounts, transactions, entries, reversals, balance snapshots
platform  outbox, leases, configuration versions
audit     privileged and security audit events
```

## Key tables and constraints

### `payment.payment_intent`

- `id uuid primary key`
- `merchant_id uuid not null`
- `amount_minor bigint check (amount_minor > 0)`
- `currency char(3) check (currency ~ '^[A-Z]{3}$')`
- `authorized_amount_minor bigint check (authorized_amount_minor >= 0)`
- `captured_amount_minor bigint check (captured_amount_minor >= 0 and captured_amount_minor <= authorized_amount_minor)`
- `refunded_amount_minor bigint check (refunded_amount_minor >= 0 and refunded_amount_minor <= captured_amount_minor)`
- `state` constrained to the state catalog
- `version bigint not null`
- unknown snapshot fields required together through a composite check
- tenant-safe unique/index keys always begin with `merchant_id`

### Idempotency and attempts

```text
unique (merchant_id, operation_type, idempotency_key)
unique (provider, provider_operation_key)
unique (provider, provider_event_id)
unique (payment_id, operation_type, attempt_number)
```

### Ledger

```text
unique (posting_idempotency_key)
unique (source_type, source_id)
unique (ledger_transaction_id, entry_sequence)
check (entry_amount_minor > 0)
check (direction in ('DEBIT','CREDIT'))
check (reversal_transaction_id <> original_transaction_id)
```

A deferred constraint trigger verifies debit total equals credit total before transaction commit. The runtime role has insert/select only on immutable ledger tables; update/delete triggers raise an exception.

### Outbox

Fields include `event_id`, `event_type`, `schema_version`, aggregate identity, correlation/causation IDs, payload, status, available time, claim owner/expiry, attempt count, and timestamps.

```text
unique (event_id)
index (status, available_at, occurred_at)
```

Claims use `FOR UPDATE SKIP LOCKED`, bounded batches, leases, and compare-and-set completion. Publication order is guaranteed only per aggregate/partition key, not globally.

## Foreign keys and tenant isolation

- Child payment rows reference `(merchant_id, payment_id)` through tenant-scoped candidate keys.
- Cross-schema foreign keys are used inside Stage 1 only where they enforce local atomicity; extraction requires replacing them with stable IDs and asynchronous contracts.
- Repositories always require merchant scope for merchant-owned reads.

## Isolation and concurrency

- Normal payment transitions: PostgreSQL `READ COMMITTED` plus optimistic `version` update.
- Idempotency reservation: unique constraint plus atomic insert/update.
- Ledger posting: one database transaction; account/snapshot locks acquired by sorted account UUID; serialization/deadlock failures retried with a bounded policy before any external side effect.
- Provider HTTP calls never execute while a database transaction or row lock is held.
