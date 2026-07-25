# Reconciliation Invariants

| ID | Invariant |
|---|---|
| R-001 | Every imported statement has a source identity, checksum, import time, schema version, and immutable raw-object reference. |
| R-002 | The same statement or row cannot create duplicate effective reconciliation evidence. |
| R-003 | Matching is deterministic for a recorded rules version. |
| R-004 | Exact matches, tolerated differences, duplicates, missing internal records, and missing external records are distinct outcomes. |
| R-005 | A difference is never silently discarded. |
| R-006 | Every exception remains queryable after resolution. |
| R-007 | Resolution appends a resolution action; it does not delete or rewrite original evidence. |
| R-008 | Manual resolution records principal, reason, evidence, timestamp, and before/after classification. |
| R-009 | Reconciliation may resolve an unknown payment operation but cannot bypass payment and ledger invariants. |
| R-010 | If provider evidence confirms success, the corresponding financial posting remains idempotent. |
| R-011 | If provider evidence confirms non-execution, the system may return to a safe retryable state only through an explicit transition. |
| R-012 | File parsing is bounded by file size, row count, column count, field length, and processing time. |
| R-013 | CSV exports neutralize spreadsheet formula injection. |
| R-014 | Amount comparison uses currency-aware integer minor units and recorded rounding policy. |
| R-015 | Reconciliation runs are restartable from persisted checkpoints without duplicating matches or resolutions. |
