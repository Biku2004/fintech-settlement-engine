# Remote Operation Failure Matrix v0.2

Every remote operation distinguishes definite pre-execution failure, known rejection, ambiguous execution, duplicate attempts, late/out-of-order evidence, restart, and reconciliation.

## Authorization

| Scenario | Required behaviour | Evidence/recovery |
|---|---|---|
| Definite pre-execution failure | attempt=`FAILED_PRE_EXECUTION`; `REQUIRES_AUTHORIZATION -> CREATED`; same idempotency key may retry | adapter classification proves no bytes accepted |
| Known rejection | `AUTHORIZATION_FAILED`; no automatic new attempt | signed response/query |
| Success, response lost | `AUTHORIZATION_UNKNOWN`; block independent authorization | query/callback/statement |
| Duplicate command | same operation key converges to one authorization | API and provider idempotency |
| Callback before response | process event once; later response is evidence only | provider event ID |
| Restart | resume/query from persisted attempt | attempt and operation key |
| Unresolved | remain unknown; assign review owner | age and evidence log |

## Capture

| Scenario | Required behaviour | Evidence/recovery |
|---|---|---|
| Definite pre-execution failure | preserve exact prior payment state; mark attempt retryable | adapter classification |
| Known rejection | preserve captured amounts; expose rejection | signed response/query |
| Success, response lost | `CAPTURE_UNKNOWN`; no second capture | query/callback/statement |
| Duplicate execution attempt | provider operation key and source financial event create one effective capture/posting | provider/local uniqueness |
| Late/out-of-order event | apply only if operation key matches and authority does not regress state | event ordering/authority rules |
| DB fails after provider success | unknown state is persisted if possible; otherwise reconciliation discovers orphan provider success | operation-key query/statement |
| Restart | query before retry | persisted attempt |

## Cancellation

| Scenario | Required behaviour | Evidence/recovery |
|---|---|---|
| Definite pre-execution failure | restore exact pre-command state; same key may retry | adapter classification |
| Known rejection/non-execution | preserve authorization state and `authorizationClosed=false` | response/query |
| Success, response lost | `CANCELLATION_UNKNOWN`; block capture and duplicate cancel | query/callback/statement |
| Cancel races with capture | optimistic guard permits at most one command preparation; if either external outcome is unknown, both business actions remain blocked | version + attempt records |
| Duplicate cancellation | same operation key returns one effective closure | idempotency |
| Late cancellation after full capture | record evidence but do not convert captured payment to cancelled | state/amount guard |
| Restart | query original cancellation operation | persisted snapshot/key |

## Refund

| Scenario | Required behaviour | Evidence/recovery |
|---|---|---|
| Pre-execution failure | restore exact captured/refunded state | adapter classification |
| Success, response lost | `REFUND_UNKNOWN`; no new refund | query/callback/statement |
| Duplicate | one effective refund and ledger posting | operation/source uniqueness |
| Partial refunds | cumulative total guarded against captured amount | aggregate/version |
| Restart/late event | reconcile original operation key | evidence log |

## Ledger posting

| Scenario | Required behaviour |
|---|---|
| Validation fails | no transaction header or entry commits |
| Failure after header insert | full database rollback |
| Duplicate source/idempotency | return original posting or reject changed canonical entries |
| Concurrent accounts | lock by sorted account UUID; bounded retry on deadlock/serialization |
| Imbalance | reject, sanitize log, increment critical metric |
| Process restart after commit | uniqueness returns committed posting; no duplicate |

## Settlement payout

Ambiguous submission enters `PAYOUT_UNKNOWN`; a restarted worker queries the original deterministic payout key. One merchant failure cannot block unrelated merchant partitions. Confirmed external success with failed local update is repaired through reconciliation, not a second payout.

## Merchant webhook delivery

Delivery IDs remain stable across retries. Timeout is ambiguous because the merchant may have processed the request. Retries carry the same event/delivery ID and signature timestamp policy. Permanent 4xx responses stop automatic retry and create operator-visible failure; SSRF validation occurs at activation and delivery.

## Statement import

Imports are identified by object identity and checksum. Partial parsing resumes from persisted checkpoints; duplicate files/rows do not duplicate evidence. Bounds stop oversized files/rows/fields. Raw evidence is immutable and resolution is append-only.

## Outbox publication and consumer processing

State change plus outbox append is atomic. Publisher claims bounded rows with leases. Crash after publish but before marking causes duplicate publication with the same event ID. Consumers insert inbox ID and business effect in one transaction, acknowledge only after commit, and route poison messages to controlled dead-letter handling.

## Authorization/policy and audit dependencies

Authorization failure or unavailable policy data fails closed. Failure to append a mandatory privileged audit event rolls back the privileged local action; audit publication after commit uses the outbox and stable event ID.
