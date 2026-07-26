# Payment State Machine v0.2

## 1. State model

```text
CREATED
REQUIRES_AUTHORIZATION
AUTHORIZED
AUTHORIZATION_FAILED
AUTHORIZATION_UNKNOWN
PARTIALLY_CAPTURED
CAPTURED
CAPTURE_UNKNOWN
CANCELLATION_UNKNOWN
CANCELLED
PARTIALLY_REFUNDED
REFUNDED
REFUND_UNKNOWN
DISPUTED          # later batch
REVERSED           # later batch
```

Payment state is combined with amounts, `authorizationClosed`, active attempt data, and an optional unknown-operation snapshot. State alone is never sufficient to authorize a financial command.

## 2. Deterministic unknown-operation snapshot

Entering an unknown state atomically stores:

```text
unknownOperationType
unknownOperationKey
preUnknownState
preUnknownAuthorizedAmountMinor
preUnknownCapturedAmountMinor
preUnknownRefundedAmountMinor
preUnknownAuthorizationClosed
unknownSince
```

If authoritative evidence proves non-execution, the aggregate restores these exact values. The implementation must never infer “previous stable state” from current history at runtime.

## 3. Authorization transitions

| Current | Trigger | Guard | Next |
|---|---|---|---|
| `CREATED` | authorization command reserved | request valid; no attempt active | `REQUIRES_AUTHORIZATION` |
| `REQUIRES_AUTHORIZATION` | transport proves request never left client boundary | persisted attempt is `FAILED_PRE_EXECUTION` | `CREATED` |
| `REQUIRES_AUTHORIZATION` | provider rejects or confirms non-execution | authoritative evidence | `AUTHORIZATION_FAILED` |
| `REQUIRES_AUTHORIZATION` | provider confirms success | amount/currency/reference match | `AUTHORIZED` |
| `REQUIRES_AUTHORIZATION` | execution may have occurred | operation key persisted | `AUTHORIZATION_UNKNOWN` |
| `AUTHORIZATION_UNKNOWN` | success confirmed | matching authoritative evidence | `AUTHORIZED` |
| `AUTHORIZATION_UNKNOWN` | rejection/non-execution confirmed | matching authoritative evidence | `AUTHORIZATION_FAILED` |

`AUTHORIZATION_FAILED` is terminal for that payment intent in the baseline. A merchant creates a new intent rather than mutating the failed intent.

## 4. Capture transitions

| Current | Trigger | Guard | Next |
|---|---|---|---|
| `AUTHORIZED` | partial capture confirmed | `0 < amount < remainingAuthorization` | `PARTIALLY_CAPTURED` |
| `AUTHORIZED` | final capture confirmed | amount equals remaining authorization | `CAPTURED` |
| `PARTIALLY_CAPTURED` | additional partial capture confirmed | cumulative capture < authorization and no refund exists | `PARTIALLY_CAPTURED` |
| `PARTIALLY_CAPTURED` | final capture confirmed | cumulative capture = authorization and no refund exists | `CAPTURED` |
| `AUTHORIZED` or `PARTIALLY_CAPTURED` | capture execution ambiguous | no other unknown operation | `CAPTURE_UNKNOWN` |
| `CAPTURE_UNKNOWN` | partial capture confirmed | cumulative capture < authorization | `PARTIALLY_CAPTURED` |
| `CAPTURE_UNKNOWN` | full capture confirmed | cumulative capture = authorization | `CAPTURED` |
| `CAPTURE_UNKNOWN` | non-execution confirmed | unknown snapshot exists | exact `preUnknownState` and amounts |

The baseline prohibits additional capture after any successful refund. This restriction can be revisited only with a new ADR and posting policy.

## 5. Cancellation transitions

Cancellation closes unused authorization; it never removes an effective capture.

| Current | Trigger | Guard | Next/effect |
|---|---|---|---|
| `AUTHORIZED` | cancellation confirmed | no capture active or unknown | `CANCELLED`; `authorizationClosed=true` |
| `PARTIALLY_CAPTURED` | remaining authorization cancellation confirmed | no capture/refund active or unknown | remain `PARTIALLY_CAPTURED`; `authorizationClosed=true` |
| `AUTHORIZED` or `PARTIALLY_CAPTURED` | cancellation ambiguous | operation key persisted | `CANCELLATION_UNKNOWN` |
| `CANCELLATION_UNKNOWN` | cancellation confirmed and pre-state was `AUTHORIZED` | authoritative evidence | `CANCELLED` |
| `CANCELLATION_UNKNOWN` | cancellation confirmed and pre-state was `PARTIALLY_CAPTURED` | authoritative evidence | `PARTIALLY_CAPTURED`; `authorizationClosed=true` |
| `CANCELLATION_UNKNOWN` | non-execution confirmed | unknown snapshot exists | exact `preUnknownState` and amounts |

No capture or second cancellation may start while cancellation is unknown.

## 6. Refund transitions

| Current | Trigger | Guard | Next |
|---|---|---|---|
| `CAPTURED` or `PARTIALLY_CAPTURED` | partial refund confirmed | cumulative refund < captured amount | `PARTIALLY_REFUNDED` |
| `PARTIALLY_REFUNDED` | additional partial refund confirmed | cumulative refund < captured amount | `PARTIALLY_REFUNDED` |
| captured/refunded state | full effective refund confirmed | cumulative refund = captured amount | `REFUNDED` |
| `CAPTURED`, `PARTIALLY_CAPTURED`, or `PARTIALLY_REFUNDED` | refund ambiguous | no other unknown operation | `REFUND_UNKNOWN` |
| `REFUND_UNKNOWN` | partial refund confirmed | cumulative refund < captured amount | `PARTIALLY_REFUNDED` |
| `REFUND_UNKNOWN` | full refund confirmed | cumulative refund = captured amount | `REFUNDED` |
| `REFUND_UNKNOWN` | non-execution confirmed | unknown snapshot exists | exact `preUnknownState` and amounts |

## 7. Global guards

- All unlisted transitions are rejected.
- Only the domain aggregate may transition state.
- A payment has at most one active provider side effect or unknown operation.
- `capturedAmountMinor <= authorizedAmountMinor`.
- `refundedAmountMinor <= capturedAmountMinor`.
- Optimistic-lock conflicts cause reload and guard re-evaluation; they never blindly repeat an external side effect.
- Late/out-of-order evidence may add timeline evidence but cannot regress a more authoritative state.
- Manual resolution requires elevated permission, reason, evidence reference, and audit event.
