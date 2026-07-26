# Domain Glossary

## Payment intent

The platform’s durable representation of the merchant’s intention to collect a specific amount in a specific currency. It owns the lifecycle but does not itself prove that an external provider operation succeeded.

## Authorization

A provider decision reserving or approving an amount for later capture. Authorization is not the same as captured money.

## Capture

The operation that converts all or part of an authorized amount into a completed financial event for this project’s model.

## Partial capture

A capture smaller than the authorized amount. Further captures are allowed only up to the remaining authorized amount. Cancelling the unused remainder is deferred beyond the first vertical slice.

## Cancellation

A request to release an uncaptured authorization. Cancellation is permitted only when no conflicting capture result is unknown.

## Refund

A post-capture operation returning some or all captured value. A refund is represented by new payment records and reversing financial entries, not mutation of the original capture.

## Unknown operation

An operation for which the system cannot prove whether the provider executed the side effect. Unknown is neither success nor failure.

## Provider operation key

A deterministic key used when invoking the provider simulator so that repeated requests refer to the same intended external operation.

## Idempotency key

A client- or system-supplied key scoped to an actor and operation. The same key with the same semantic request returns the prior result; the same key with a different semantic request is rejected.

## Ledger account

A named financial bucket with a currency and account type. Examples include Processor Clearing, Merchant Payable, Platform Fee Revenue, Refund Liability, Settlement Cash, and Dispute Reserve.

## Ledger transaction

An immutable set of debit and credit entries that commits atomically and balances to zero by direction.

## Ledger entry

One debit or credit in a ledger transaction. Entries are immutable and never independently committed.

## Posting

The act of committing a balanced ledger transaction using a posting idempotency key.

## Reversal

A new ledger transaction whose entries offset an earlier transaction. The original transaction remains unchanged.

## Settlement

The process that determines how much the platform should pay a merchant for an eligible period after fees, refunds, reserves, and other adjustments.

## Settlement batch

An immutable calculation result for one merchant and settlement cycle, followed by a payout-instruction lifecycle.

## Reconciliation

Comparison of internal payment and ledger evidence with provider statements or query results.

## Reconciliation exception

A recorded difference or unmatched record requiring automated or human resolution.

## Outbox event

An event persisted in the same database transaction as the state change it describes, for reliable later publication.

## Consumer inbox

A consumer-owned record of processed event IDs used to make event handling idempotent after Kafka is introduced.

## Correlation ID

An identifier connecting all work belonging to one business request or investigation.

## Causation ID

The ID of the command or event that directly caused another event.

## Effective financial operation

The one business effect recognized by the platform after duplicates, retries, unknown outcomes, and reconciliation are considered.

## Additional v0.2 terms

- **Attempt state:** Technical execution state such as CREATED, IN_PROGRESS, FAILED_PRE_EXECUTION, REJECTED, SUCCEEDED, or UNKNOWN; it is not the payment business state.
- **Authoritative evidence order:** Signed provider query/response or callback and immutable statement evidence outrank local timeout inference; later evidence cannot violate domain invariants.
- **Source financial event:** Stable immutable identity for one confirmed external financial effect, used to prevent duplicate ledger posting.
- **Known rejection:** Evidence proves the provider declined or did not execute the operation.
- **Pre-execution failure:** Transport classification proves the remote side effect did not begin, allowing safe reacquisition under the same business key.
- **Unknown snapshot:** Exact pre-operation state/amounts plus operation identity stored when external execution may have occurred.
- **Audit event:** Append-only evidence of a privileged/security action, separate from diagnostic logs.
- **Posting policy:** Versioned mapping from a business financial event to permitted account roles, directions, and calculations.

