# Service-Level Indicators and Objectives

All objectives are initial targets. They become claims only after dashboards and load/runtime evidence exist.

## SLI definitions

### Local API availability

- Numerator: valid authenticated requests receiving a non-5xx response.
- Denominator: all valid authenticated requests reaching the application.
- Exclusions: deliberate load shedding (`429`), client errors, and configured provider-simulator fault scenarios.
- Window: rolling 30 days.
- Target: 99.5%; error budget 216 minutes/30 days.

### Local API latency

- Measurement: server duration excluding time inside provider adapter spans.
- Scope: create, read, timeline, and local command preparation/finalization.
- Target: p99 <=300 ms over rolling 24 hours at baseline workload.

### Provider operation initiation

- Numerator: accepted provider commands that begin adapter execution within 100 ms after local validation/idempotency reservation.
- Denominator: accepted provider commands not rejected by bulkhead/load shedding.
- Target: 99% over rolling 24 hours.

### Ledger correctness

- Numerator: committed ledger transactions passing balance and policy checks.
- Denominator: all committed ledger transactions.
- Target: 100%; error budget zero.
- Any confirmed unbalanced commit is a critical incident.

### Acknowledged durability

- Target: no acknowledged ledger posting or outbox append is lost after committed database acknowledgement.
- Error budget: zero.

### Unknown-operation resolution

- Target: 95% of unknown authorization/capture/cancellation/refund operations resolved within 60 minutes in simulator/staging.
- Critical threshold: any unknown operation older than 24 hours without an owner/evidence trail.

### Reconciliation visibility

- Target: 100% of imported differences produce a queryable classification or exception.
- Error budget: zero silent differences.

## Error-budget policy

- Zero-budget correctness/security objectives stop releases until root cause and invariant verification complete.
- Availability/latency budget burn >2x for one hour blocks risky deployments.
- >50% monthly budget consumed triggers capacity/reliability review.
- Provider-caused failures are reported separately but are not hidden from end-to-end user experience metrics.

## Measurement ownership

Service operator owns SLI implementation; domain owners approve correctness definitions; security owner approves redaction and audit evidence. Every alert maps to `docs/runbooks/`.
