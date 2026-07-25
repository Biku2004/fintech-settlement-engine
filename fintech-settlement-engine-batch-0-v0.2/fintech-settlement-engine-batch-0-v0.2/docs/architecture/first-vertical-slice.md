# First Vertical Slice

## Goal

Demonstrate one complete, correct, observable flow from merchant command to provider attempt to ledger evidence.

## Sequence

```mermaid
sequenceDiagram
    participant C as Merchant Client
    participant P as Payment Module
    participant S as Provider Simulator
    participant L as Ledger Module
    participant D as PostgreSQL
    participant O as Outbox Publisher

    C->>P: Create payment (Idempotency-Key)
    P->>D: payment + idempotency record
    D-->>P: commit
    P-->>C: payment CREATED

    C->>P: Authorize (Idempotency-Key)
    P->>D: persist attempt + REQUIRES_AUTHORIZATION
    P->>S: authorize(providerOperationKey)
    S-->>P: known success
    P->>D: AUTHORIZED + attempt result + outbox
    D-->>P: commit
    P-->>C: AUTHORIZED

    C->>P: Capture (Idempotency-Key)
    P->>D: persist capture attempt
    P->>S: capture(providerOperationKey)
    S-->>P: known success
    P->>L: postCapture(sourceEventId, entries)
    L->>D: ledger transaction + entries + posting idempotency
    P->>D: CAPTURED + payment event + outbox
    D-->>P: one atomic commit
    P-->>C: CAPTURED

    O->>D: claim unpublished outbox rows
    O->>D: record publication attempt
```

## Transaction boundaries

- Create is one local database transaction.
- Provider HTTP calls are never held inside a database transaction.
- Attempt intent is persisted before provider invocation.
- Known provider success is applied in a new transaction.
- In Stage 1, payment state, ledger posting, and outbox insertion for capture commit atomically in one PostgreSQL transaction across owned schemas.
- A lost provider response produces `CAPTURE_UNKNOWN`; the system does not post the ledger until authoritative success evidence exists.

## Definition of complete

- Happy path works through public API.
- Lost-response scenario produces unknown state.
- Duplicate client requests and provider callbacks are safe.
- Ledger transaction balances and is immutable.
- Timeline connects command, attempt, provider reference, posting, and event.
- Traces and metrics explain latency and failures.
