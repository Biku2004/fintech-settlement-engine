# Project Charter — Fintech Settlement Engine

## Product problem

Merchants need a reliable platform that can create and track payment operations, prevent duplicate financial effects, represent uncertain provider outcomes, record immutable double-entry movements, settle merchant balances, reconcile external evidence, and explain every state to operators.

## Users

- Merchant developer integrating payment commands and webhooks.
- Merchant finance user inspecting captures, fees, refunds, and settlement.
- Payment operations analyst investigating unknown outcomes.
- Reconciliation analyst resolving evidence differences.
- Security/compliance operator reviewing privileged actions and secrets.
- Service operator responding to availability, lag, database, and recovery incidents.

## Stage 1 product scope

Executable first slice:

1. create payment intent;
2. authorize through a separate provider simulator;
3. capture partially or fully;
4. cancel unused authorization;
5. post successful capture through `capture-v1` into an immutable balanced ledger;
6. append an outbox event atomically;
7. expose payment and evidence timeline;
8. reconcile unknown authorize/capture/cancel outcomes through provider query/callback evidence.

Refund, settlement, statement reconciliation, merchant webhook delivery, Kafka, Debezium, Redis, and Kubernetes are designed in Batch 0 but implemented in later batches.

## Non-goals

- Real card numbers, CVV, bank credentials, or real-money movement.
- Claiming PCI certification, regulatory approval, five-nines availability, or proven scale.
- Building all future services as empty scaffolding.
- Treating Kafka/Redis as financial authority.
- Automatic manual-looking reconciliation or deletion of evidence.

## Success criteria for the first slice

- duplicate API/provider requests create one effective operation;
- provider success with lost response becomes explicit unknown state;
- unknown operations resolve without duplicate provider or ledger effects;
- every committed capture posting balances and follows `capture-v1`;
- ledger entries cannot be mutated by runtime application roles;
- state, attempt evidence, ledger posting, and outbox event are queryable;
- cross-merchant access and mass assignment fail closed;
- all Batch 1–4 invariant and failure tests pass.

## Product safety principles

- Unknown is not failure.
- A status without evidence is insufficient for financial operations.
- Reconciliation repairs knowledge; it does not rewrite history.
- Notifications never control financial commit.
- Operator actions are explicit, permissioned, reasoned, and audited.
- Correctness objectives have zero error budgets where loss or duplication is possible.
