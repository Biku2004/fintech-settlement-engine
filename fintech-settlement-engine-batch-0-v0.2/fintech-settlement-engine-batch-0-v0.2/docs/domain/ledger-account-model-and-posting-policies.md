# Ledger Account Model and Posting Policies

## 1. Accounting model

| Account type | Normal balance | Increase | Decrease |
|---|---|---|---|
| Asset | Debit | Debit | Credit |
| Liability | Credit | Credit | Debit |
| Revenue | Credit | Credit | Debit |
| Expense | Debit | Debit | Credit |
| Equity | Credit | Credit | Debit |

A balanced transaction can still be financially wrong. Posting policies therefore define allowed account roles, directions, sources, and calculations in addition to debit/credit equality.

## 2. Baseline account roles

| Role | Type | Scope | Purpose |
|---|---|---|---|
| `PROCESSOR_CLEARING` | Asset | Platform + currency | Amount owed by/held at the simulated processor |
| `SETTLEMENT_CASH` | Asset | Platform + currency | Cash used for merchant payouts |
| `MERCHANT_PAYABLE` | Liability | Merchant + currency | Captured funds owed to a merchant |
| `MERCHANT_RECEIVABLE` | Asset | Merchant + currency | Amount merchant owes platform after post-settlement refund/dispute |
| `PLATFORM_FEE_REVENUE` | Revenue | Platform + currency | Earned platform fee |
| `REFUND_LIABILITY` | Liability | Merchant + currency | Approved but not yet externally completed refund obligation, when used in a later policy |
| `DISPUTE_RESERVE` | Liability | Merchant + currency | Merchant funds held pending dispute/reserve release |

Each account has one immutable currency and one immutable owner scope. A posting cannot mix merchant scopes unless the posting policy explicitly includes a platform clearing account.

## 3. Capture policy `capture-v1`

For gross amount `G`, platform fee `F`, and merchant net `N = G - F`:

```text
Debit   PROCESSOR_CLEARING        G
Credit  MERCHANT_PAYABLE          N
Credit  PLATFORM_FEE_REVENUE      F
```

Guards:

- `G > 0`, `0 <= F <= G`, `N >= 0`;
- source type is `PAYMENT_CAPTURE_CONFIRMED`;
- source ID is the capture financial-event ID;
- all entries use the payment currency;
- one source event creates at most one effective posting.

## 4. Settlement policy `settlement-v1`

```text
Debit   MERCHANT_PAYABLE          payout
Credit  SETTLEMENT_CASH           payout
```

The payout cannot exceed eligible payable after reserves and prior adjustments.

## 5. Refund policy `refund-v1`

The baseline assumes the platform fee is refunded proportionally using the fee policy recorded on the capture.

Before settlement, available merchant payable funds the merchant portion:

```text
Debit   MERCHANT_PAYABLE          merchantRefundShare
Debit   PLATFORM_FEE_REVENUE      refundedFeeShare
Credit  PROCESSOR_CLEARING        totalRefund
```

If merchant payable is insufficient because funds were already settled, the shortfall uses `MERCHANT_RECEIVABLE` rather than allowing a liability account to silently become an asset:

```text
Debit   MERCHANT_PAYABLE          availablePayable
Debit   MERCHANT_RECEIVABLE       merchantShortfall
Debit   PLATFORM_FEE_REVENUE      refundedFeeShare
Credit  PROCESSOR_CLEARING        totalRefund
```

## 6. Reserve policies

Hold:

```text
Debit   MERCHANT_PAYABLE
Credit  DISPUTE_RESERVE
```

Release:

```text
Debit   DISPUTE_RESERVE
Credit  MERCHANT_PAYABLE
```

## 7. Reversal policy

A reversal:

- references exactly one original ledger transaction;
- copies each original entry with debit/credit direction inverted;
- uses the same currency and absolute amount;
- records a new source event and reason;
- cannot exceed the original transaction's remaining unreversed amount;
- never edits the original transaction.

## 8. Fee and rounding policy

- Fee rates are stored as integer basis points plus optional fixed minor units.
- Multiplication uses checked arithmetic.
- Division uses `HALF_UP` to the currency's minor unit for the baseline.
- The exact fee policy ID and version are stored on the payment and ledger transaction.
- A policy change never recalculates an already confirmed capture.

## 9. Posting identity

Every posting has:

```text
postingId (UUIDv7)
postingIdempotencyKey
sourceType
sourceId
postingPolicyId
postingPolicyVersion
merchantId when merchant-scoped
currency
correlationId
occurredAt
recordedAt
```

Uniqueness is enforced independently on posting idempotency and `(sourceType, sourceId)`.
