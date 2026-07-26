# Batch 1 Ledger Invariant Coverage — v0.3.1

| Invariant | Batch 1 status | Evidence |
|---|---|---|
| L-001 balanced totals | Implemented | `LedgerTransactionFactory`; deterministic/property tests |
| L-002 integer minor units | Implemented | `Money` stores `long`; static validation rejects floating-point domain types |
| L-003 currency required | Implemented | `Money`, `LedgerAccount`, `PostingLine`, policies |
| L-004 immutable objects | Implemented in domain | records/final classes/defensive copies/private transaction construction; DB immutability deferred |
| L-005 corrections use reversals | Implemented for full reversal | `ReversalPostingPolicy` and kernel reversal link |
| L-006 atomic transaction+entries | Domain object is atomic | database transaction proof deferred |
| L-007 no partial posting | In-memory kernel mutates only after complete policy/account/reversal/factory validation | PostgreSQL rollback proof deferred |
| L-008 source event uniqueness | Implemented | `LedgerKernel.bySource`; source type bound to policy |
| L-009 idempotency conflict | Implemented | canonical fingerprint and conflict exception |
| L-010 transaction currency | Implemented | line/factory/policy validation |
| L-011 two-sided entries | Implemented | factory validation |
| L-012 positive magnitudes | Implemented | `PostingLine`, `LedgerEntry` |
| L-013 checked arithmetic | Implemented | `Math.*Exact`, fee and balance checks |
| L-014 deterministic lock order | Helper implemented | policy-validated `lockOrder`; DB concurrency proof deferred |
| L-015 reversal remaining amount | Full reversal implemented | exact inverse and duplicate full reversal rejected; partial reversal deferred |
| L-016 no self/cyclic reversal | Implemented for supported full-reversal graph | self/reversal-of-reversal rejected |
| L-017 entries authoritative | Implemented | `LedgerBalanceCalculator` |
| L-018 stale snapshot cannot authorize | Structural guarantee | write path never accepts a snapshot; DB balance authorization deferred |
| L-019 posting metadata | Implemented | context/source/policy/correlation required; source-policy compatibility enforced |
| L-020 DB denies mutation | Deferred | requires PostgreSQL roles/triggers |
| L-021 alarm metric/log sanitation | Deferred | requires observability adapter |
| L-022 currency scale boundary | Implemented | USD/JPY/KWD exact conversion tests |

## Additional security invariants proved in v0.3.1

- A balanced command cannot use an unsupported policy/account-role/direction shape.
- Capture, refund, settlement, reserve, and reversal policies require their approved financial source-event types.
- Platform-scoped accounts belong to one kernel-owned platform.
- Application and split-package code cannot construct authoritative transaction objects directly.
- Reversal command size is bounded before canonicalization and exact-original comparison.
