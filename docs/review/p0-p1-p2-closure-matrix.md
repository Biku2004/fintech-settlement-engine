# v0.1 Review Closure Matrix

| Priority | Finding | v0.2 evidence | Status |
|---|---|---|---|
| P0 | Payment state machine incomplete | `domain/payment-state-machine.md` | Closed |
| P0 | Ledger account/posting policy missing | `domain/ledger-account-model-and-posting-policies.md` | Closed |
| P0 | Generic Batch 1 tests | `test-strategy/invariant-test-traceability.csv` | Closed |
| P0 | Validation prematurely passed | `review/validation-report.md` | Closed |
| P0 | Batch 1 technology choices unresolved | `review/implementation-decisions.md`, ADR-008–010 | Closed |
| P1 | Cancellation failure analysis absent | `failure-modes/remote-operation-failure-matrix.md` | Closed |
| P1 | API/error/idempotency contract absent | `api/` | Closed |
| P1 | Relational constraints absent | `architecture/relational-model-and-constraints.md` | Closed |
| P1 | Observability/audit specification absent | `observability/` | Closed |
| P1 | Payment/security tests generic | traceability CSV | Closed |
| P1 | ADRs incomplete | ADR-001–010 | Closed |
| P2 | Settlement/payout states absent | `domain/settlement-and-payout-state-machines.md` | Closed for design |
| P2 | Reconciliation states absent | `domain/reconciliation-state-machines.md` | Closed for design |
| P2 | Runbooks incomplete | `runbooks/` | Closed for design; commands evolve with code |
| P2 | Capacity/formal SLIs absent | `slo/` | Closed for design |
| P2 | DR cadence/ownership absent | `disaster-recovery/recovery-policy.md` | Closed for design |
| P2 | Actual restore/replay exercise | `restore-and-replay-exercise-plan.md` | Deferred honestly until executable infrastructure exists |
