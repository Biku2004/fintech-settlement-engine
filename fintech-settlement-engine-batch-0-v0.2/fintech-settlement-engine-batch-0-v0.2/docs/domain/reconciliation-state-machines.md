# Reconciliation State Machines

## Reconciliation run

```text
CREATED -> IMPORTING -> MATCHING -> COMPLETED_CLEAN
                         |        -> COMPLETED_WITH_EXCEPTIONS
                         |        -> FAILED_RETRYABLE
                         \--------> FAILED_FINAL
```

- `IMPORTING` stores immutable source identity, checksum, schema version, and row checkpoints.
- `MATCHING` uses a recorded rules version and deterministic precedence.
- `FAILED_RETRYABLE` resumes from the last committed checkpoint.
- A completed run is immutable; corrections create another run or resolution action.

## Exception lifecycle

```text
OPEN -> INVESTIGATING -> AWAITING_EVIDENCE
  |          |                  |
  |          +------------------+
  v
RESOLVED_MATCHED
RESOLVED_NON_EXECUTION
RESOLVED_ADJUSTMENT_POSTED
RESOLVED_DUPLICATE
REQUIRES_ESCALATION
```

Every resolution records principal, reason, evidence references, before/after classification, correlation ID, and timestamp. No resolution deletes imported evidence.

## Matching precedence

1. exact provider operation/reference ID and currency;
2. exact platform source-event ID when provider echoes it;
3. deterministic composite match using merchant, amount, currency, operation type, and bounded time window;
4. manual investigation—never an automatic fuzzy financial match.

## Tolerance policy

The baseline uses exact integer minor-unit equality. A future non-zero tolerance must be currency- and provider-specific, versioned, visible in the exception, and approved by ADR.
