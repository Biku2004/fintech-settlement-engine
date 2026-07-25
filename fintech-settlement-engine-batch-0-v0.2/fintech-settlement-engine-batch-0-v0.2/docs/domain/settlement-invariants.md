# Settlement Invariants

Settlement is designed in Batch 0 and implemented later.

| ID | Invariant |
|---|---|
| ST-001 | Each settlement batch belongs to one merchant, currency, policy version, and settlement cycle. |
| ST-002 | A source payment item is included at most once in an effective settlement batch. |
| ST-003 | Batch calculation uses a fixed cut-off instant and records the time zone and policy version. |
| ST-004 | Configuration changes after calculation do not silently change an existing batch. |
| ST-005 | Batch totals equal the sum of immutable included items and adjustments. |
| ST-006 | A payout instruction has a deterministic idempotency key. |
| ST-007 | A provider payout timeout becomes `PAYOUT_UNKNOWN`, not immediate failure. |
| ST-008 | A restarted worker resumes from persisted batch state and checkpoint. |
| ST-009 | One merchant’s failure does not block calculation or submission for unrelated merchants. |
| ST-010 | Negative payable balances follow an explicit carry-forward or reserve policy; they are never silently paid. |
| ST-011 | Refunds or disputes after inclusion create later adjustments; they do not mutate the closed batch. |
| ST-012 | A batch cannot move to `CONFIRMED` without authoritative payout evidence. |
| ST-013 | Manual batch resolution requires privilege, reason, evidence, and audit. |
| ST-014 | Every batch exposes gross, fees, refunds, reserves, adjustments, and net payout as independently explainable values. |
| ST-015 | Settlement calculation is repeatable from its recorded inputs and policy version. |
