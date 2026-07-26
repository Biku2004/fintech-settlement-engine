# Product Principles

## 1. The product is an evidence system

Every important page or API response should answer four questions:

1. What is the current business state?
2. Which attempts and events produced it?
3. What financial records were created?
4. What safe action, if any, is available next?

A status badge without evidence is insufficient for financial operations.

## 2. Never hide uncertainty

The product must not collapse `AUTHORIZATION_UNKNOWN`, `CAPTURE_UNKNOWN`, or `REFUND_UNKNOWN` into a generic failure. An operator must see:

- the operation key;
- when the request was sent;
- which response or callback is missing;
- reconciliation checks already attempted;
- whether another provider-side operation is prohibited; and
- the next safe check.

## 3. Separate business status from technical attempt status

Example:

- Payment business status: `AUTHORIZED`.
- Latest provider attempt: `TIMED_OUT_BEFORE_RESPONSE`.
- Reconciliation status: `NOT_REQUIRED`.

These values may differ and must not overwrite one another.

## 4. Financial history is append-only

The operator experience must never offer “Edit ledger entry” or “Delete transaction.” Correction uses a reversal with a reason, principal, correlation ID, and reference to the original transaction.

## 5. Make dangerous actions deliberate

A privileged action such as resolving an exception, replaying a delivery, or reversing a posting requires:

- clear target identification;
- impact preview;
- permission check;
- reason capture;
- confirmation that does not rely only on colour;
- idempotency or replay protection; and
- an audit event.

## 6. Design for investigation, not only happy paths

Timeline, search, filters, correlation IDs, event IDs, provider references, and ledger links are primary product features. They are not optional administration polish.

## 7. Money presentation

- Store integer minor units and ISO currency.
- Format for humans only at the presentation boundary.
- Always display the currency code near amounts in operator workflows.
- Do not silently combine currencies.
- Display signed effects and debit/credit direction explicitly in ledger views.

## 8. Product language

Use precise terms:

- `Failed`: execution is known not to have succeeded.
- `Unknown`: execution may have succeeded, but evidence is incomplete.
- `Rejected`: validation, policy, authorization, or provider decision prevented success.
- `Reversed`: a new compensating financial record offsets an earlier record.
- `Reconciled`: internal and external evidence have been compared and a recorded conclusion exists.
