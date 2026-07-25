# Stable Public Error Catalog

Error envelope:

```json
{
  "error": {
    "code": "PAYMENT_INVALID_STATE",
    "message": "The payment cannot be captured in its current state.",
    "correlationId": "...",
    "retryable": false,
    "details": []
  }
}
```

| Code | HTTP | Retryable | Meaning |
|---|---:|---:|---|
| `AUTHENTICATION_REQUIRED` | 401 | No | No valid principal |
| `FORBIDDEN` | 403 | No | Principal lacks action permission |
| `RESOURCE_NOT_FOUND` | 404 | No | Owned resource absent or inaccessible |
| `REQUEST_INVALID` | 400 | No | Invalid syntax/shape |
| `FIELD_NOT_ALLOWED` | 400 | No | Internal/mass-assignment field supplied |
| `MONEY_INVALID` | 422 | No | Amount, currency, or minor-unit rule invalid |
| `PAYMENT_INVALID_STATE` | 409 | No | State-machine guard rejected command |
| `PAYMENT_CONCURRENT_MODIFICATION` | 409 | Yes | Version changed; client may read and decide |
| `IDEMPOTENCY_KEY_REQUIRED` | 400 | No | Missing key on state-changing request |
| `IDEMPOTENCY_CONFLICT` | 409 | No | Same scope/key with different semantic hash |
| `OPERATION_IN_PROGRESS` | 202 | Yes | Original operation is still running |
| `PROVIDER_OUTCOME_UNKNOWN` | 202 | No automatic retry | External outcome requires reconciliation |
| `PROVIDER_REJECTED` | 422 | Depends on reason | Authoritative provider rejection |
| `RATE_LIMITED` | 429 | Yes | Retry after supplied delay |
| `DEPENDENCY_UNAVAILABLE_PRE_EXECUTION` | 503 | Yes | Request definitely did not execute externally |
| `INTERNAL_ERROR` | 500 | Maybe | Sanitized unexpected failure |

Public errors never reveal SQL, stack traces, credentials, topology, or whether another tenant owns a supplied ID.
