# ADR-002 — PostgreSQL Is Authoritative Storage

**Status:** Accepted

## Context
Financial state requires atomic constraints, durable transactions, relational evidence, and verifiable restore.

## Decision
PostgreSQL is authoritative for payment state, ledger history, idempotency, outbox, settlement, reconciliation metadata, and audit indexes. Redis and Kafka may accelerate or transport data later but are not authoritative financial stores.

## Alternatives
- Redis as ledger/state: rejected because cache/failover semantics are unsuitable as sole authority.
- Event broker as only source: rejected for initial operational and query complexity.
- Multiple databases immediately: rejected before extraction is justified.

## Consequences
Database constraints and restore quality become critical. Scale-up, indexing, archival, and partitioning are measured before sharding.

## Revisit criteria
Measured data volume, isolation, regulatory, or independent availability needs exceed a single PostgreSQL deployment and an extraction ADR defines consistency and migration.
