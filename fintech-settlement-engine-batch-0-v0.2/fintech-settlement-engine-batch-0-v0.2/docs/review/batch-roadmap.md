# Batch Roadmap and Gates

| Batch | Scope | Entry gate | Exit evidence |
|---|---|---|---|
| 0 | Constitution | project selected | approved v0.2 package |
| 1 | Money + Ledger Kernel | Batch 0 approval | L invariants, DB roles/triggers, property/integration tests |
| 2 | Payment lifecycle | Batch 1 pass | state/amount/idempotency domain tests |
| 3 | Provider simulator + ambiguity | Batch 2 pass | network/query/callback/cancel unknown tests |
| 4 | Capture-to-ledger slice | Batch 3 pass | atomic payment/ledger/outbox E2E |
| 5 | Idempotency + outbox publisher | Batch 4 pass | crash/replay publication tests |
| 6 | Refund/reversal | Batch 5 pass | refund and posting-policy tests |
| 7 | Settlement/payout | Batch 6 pass | state, lease, payout unknown, adjustment tests |
| 8 | Reconciliation | Batch 7 pass | bounded import, deterministic match, resolution tests |
| 9 | Kafka/inbox/Debezium | measured need + contracts | compatibility, lag, replay, DLQ tests |
| 10 | Merchant webhook delivery | Batch 9 pass | SSRF/signature/retry/delivery tests |
| 11 | Security hardening | working surfaces | threat/control integration and scan gates |
| 12 | Observability/load/recovery | stable features | dashboards, load results, restore/replay game day |
| 13 | Deployment/portfolio | all gates | reproducible demo and design explanation |
