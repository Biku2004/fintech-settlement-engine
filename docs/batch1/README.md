# Batch 1 — Money and Ledger Domain Kernel

## Goal

Implement the smallest complete accounting kernel that can later support payment capture, refunds, settlement, reconciliation, and outbox persistence without depending on Spring, HTTP, jOOQ, or PostgreSQL.

## Delivered capabilities

1. Currency-aware `Money` using integer minor units.
2. Exact currency-boundary conversion.
3. Checked arithmetic and configurable absolute-limit guard.
4. Ledger account roles, account types, normal balances, owner scope, currency, and status.
5. Immutable posting commands, entries, and ledger transactions.
6. Debit/credit equality validation.
7. Deterministic entry and future lock ordering.
8. Capture, settlement, refund, reserve, and full-reversal policies.
9. Posting fingerprint, idempotency-key replay, source-event replay, and conflict rejection.
10. Authoritative balances calculated from immutable entries.
11. Derived balance snapshots that do not authorize writes.
12. Executable offline verification plus Maven/JUnit/property/architecture test sources.

## Central rule

A balanced transaction is necessary but not sufficient. Every policy also validates account role, scope, currency, source identity, and posting-policy version.
