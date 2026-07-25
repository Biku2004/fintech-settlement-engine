# Settlement and Payout State Machines

## Settlement batch states

```text
CREATED -> CALCULATING -> READY -> SUBMITTING -> SUBMITTED -> CONFIRMED
                |           |          |             |
                v           v          v             v
        REQUIRES_REVIEW   CANCELLED  PAYOUT_UNKNOWN  REQUIRES_REVIEW
                \-----------------------> FAILED
```

| Current | Trigger | Next | Rule |
|---|---|---|---|
| `CREATED` | worker lease acquired | `CALCULATING` | one active lease per batch |
| `CALCULATING` | immutable items and totals persisted | `READY` | totals reconcile and checkpoint completes |
| `CALCULATING` | non-retryable data defect | `REQUIRES_REVIEW` | evidence retained |
| `READY` | approved submit command | `SUBMITTING` | deterministic payout key created |
| `READY` | operator cancels before submission | `CANCELLED` | reason and audit required |
| `SUBMITTING` | provider accepts instruction | `SUBMITTED` | provider reference persisted |
| `SUBMITTING` | result ambiguous | `PAYOUT_UNKNOWN` | no second payout instruction |
| `SUBMITTING` | authoritative rejection | `FAILED` or `REQUIRES_REVIEW` | based on retry policy |
| `PAYOUT_UNKNOWN` | success confirmed | `SUBMITTED` or `CONFIRMED` | authoritative evidence |
| `PAYOUT_UNKNOWN` | non-execution confirmed | `READY` | original payout key remains authoritative |
| `SUBMITTED` | settlement confirmed | `CONFIRMED` | statement/query/callback evidence |
| any non-terminal | manual intervention required | `REQUIRES_REVIEW` | privilege, reason, evidence, audit |

Closed batches are immutable. Refunds/disputes arriving later create adjustment items in a later cycle.

## Payout attempt states

```text
CREATED -> IN_PROGRESS -> SUCCEEDED
                     \-> FAILED_RETRYABLE
                     \-> FAILED_FINAL
                     \-> UNKNOWN
UNKNOWN -> SUCCEEDED | FAILED_FINAL | CONFIRMED_NOT_EXECUTED
```

A retry from `FAILED_RETRYABLE` reuses the deterministic payout operation key unless provider semantics require a separate attempt identifier underneath the same business operation.
