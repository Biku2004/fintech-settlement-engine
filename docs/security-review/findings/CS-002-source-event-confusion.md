# CS-002 — Posting Policy Was Not Bound to Its Financial Source Event

## Severity

**Medium.** CWE-20 / CWE-345.

## Original vulnerable path

Policy builders validated amounts, roles, scope, and currency, but did not require the financial source-event type that gives the posting its business meaning. A capture posting could therefore be created with a source such as `SETTLEMENT_CONFIRMED`. Because source identity is also used for deduplication and audit correlation, this was more than a label-quality problem.

Affected original locations:

- `CapturePostingPolicy.java:20-49`
- corresponding refund, settlement, and reserve builders;
- `LedgerKernel.java:63-100`, which did not independently validate source type.

## Reproduction

The original probe built a structurally valid capture using the wrong source-event type. The kernel returned `CREATED`.

## Impact

The system could permanently associate ledger effects with the wrong external fact. This weakens replay protection, incident reconstruction, reconciliation, and any future event-driven consumer that routes by source type.

## Fix

Every policy builder now calls `PostingPolicySupport.requireSourceType`, and the authoritative kernel-side validator repeats the check so deserialized or directly constructed commands cannot bypass it.

Fixed locations:

- `CapturePostingPolicy.java:27`
- `RefundPostingPolicy.java:28`
- `SettlementPostingPolicy.java:23`
- `ReservePostingPolicy.java:24,36`
- `PostingPolicyValidator.java:47-98,130-137`

## Validation

Builder-level and direct-command tests both reject mismatched source types. The post-fix exploit probe records `REJECTED:InvalidPostingPolicyException` and no transaction is created.

**Outcome: fixed.**
