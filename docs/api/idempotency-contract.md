# Idempotency Contract

## Scope and key

Scope is `(merchantId, operationType, Idempotency-Key)`. Keys are 1–128 printable ASCII characters and must not contain credentials or personal data.

## Canonical request hash

The server builds a canonical semantic document containing only business inputs, sorted field names, normalized uppercase currency, canonical UUIDs, and integer minor-unit amounts. Transport metadata and correlation IDs are excluded. SHA-256 of UTF-8 canonical JSON is stored.

## Record states

```text
RESERVED -> IN_PROGRESS -> COMPLETED
                       \-> RETRYABLE_ABORTED
```

External unknown outcomes are represented by a `COMPLETED` API result whose payment/operation status is `*_UNKNOWN`; the request must not be re-executed independently.

## Duplicate behaviour

| Existing state | Same hash | Response |
|---|---:|---|
| `RESERVED`/`IN_PROGRESS` | Yes | `202 OPERATION_IN_PROGRESS` with same operation ID |
| `COMPLETED` | Yes | Original status, headers, and safe response body |
| `RETRYABLE_ABORTED` | Yes | Atomically reacquire and execute using same business operation key |
| Any | No | `409 IDEMPOTENCY_CONFLICT` |

The response snapshot is immutable except for explicitly non-semantic headers. Expired keys are retained at least as long as provider replay risk and financial audit policy require; the initial target is 30 days for API keys and permanent uniqueness for financial source-event/posting keys.
