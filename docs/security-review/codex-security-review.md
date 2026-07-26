# Codex Security Review — Batch 1 v0.3.0

## Scope and method

The review covered the complete Batch 1 repository snapshot: production Java source, self-tests, Maven test sources, POMs, validation scripts, and Batch 0-derived financial invariants. The workflow followed separate threat-model, discovery, validation, attack-path, remediation, and bypass-review phases.

The scan ran in prompt-only Chat mode because the Codex desktop workbench, scan finalizer, and one-finding subagents were not available in this host. The source review, exploit probes, fixes, and validation are real; this directory is a distributable review package rather than a sealed Codex workbench export.

## Security objective

The critical asset is financial integrity. A caller must not be able to create an effective ledger movement merely because debit and credit totals balance. Before mutation, the kernel must also verify:

- posting policy identity and version;
- required source financial-event type;
- permitted account roles and directions;
- merchant and platform ownership;
- registered immutable account definitions;
- exact reversal relationship;
- idempotency and source-event uniqueness.

## Findings

| ID | Finding | Original severity | Outcome |
|---|---|---:|---|
| CS-001 | Caller-controlled policy label authorized financially wrong balanced postings | High | Fixed |
| CS-002 | Posting policies were not bound to financial source-event types | Medium | Fixed |
| CS-003 | One transaction could mix platform accounts owned by different platforms | Medium | Fixed |
| CS-004 | Public/package construction APIs bypassed the ledger kernel boundary | Medium | Fixed |

No command execution, deserialization, network, filesystem, SQL, template, or secret-handling sink exists in Batch 1 production code. The meaningful security surface is therefore business-logic integrity, scope isolation, immutable construction, replay handling, and resource bounds.

## Refactor result

The patch introduces one mandatory validation choke point in `LedgerKernel.post`, source-type checks in both policy builders and the kernel validator, platform identity ownership at account registration, exact role/direction/cardinality rules, bounded reversal input, and an unforgeable transaction-construction capability. `LedgerTransaction` is privately constructed, including against same-package classpath spoofing.

## Validation summary

- Original exploit probe: three unauthorized inputs created transactions.
- Post-fix exploit probe: all three inputs rejected; transaction count remained zero.
- Security self-tests: forged policy, wrong source type, mixed platform owners, and oversized reversal rejected.
- Negative compilation probes: external and same-package code cannot mint `LedgerKernel.Access`, construct the factory, or invoke the private transaction constructor.
- Full offline verification: passed five consecutive runs.
- Deterministic checks: 33 passed.
- Randomized checks: 2,000 balanced capture postings passed.
- Concurrency check: 32 duplicate submissions produced one created transaction.
- Strict compilation: 60 production files and 7 Maven test-source files passed the available offline gates.

## Residual risk

The security rating applies to the Batch 1 domain-kernel scope, not to a production payment platform. Authentication, principal-to-merchant authorization, PostgreSQL constraints, immutable database grants/triggers, durable idempotency, audit emission, bounded retention, and real JDK 25 Maven/Testcontainers execution remain future gates.

## Rating

| Area | Before | After |
|---|---:|---:|
| Financial-integrity security | 6.4/10 | 9.1/10 |
| Domain correctness | 8.5/10 | 9.3/10 |
| Architecture and boundaries | 8.2/10 | 9.1/10 |
| Test evidence | 8.4/10 | 9.2/10 |
| Maintainability | 8.6/10 | 9.0/10 |
| Production readiness | 4.8/10 | 6.3/10 |
| Batch 1 scope overall | 8.0/10 | 9.1/10 |

Production readiness remains lower by design because this batch intentionally contains no authenticated service or durable database implementation.
