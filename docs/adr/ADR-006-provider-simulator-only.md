# ADR-006 — Provider Simulator Only

**Status:** Accepted

## Context
The portfolio system must exercise real failure semantics without collecting regulated card/bank data or charging money.

## Decision
Use a separately deployed simulator with versioned authorize/capture/cancel/refund/query/callback/statement contracts and deterministic fault scenarios. Real provider credentials and payment instruments are prohibited.

## Alternatives
- Real sandbox provider: deferred because it adds vendor onboarding and accidental data risk.
- In-process mocks only: rejected because they cannot prove network, timeout, signature, and restart behaviour.

## Consequences
Provider behaviour is controlled and repeatable but not proof of a vendor's exact production semantics. Adapter contracts remain replaceable.

## Revisit criteria
After core batches pass, a separate sandbox adapter may be added with secrets isolation and no raw card handling.
