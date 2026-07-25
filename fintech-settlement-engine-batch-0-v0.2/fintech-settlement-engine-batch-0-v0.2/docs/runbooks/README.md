# Runbook Index and Standard

Every runbook defines owner, severity, trigger/threshold, impact, containment, diagnosis, recovery, verification, escalation/communication, and post-incident actions. Metric names/thresholds are baseline hypotheses; implementation-specific dashboard URLs and exact commands are added with the owning batch without weakening the safety steps.

- `provider-outage.md`
- `database-connection-exhaustion.md`
- `ledger-imbalance-alarm.md`
- `stuck-settlement-batch.md`
- `outbox-or-consumer-lag.md`
- `dead-letter-replay.md`
- `secret-compromise.md`
- `failed-deployment-rollback.md`
- `reconciliation-backlog.md`

No runbook authorizes editing ledger history, replacing stable operation/event IDs, retrying an unknown external effect independently, or bypassing tenant/privilege checks.
