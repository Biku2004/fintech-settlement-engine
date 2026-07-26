# Fintech Settlement Engine


# Fintech Settlement Engine — Batch 0 v0.2

**Batch:** Product, Domain, Architecture, Reliability, and Safety Constitution  
**Status:** Internally validated; ready for user approval and Batch 1  
**Runtime evidence:** Not applicable until executable batches exist

## Purpose

This package converts the project idea into enforceable product rules, financial invariants, state machines, architecture decisions, security boundaries, failure semantics, API behaviour, database constraints, observability requirements, recovery procedures, and test obligations.

The system models a multi-merchant payment orchestration and settlement platform. It uses a provider simulator and never accepts or stores real payment-card or bank credentials.

## First business slice

```text
Create payment intent
    -> authorize through provider simulator
    -> capture
    -> post one immutable balanced ledger transaction
    -> write one transactional outbox event
    -> expose the evidence-rich payment timeline
```

## Stage 1 implementation decisions

| Concern | Decision |
|---|---|
| Runtime | Java 25 LTS |
| Framework | Spring Boot 4.1 |
| Architecture | One Spring Boot modular monolith plus separate provider simulator |
| Module enforcement | Spring Modulith verification plus ArchUnit dependency rules |
| Persistence | jOOQ for explicit SQL and transaction boundaries |
| Database | PostgreSQL; separate schemas and restricted database roles |
| IDs | UUIDv7 generated in the application and stored as PostgreSQL `uuid` |
| Money | `long`/PostgreSQL `BIGINT` minor units with checked arithmetic |
| Ledger immutability | No mutation API, restricted role grants, and defensive triggers |
| Messaging | Database outbox publisher initially; Kafka/Debezium later |
| Cache | None initially; Redis only after a measured requirement |
| Base package | `com.bikash.fintechsettlement` |

## Package map

- `docs/product/` — users, jobs, workflows, permissions, screen states, and safe actions.
- `docs/domain/` — glossary, invariants, account/posting policies, and lifecycle state machines.
- `docs/api/` — endpoint, error, ownership, and idempotency contracts.
- `docs/architecture/` — context, containers, module boundaries, data ownership, and relational constraints.
- `docs/adr/` — accepted architecture decisions and revisit triggers.
- `docs/threat-model/` — assets, attackers, trust boundaries, abuse cases, and controls.
- `docs/failure-modes/` — remote-operation ambiguity and recovery matrices.
- `docs/observability/` — logs, traces, metrics, audit events, redaction, and alerts.
- `docs/slo/` — workload assumptions, SLIs, SLOs, error budgets, and capacity thresholds.
- `docs/test-strategy/` — release gates and concrete invariant/failure traceability.
- `docs/schema/` — event envelope, partitioning, compatibility, retention, and data classification.
- `docs/disaster-recovery/` — backup, restore, replay, retention, ownership, and exercise plan.
- `docs/runbooks/` — operational response guides with ownership and escalation.
- `docs/review/` — closure matrix, risk register, validation report, and exit criteria.
- `scripts/validate_batch0.py` — deterministic package consistency checks.

## Approval meaning

Batch 0 approval means the constitution is safe and specific enough to begin the Money and Ledger Domain Kernel. It does not claim that runtime SLOs, restores, load tests, deployments, or replay behaviour have already been proven.

------------------------------------------------------------------------------------------------------------------

## Batch 1 — Money and Ledger Domain Kernel

This repository builds on the approved Batch 0 v0.2 Product, Domain, and Safety Constitution.

Batch 1 proves the central accounting rule before introducing HTTP, Spring Boot, PostgreSQL, Kafka, Redis, or provider calls:

```text
For every accepted ledger transaction:

sum(debits) = sum(credits)
```

## Implemented

- Currency-aware money stored as signed `long` minor units.
- Exact parsing of USD, JPY, KWD, and other ISO currencies using their configured fraction digits.
- Checked addition, subtraction, multiplication, negation, and fee arithmetic.
- Immutable ledger accounts, posting lines, entries, and transactions.
- Account role/type/scope validation.
- Balanced transaction creation with deterministic entry ordering.
- Capture, settlement, refund, reserve hold/release, and full reversal policies.
- Posting idempotency and source financial-event uniqueness.
- Authoritative account balance calculation from immutable entries.
- Deterministic account lock ordering helper for the later PostgreSQL implementation.
- Domain tests, property-test sources, architecture-test sources, and an offline self-test runner.

## Not implemented in this batch

- No controllers or API endpoints.
- No Spring Boot runtime.
- No PostgreSQL or jOOQ repositories.
- No Kafka, Debezium, Redis, or outbox publisher.
- No payment state machine or provider simulator.
- No balance snapshot used to authorize writes.

These omissions are deliberate. Batch 1 is the domain kernel, not an enterprise-looking scaffold.

## Project structure

```text
libraries/money
libraries/identity
applications/fintech-application/ledger-domain
tests/domain-tests/ledger
tests/selftest
docs/batch1
scripts
```

## Verify without Maven

The environment-independent verification path requires JDK 21 or newer because the source intentionally uses only language features available in Java 21 while the Maven target is Java 25.

```bash
./scripts/verify-batch1.sh
```

## Maven build

With JDK 25 and Maven 3.9+:

```bash
mvn clean verify
```

The Maven test modules use JUnit 5.14.4, jqwik 1.9.3, and ArchUnit 1.4.2.
