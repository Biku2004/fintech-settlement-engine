# ADR-001 — Initial Deployment Structure

**Status:** Accepted

## Context
The target system has payment, ledger, settlement, reconciliation, webhook, and notification boundaries, but beginning with deployable microservices would multiply distributed failure modes before domain correctness is proven.

## Decision
Build one Spring Boot modular monolith with strict Payment, Ledger, Outbox, and Audit modules plus a separately deployed provider simulator. Preserve separate schemas and public module ports.

## Alternatives
- Microservices immediately: rejected because it introduces network consistency, deployment, and observability complexity too early.
- One unstructured monolith: rejected because it prevents ownership and extraction.

## Consequences
Capture, ledger posting, and outbox append can be locally atomic. Modules share a release and database failure domain. Extraction later is a consistency redesign, not package movement.

## Revisit criteria
Independent scaling, release cadence, security isolation, team ownership, or resource contention is measured and cannot be solved within the modular application.
