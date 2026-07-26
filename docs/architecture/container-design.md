# Container Design

## Stage 1 — implementation baseline

```mermaid
flowchart TB
    Client[Merchant or Operator Client]
    App[Fintech Application
Spring Boot modular monolith]
    Simulator[Provider Simulator
separate Spring Boot application]
    DB[(PostgreSQL)]
    Publisher[Outbox Publisher
in application process]
    Telemetry[OpenTelemetry Collector / local observability]

    Client --> App
    App --> Simulator
    Simulator --> App
    App --> DB
    Publisher --> DB
    App --> Telemetry
    Simulator --> Telemetry
```

### Fintech application modules

- Identity and merchant authorization.
- Payment.
- Ledger.
- Settlement, initially skeletal.
- Reconciliation, initially skeletal.
- Outbox.
- Audit and observability.

### Boundary rules

- Modules expose application interfaces, not repositories.
- A module cannot write another module’s tables directly.
- Payment owns payment lifecycle; Ledger owns posting rules.
- Stage 1 may coordinate Payment and Ledger in one PostgreSQL transaction because both are in one deployable and database.
- Extraction must not preserve cross-schema table access; it will move to commands/events and reconciliation.

## Stage 2 — target distributed shape

```mermaid
flowchart LR
    Gateway[API Gateway] --> Payment[Payment Service]
    Payment --> Provider[Provider Simulator]
    Payment --> PDB[(Payment DB)]
    Payment --> Kafka[(Kafka)]
    Kafka --> Ledger[Ledger Service]
    Kafka --> Settlement[Settlement + Reconciliation]
    Kafka --> Webhook[Webhook Delivery]
    Ledger --> LDB[(Ledger DB)]
    Settlement --> SDB[(Settlement / Reconciliation DB)]
```

Stage 2 is conditional. Extraction requires measured operational or ownership need, not résumé appearance.
