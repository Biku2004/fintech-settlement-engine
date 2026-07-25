# ADR-005 — Transactional Outbox

**Status:** Accepted

## Context
Committing database state and publishing an event as separate uncoordinated actions creates lost or phantom events.

## Decision
Append the event envelope to `platform.outbox_event` in the same PostgreSQL transaction as the state change. Initially publish with leased polling using `FOR UPDATE SKIP LOCKED`; later Debezium may replace polling without changing event identity.

## Alternatives
- Direct publish after commit: rejected due to crash gap.
- Publish before commit: rejected due to phantom event.
- Distributed transaction: rejected for complexity and limited external coverage.

## Consequences
Publication is at-least-once; consumers require inbox/idempotency. Outbox retention, lag, poison rows, and replay need operations support.

## Revisit criteria
Measured throughput or operational cost justifies Debezium/Kafka and compatibility/replay tests are ready.
