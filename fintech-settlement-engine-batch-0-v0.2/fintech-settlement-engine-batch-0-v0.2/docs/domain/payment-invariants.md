# Payment Invariants

These rules are mandatory. Each has at least one test in the traceability matrix.

| ID | Invariant |
|---|---|
| P-001 | Every payment belongs to exactly one merchant. |
| P-002 | A payment’s amount and currency are positive, valid, and immutable after authorization begins. |
| P-003 | Only transitions listed in the payment state-machine specification are allowed. |
| P-004 | Total captured amount never exceeds total successfully authorized amount. |
| P-005 | Total refunded amount never exceeds total effectively captured amount. |
| P-006 | A timeout or connection loss is not automatically classified as provider failure. |
| P-007 | Ambiguous provider execution produces an explicit `*_UNKNOWN` state. |
| P-008 | One provider operation key maps to at most one effective external operation. |
| P-009 | The same idempotency key and semantic request return the original result or current in-progress result. |
| P-010 | The same idempotency key with a different semantic request is rejected. |
| P-011 | Idempotency scope includes merchant, operation type, and key; keys do not collide across merchants. |
| P-012 | Provider callbacks are deduplicated by provider and provider event ID. |
| P-013 | An older or out-of-order provider event cannot regress a terminal or more authoritative state. |
| P-014 | A capture cannot start while another capture for the same remaining authorization is in progress or unknown unless policy proves it is safe. |
| P-015 | Cancellation cannot be confirmed while a capture outcome is unknown. |
| P-016 | Every external attempt records operation key, attempt number, request time, deadline, result classification, and sanitized provider reference. |
| P-017 | Merchant-supplied state, provider, fee, account, and internal reference fields are ignored or rejected. |
| P-018 | A payment timeline is append-only from the product perspective; corrections append new evidence. |
| P-019 | Each successful capture has one source financial event ID used for ledger-posting idempotency. |
| P-020 | Unknown outcomes are resolved only through authoritative provider query, signed callback, statement evidence, or an explicitly audited manual process. |

## Semantic request hash

The idempotency hash must include all fields that change the business effect and exclude transport-only fields such as trace ID. Canonicalization rules must be versioned.
| P-021 | An ambiguous cancellation enters `CANCELLATION_UNKNOWN`; capture and duplicate cancellation remain blocked until authoritative resolution. |
| P-022 | Every `*_UNKNOWN` state stores the exact pre-operation state and amounts so confirmed non-execution restores deterministically. |

