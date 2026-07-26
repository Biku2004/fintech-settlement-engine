# ADR-007 — Delay Kafka, Redis, and Debezium

**Status:** Accepted

## Context
The first slice can prove financial correctness with PostgreSQL and a local outbox publisher.

## Decision
Do not introduce Kafka, Redis, or Debezium before the capture-to-ledger slice, failure tests, and observability baseline pass.

## Alternatives
- Introduce all infrastructure immediately: rejected due to debugging surface and false enterprise appearance.
- Never use asynchronous infrastructure: rejected because later settlement/webhook/replay workloads benefit from it.

## Consequences
Early throughput is lower and publisher polling is temporary. Event contracts and idempotency are still designed now to avoid rewrite.

## Revisit criteria
Batch 9 entry criteria pass and measured asynchronous load/independent scaling justifies adoption.
