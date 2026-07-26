# Restore and Replay Exercise Plan

## Preconditions

Executable migrations, seeded PostgreSQL, ledger verification queries, outbox/inbox workers, provider simulator query endpoint, and isolated restore environment.

## Exercise

1. Create known payment/capture/ledger/outbox fixtures and record checksums.
2. Take backup and record recovery marker.
3. Add later transactions; create one unknown provider operation and one unpublished outbox row.
4. Simulate database loss.
5. Restore to the selected recovery point.
6. Run invariant verification.
7. Reconcile provider operations after the recovery point.
8. Resume publisher and replay one duplicate event.
9. Prove one effective ledger posting and one effective consumer side effect.
10. Record RPO, RTO, queries, logs, screenshots, failures, and actions.

## Evidence artifact

`docs/disaster-recovery/evidence/YYYY-MM-DD-restore-exercise.md` will contain environment, versions, backup IDs, start/end times, measured RPO/RTO, invariant results, duplicate tests, deviations, owners, and approval.

**Current execution status:** NOT YET EXECUTABLE — no database/service implementation exists in Batch 0. This is transparent deferred evidence, not a passed test.
