# Disaster Recovery and Verified Restore Policy v0.2

## Ownership

- Incident commander: Service Operator on call.
- Database restore owner: Database/Platform Owner.
- Financial verification owner: Ledger/Finance Domain Owner.
- Security approval for secret/audit recovery: Security Owner.
- Final resume approval: Incident Commander plus Financial Verification Owner.

## Targets

| Environment | RPO target | RTO target |
|---|---:|---:|
| Local/demo | 24 hours | 4 hours |
| Staging | 15 minutes | 2 hours |
| Production design | 5 minutes | 60 minutes |

No duplicate effective provider or ledger side effect after restart/replay is a zero-error objective.

## Backup and retention

- Local/demo: daily logical backup, retain 7 days.
- Staging: continuous WAL/PITR where available, daily full backup, retain 14 days.
- Production design: continuous WAL/PITR retain 14 days; daily full retain 35 days; month-end retain 12 months, subject to legal approval.
- Statement/evidence object storage: versioning, checksum, server-side encryption, 90-day staging retention, production policy subject to legal/audit requirements.
- Secrets are recovered from secret manager procedures, never database dumps.

## Restore-test frequency

- Before first staging release.
- Monthly automated staging restore and invariant verification.
- Quarterly full restore, worker resume, and replay game day after distributed workers exist.
- After any material backup, migration, ledger, or outbox architecture change.

## Restore sequence

1. Declare incident, freeze writes, preserve evidence.
2. Provision isolated network/database and compatible application version.
3. Restore backup and WAL target.
4. Verify Flyway history and schema checksum.
5. Run ledger balance, reversal, source uniqueness, payment amount, idempotency, and audit readability checks.
6. Compare backup metadata counts/checksums.
7. Start publishers/workers paused.
8. Inspect outbox/inbox/lease checkpoints.
9. Reconcile unknown external operations before retry.
10. Resume one worker class/partition at a time with duplicate-effect monitoring.
11. Record actual RPO/RTO and attach evidence to the exercise report.

## Acceptance

Restore passes only if all committed ledger transactions balance, no duplicate source posting exists, payment totals remain valid, audit/idempotency evidence is readable, checkpoints are understood, and controlled resume produces no duplicate effect.

Actual restore evidence is not claimed in Batch 0; the exercise plan is defined in `restore-and-replay-exercise-plan.md` and becomes a later release gate.
