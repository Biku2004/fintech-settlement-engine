# CS-001 — Caller-Controlled Policy Label Authorized Wrong Financial Movement

## Severity

**High** within the ledger-service trust boundary. CWE-863 / CWE-840.

## Original vulnerable path

In v0.3.0, `PostingCommand` was publicly constructible and carried a caller-selected `PostingPolicyReference`. `LedgerKernel.post` calculated a fingerprint, checked replay maps and account registration, then passed the command directly to `LedgerTransactionFactory`. The factory enforced positive entries, currency equality, merchant account ownership, and debit/credit balance, but it did not verify that `capture-v1` actually used capture account roles and directions.

Affected original locations:

- `PostingCommand.java:8-35` — caller-controlled command and policy label.
- `LedgerKernel.java:63-100` — mutation path without policy validation.
- `LedgerTransactionFactory.java:32-105` — generic balance validation accepted the forged shape.

## Reproduction

The original probe labeled a balanced settlement-cash-to-merchant-payable movement as `capture-v1`. The kernel returned `CREATED`. This proves that mathematical balance alone was acting as authorization for the posting.

Evidence:

- `../evidence/original-exploit-probe.java`
- `../evidence/original-exploit-output.txt`

## Impact

A compromised, buggy, or overly privileged internal caller could move value between registered accounts using an approved policy name without respecting that policy's accounting template. In a durable implementation this could corrupt merchant liabilities, platform cash, fee revenue, reserves, or reconciliation evidence while preserving debit/credit equality.

## Fix

`PostingPolicyValidator` is now called at the beginning of `LedgerKernel.post` before fingerprint registration or mutation. It allowlists exact policy versions and validates source type, reversal mode, account roles, directions, and line cardinality. The transaction factory accepts only `ValidatedPostingCommand`.

Fixed locations:

- `LedgerKernel.java:86-89`
- `PostingPolicyValidator.java:24-172`
- `LedgerTransactionFactory.java:50-52`

## Validation

The original forged command now raises `InvalidPostingPolicyException`; the kernel transaction count remains zero. The focused JUnit/security self-tests and the post-fix exploit probe cover the same boundary.

**Outcome: fixed.**
