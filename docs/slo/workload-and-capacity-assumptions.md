# Workload and Capacity Assumptions

These are design inputs, not measured claims.

## Portfolio baseline

- 1,000 merchants; 100 concurrently active.
- 1,000,000 payment intents per month.
- Average 0.5 payment commands/second; expected peak 25 commands/second.
- Stress target 100 local API commands/second with provider simulator latency excluded from local processing SLI.
- Up to 200 concurrent provider calls across all operations.
- One hot merchant may contribute 20% of peak traffic; per-merchant limits prevent monopolization.
- Average 5 timeline events per payment.
- Average 3 outbox events per completed payment lifecycle in the first slice.
- Statement import target: 100 MB or 1,000,000 rows, whichever limit is reached first; import is asynchronous and bounded.
- Initial PostgreSQL dataset target: 100 million immutable ledger entries before partitioning is considered based on measured index/table behaviour.

## Capacity safety thresholds

- Database pool pending requests <20% of pool for 99% of five-minute windows.
- Provider in-flight usage <80% of each bulkhead during normal load.
- Outbox oldest unpublished age <60 seconds during normal operation.
- Disk forecast must retain >30 days headroom at current growth before a capacity incident.
- Load shedding begins before pool, provider bulkhead, or worker queue exhaustion.

No partitioning, read replica, Redis, Kafka, or Kubernetes component is introduced solely to satisfy hypothetical scale. Measurements and an ADR trigger the change.
