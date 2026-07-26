# Ledger Invariants

| ID | Invariant |
|---|---|
| L-001 | For every committed ledger transaction, total debits equal total credits. |
| L-002 | Money uses integer minor units; `double` and `float` are prohibited. |
| L-003 | Every amount has an ISO currency, and every ledger account has one configured currency. |
| L-004 | Ledger transactions and entries are immutable after commit. |
| L-005 | Corrections are new reversal or adjustment transactions, never edits. |
| L-006 | A transaction and all entries commit atomically. |
| L-007 | A failed posting creates no partial transaction or entry set. |
| L-008 | One source financial event maps to at most one effective ledger transaction. |
| L-009 | A posting idempotency key reused with different canonical entries is rejected. |
| L-010 | All accounts in a transaction use the transaction currency. |
| L-011 | Every transaction has at least two entries and at least one debit and one credit. |
| L-012 | Entry amount is strictly positive; direction carries debit or credit meaning. |
| L-013 | Checked arithmetic detects overflow before commit. |
| L-014 | Account locks are acquired in deterministic account-ID order for balance-affecting writes. |
| L-015 | A reversal references exactly one original transaction and cannot reverse more than the un-reversed effective amount. |
| L-016 | A transaction cannot reverse itself or form a reversal cycle. |
| L-017 | Balance snapshots are derived views; immutable entries remain authoritative. |
| L-018 | A stale snapshot can affect read latency but cannot authorize an invalid write. |
| L-019 | Every posting records source type, source ID, correlation ID, and posting policy version. |
| L-020 | Database permissions deny application-level update and delete of committed ledger entries. |
| L-021 | A ledger imbalance attempt is rejected, logged without sensitive payloads, and increments a domain alarm metric. |
| L-022 | Currency minor-unit rules are validated at boundaries; seed examples use USD but the domain remains currency-aware. |

## Capture posting example

For a USD 100.00 capture with a USD 3.00 platform fee:

```text
Debit   Processor Clearing       USD 100.00
Credit  Merchant Payable         USD  97.00
Credit  Platform Fee Revenue     USD   3.00
```

The transaction is valid because debit total equals credit total.
