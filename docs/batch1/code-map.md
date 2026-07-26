# Batch 1 Code Map — v0.3.1

| Concern | Main package/class |
|---|---|
| Money | `shared.money.Money` |
| Currency mismatch | `shared.money.CurrencyMismatchException` |
| UUIDv7 identity | `shared.identity.UuidV7Generator`, `UuidV7` |
| Platform identity | `ledger.domain.identity.PlatformId` |
| Account taxonomy | `ledger.domain.account` |
| Posting identity | `ledger.domain.posting.PostingContext` |
| Canonical request fingerprint | `PostingCanonicalizer` |
| Policy authorization gate | `PostingPolicyValidator` |
| Opaque validated command | `ValidatedPostingCommand` |
| Immutable transaction | `ledger.domain.transaction.LedgerTransaction` |
| Capability-protected construction | `LedgerKernel.Access`, `LedgerTransactionFactory` |
| Balance validation | `LedgerTransactionFactory` |
| Capture entries | `CapturePostingPolicy` |
| Refund shortfall | `RefundPostingPolicy` |
| Settlement | `SettlementPostingPolicy` |
| Reserve hold/release | `ReservePostingPolicy` |
| Exact full reversal | `ReversalPostingPolicy`, `LedgerKernel.validateReversal` |
| Idempotency/source dedupe | `LedgerKernel` |
| Authoritative balance | `LedgerBalanceCalculator` |
| Deterministic lock order | `AccountLockOrder`, `LedgerKernel.lockOrder` |
| Security regression tests | `PostingPolicySecurityTest`, `Batch1SelfTest` |
| Compile-time bypass gates | `scripts/verify-batch1.sh` |
| Security review | `docs/security-review` |
