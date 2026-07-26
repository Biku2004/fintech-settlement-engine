# ADR-010 — Database-Enforced Ledger Immutability

**Status:** Accepted

## Context
Application-only immutability can be bypassed by bugs, scripts, or excess database privileges.

## Decision
Runtime roles receive no update/delete on committed ledger tables; defensive triggers reject mutation; migration/maintenance roles are separate and audited; corrections use append-only reversal/adjustment transactions.

## Alternatives
Application convention only was rejected. Triggers only were rejected because least privilege is also required. Cryptographic chaining is deferred as additional tamper evidence, not a replacement.

## Consequences
Migrations involving immutable tables require explicit controlled procedures. Integration tests verify grants and triggers.

## Revisit criteria
The exact enforcement mechanism may evolve, but the runtime's inability to mutate committed history is permanent.
