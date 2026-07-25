# First-Slice API Behaviour Contract

Base path: `/v1`. All responses include `X-Correlation-Id`. Authenticated merchant identity determines ownership; request bodies cannot select a merchant.

## Money representation

Public APIs use `amountMinor` as a JSON integer and `currency` as a three-letter uppercase ISO code. Decimal floating-point numbers are rejected.

## Endpoints

| Method and path | Purpose | Idempotency |
|---|---|---|
| `POST /payment-intents` | Create intent | Required |
| `POST /payment-intents/{id}/authorize` | Authorize | Required |
| `POST /payment-intents/{id}/capture` | Partial/final capture | Required |
| `POST /payment-intents/{id}/cancel` | Cancel unused authorization | Required |
| `GET /payment-intents/{id}` | Current business state and amounts | Not used |
| `GET /payment-intents/{id}/timeline` | Append-only evidence timeline | Not used |
| `GET /operations/{operationId}` | Current in-progress/unknown operation | Not used |

Refund APIs are specified before Batch 6 and are not exposed in the first executable slice.

## Response rules

- `201 Created`: new payment intent.
- `200 OK`: synchronous command completed or idempotent replay of a completed result.
- `202 Accepted`: command is in progress or has an unknown external outcome; body contains operation and payment links.
- `400 Bad Request`: malformed request or invalid field format.
- `401 Unauthorized`: missing/invalid identity.
- `403 Forbidden`: principal lacks permission.
- `404 Not Found`: owned resource not found; cross-tenant resources use the same response.
- `409 Conflict`: invalid state transition, optimistic-lock conflict after re-evaluation, or idempotency payload conflict.
- `422 Unprocessable Entity`: valid syntax but domain amount/currency/limit violation.
- `429 Too Many Requests`: rate or concurrency limit.
- `503 Service Unavailable`: definitely pre-execution transient failure; safe retry guidance included.

## Ownership and mass-assignment rules

The server derives and overwrites:

```text
merchantId
paymentState
provider
providerReference
feePolicy
ledgerAccounts
internalSourceId
settlement fields
```

Supplying prohibited internal fields returns `FIELD_NOT_ALLOWED`; it is not silently trusted.

## Timeline contract

Each timeline item exposes safe evidence:

```text
eventId, eventType, occurredAt, recordedAt, actorType,
operationId, outcomeClassification, publicReasonCode, correlationId
```

It never exposes secrets, raw provider payloads, stack traces, or cross-tenant identifiers.
