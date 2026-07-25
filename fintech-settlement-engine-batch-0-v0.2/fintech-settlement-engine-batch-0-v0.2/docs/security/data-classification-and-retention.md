# Data Classification and Retention

| Class | Examples | Telemetry | Initial retention target |
|---|---|---|---|
| Public | public error codes, product documentation | allowed | product policy |
| Internal | service/module names, bounded metrics | allowed with access control | 90 days logs/metrics target |
| Confidential | merchant IDs, payment IDs, amounts, provider references | identifiers only where required; no raw payloads | financial records 7 years target subject to legal review |
| Restricted | secrets, authorization headers, raw credentials, real card/bank data | prohibited | must not be accepted/stored; secret manager owns lifecycle |

Raw provider callbacks and statement evidence, when implemented, are encrypted, immutable, separately authorized, referenced by checksum/object ID, and excluded from ordinary logs/events. Deletion/retention decisions never mutate immutable ledger evidence without legal/audit process.
