# Batch 1 Ledger Invariant Coverage

| Invariant | Batch 1 status | Evidence |
|---|---|---|
| L-001 balanced totals | Implemented | `LedgerTransactionFactory`; unbalanced self-test/JUnit/property tests |
| L-002 integer minor units | Implemented | `Money` stores `long`; static validation rejects money `double`/`float` fields |
| L-003 currency required | Implemented | `Money`, `LedgerAccount`, `PostingLine`, policies |
| L-004 immutable objects | Implemented in domain | records/final class/defensive copies; DB immutability deferred |
| L-005 corrections use reversals | Implemented for full reversal | `ReversalPostingPolicy` and kernel reversal link |
| L-006 atomic transaction+entries | Domain object is atomic | database transaction proof deferred |
| L-007 no partial posting | In-memory kernel commits object only after full validation | PostgreSQL rollback proof deferred |
| L-008 source event uniqueness | Implemented | `LedgerKernel.bySource` |
| L-009 idempotency conflict | Implemented | canonical fingerprint and conflict exception |
| L-010 transaction currency | Implemented | line/factory/policy validation |
| L-011 two-sided entries | Implemented | factory validation |
| L-012 positive magnitudes | Implemented | `PostingLine`, `LedgerEntry` |
| L-013 checked arithmetic | Implemented | `Math.*Exact`, fee and balance checks |
| L-014 deterministic lock order | Helper implemented | `AccountLockOrder`; DB concurrency proof deferred |
| L-015 reversal remaining amount | Full reversal implemented | duplicate full reversal rejected; partial reversal deferred |
| L-016 no self/cyclic reversal | Implemented for supported full-reversal graph | self/reversal-of-reversal rejected |
| L-017 entries authoritative | Implemented | `LedgerBalanceCalculator` |
| L-018 stale snapshot cannot authorize | Structural guarantee | kernel write path never accepts snapshot; DB balance authorization deferred |
| L-019 posting metadata | Implemented | context/source/policy/correlation required |
| L-020 DB denies mutation | Deferred | requires PostgreSQL roles/triggers |
| L-021 alarm metric/log sanitation | Deferred | requires observability adapter |
| L-022 currency scale boundary | Implemented | USD/JPY/KWD exact conversion tests |

## Batch 1 exit rule

Domain-complete invariants must pass before persistence work begins. Database-only and telemetry-only portions stay explicitly open rather than being simulated.
