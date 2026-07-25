# ADR-004 — Immutable Double-Entry Ledger

**Status:** Accepted

## Context
Balances must be explainable and corrections must preserve evidence.

## Decision
Every financial movement is an immutable balanced transaction with debit and credit entries. Corrections append reversals or adjustments. Runtime database roles and triggers deny updates/deletes.

## Alternatives
- Mutable balance rows only: rejected because history and reconciliation are lost.
- Edit erroneous entries: rejected because audit evidence changes.
- Redis balance authority: rejected.

## Consequences
Storage grows append-only; snapshots optimize reads but are not authoritative. Posting policy correctness is as important as balancing.

## Revisit criteria
Never revisit immutability/balance. Storage and snapshot mechanisms may change while preserving these invariants.
