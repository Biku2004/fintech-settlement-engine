# Operator Experience Requirements

## Payment investigation view

Must display payment amounts/state, active unknown snapshot, attempt history, provider evidence, ledger links, reconciliation state, correlation IDs, and action eligibility with explicit reason when disabled.

## Action eligibility

| Action | Enabled only when |
|---|---|
| Retry provider call | prior attempt is provably pre-execution and idempotency record permits reacquire |
| Query provider | operation key exists and user has operations permission |
| Resolve unknown | authoritative evidence exists; elevated permission, reason, and audit |
| Reverse posting | reversible effective amount exists; approval and reason |
| Replay event | original event/schema and consumer idempotency are verified |

## Interaction safety

- No generic “Mark successful/failed” control.
- Destructive/financial actions use confirmation showing payment, amount, currency, source evidence, and consequence.
- Manual free text never becomes executable amount/account data.
- Exports neutralize spreadsheet formulas and apply permissions.
- List views show last-updated time, filters, pagination, and explicit partial-data warnings.

## Accessibility and readability

- Status is never communicated by color alone.
- Unknown/critical states include icon, label, explanation, and action.
- Tables support keyboard navigation and semantic headings.
- Error focus moves to the first actionable issue.
- Currency values include code and formatted value; raw minor units are available in technical detail.
- Times display UTC source plus chosen local representation.

## Auditability

Every privileged UI flow previews the audit record fields and returns the resulting audit event ID after success.
