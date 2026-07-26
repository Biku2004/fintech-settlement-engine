# Batch 1 Code Map

| Concern | Main package/class |
|---|---|
| Money | `shared.money.Money` |
| Currency mismatch | `shared.money.CurrencyMismatchException` |
| Account taxonomy | `ledger.domain.account` |
| Posting identity | `ledger.domain.posting.PostingContext` |
| Canonical request fingerprint | `PostingCanonicalizer` |
| Immutable transaction | `ledger.domain.transaction.LedgerTransaction` |
| Balance validation | `LedgerTransactionFactory` |
| Capture entries | `CapturePostingPolicy` |
| Refund shortfall | `RefundPostingPolicy` |
| Settlement | `SettlementPostingPolicy` |
| Reserve hold/release | `ReservePostingPolicy` |
| Full reversal | `ReversalPostingPolicy` |
| Idempotency/source uniqueness proof | `ledger.domain.kernel.LedgerKernel` |
| Authoritative balances | `LedgerBalanceCalculator` |
| Future database lock order | `AccountLockOrder` |
| Offline test harness | `tests/selftest/.../Batch1SelfTest` |
