# Initial Alert Catalog

| Alert | Warning | Critical | Window | Runbook |
|---|---:|---:|---|---|
| Provider success rate | <98% | <90% | 10 min | `provider-outage.md` |
| Provider p95 latency | >3 s | >8 s | 10 min | `provider-outage.md` |
| DB pool pending | >20% pool | >50% pool | 5 min | `database-connection-exhaustion.md` |
| DB acquire timeout | >1/min | >10/min | 5 min | `database-connection-exhaustion.md` |
| Ledger imbalance rejection | any | >=2 | immediate/5 min | `ledger-imbalance-alarm.md` |
| Oldest outbox row | >60 s | >300 s | 5 min | `outbox-or-consumer-lag.md` |
| Dead-letter growth | >0 | >10 | 15 min | `dead-letter-replay.md` |
| Unknown capture age | >15 min | >60 min | rolling | `provider-outage.md` |
| Settlement batch age | >2x target | >4x target | rolling | `stuck-settlement-batch.md` |
| Reconciliation backlog | >1 day | >3 days | daily | `reconciliation-backlog.md` |

Thresholds are starting hypotheses and must be recalibrated from load and production evidence without weakening correctness alerts.
