# Core Product Workflows

## Create and authorize

1. Merchant sends a valid idempotent create request.
2. Platform returns intent and timeline links.
3. Authorization command reserves an operation and shows `PROCESSING`.
4. Known success shows `AUTHORIZED`; rejection shows public reason; ambiguity shows `AUTHORIZATION_UNKNOWN` and disables independent retry.
5. Operations view shows attempt time, deadline, sanitized provider reference, evidence source, and safe next action.

## Capture and ledger evidence

1. Merchant selects amount not exceeding remaining authorization.
2. Platform validates idempotency and optimistic version.
3. Provider simulator executes outside database transaction.
4. Confirmed success atomically updates payment, posts `capture-v1`, and appends outbox event.
5. Timeline links the capture operation, source financial event, and ledger transaction.
6. Ambiguous result becomes `CAPTURE_UNKNOWN`; no duplicate capture button is offered.

## Cancel unused authorization

- Authorized payment: confirmed cancellation becomes `CANCELLED`.
- Partially captured payment: confirmed cancellation closes only remaining authorization and keeps `PARTIALLY_CAPTURED`.
- Ambiguity becomes `CANCELLATION_UNKNOWN`; capture/cancel actions are disabled until provider query/callback resolves it.
- Late cancellation evidence cannot erase an effective capture.

## Unknown-operation investigation

The product separates:

```text
Business state
Operation/attempt state
Evidence sources
Reconciliation status
Safe next actions
```

Actions include refresh provider status, wait for callback, link statement evidence later, escalate, or perform privileged manual resolution. “Retry payment” is never offered while execution may have occurred.

## Refund, settlement, and reconciliation (later batches)

Refunds show original capture, prior refunds, fee treatment, available payable/receivable effect, and unknown outcome. Settlement shows immutable item composition and payout evidence. Reconciliation shows exact classification, matching rule version, original evidence, and append-only resolution.

## Error and empty states

- no payments: explain first integration step;
- no evidence yet: state that processing has not produced authoritative evidence;
- permission denied/not found: same safe resource response across tenants;
- dependency unavailable pre-execution: show safe retry guidance;
- unknown outcome: high-visibility warning, evidence age, owner, and blocked actions;
- stale view: show last refreshed time and require refresh before privileged action.
