# Changelog

## v0.2.0

This revision closes the documentation findings from the v0.1 principal-engineer review.

### P0 closure

- Rebuilt the payment state machine with deterministic unknown-state recovery, repeated partial refunds, explicit pre-execution failure handling, and `CANCELLATION_UNKNOWN`.
- Added ledger account taxonomy and posting policies.
- Replaced generic invariant test rows with concrete Given/When/Then scenarios.
- Resolved Batch 1 technology decisions.
- Replaced the premature validation result with layered structural/content/runtime statuses.

### P1 closure

- Added cancellation failure analysis.
- Added API, error, and idempotency contracts.
- Added relational constraints and transaction/isolation design.
- Added observability, audit-event, telemetry-safety, and alert specifications.
- Completed all ADRs using Context, Decision, Alternatives, Consequences, and Revisit Criteria.

### P2 documentation closure

- Added settlement, payout, reconciliation-run, and reconciliation-exception state machines.
- Expanded all operational runbooks to a consistent executable template.
- Added formal SLIs, error budgets, workload assumptions, and capacity thresholds.
- Fixed backup cadence, retention, ownership, restore-test frequency, and resume rules.
- Added a restore/replay exercise plan. Actual exercise evidence is intentionally deferred until database and workers exist.
