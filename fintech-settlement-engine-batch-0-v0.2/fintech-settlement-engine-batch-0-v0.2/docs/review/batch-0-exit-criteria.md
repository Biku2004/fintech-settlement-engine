# Batch 0 Exit Criteria — v0.2

## Internally validated design criteria

- [x] First slice and excluded scope are consistent.
- [x] Personas, permissions, workflows, unknown states, and safe product actions are defined.
- [x] Glossary and all domain/security invariants are uniquely numbered.
- [x] Payment state machine has deterministic unknown recovery and cancellation semantics.
- [x] Ledger account taxonomy, posting policies, fee/rounding, reversal, and source identity are defined.
- [x] Stage 1 modules, ownership, transaction boundaries, relational constraints, and extraction consequences are explicit.
- [x] Provider calls occur outside database transactions.
- [x] Threat model includes assets, attacker inputs, trust boundaries, risk ownership, and high-risk controls.
- [x] Failure matrix includes authorization, capture, cancellation, refund, ledger, payout, webhook, import, outbox, policy, and audit dependencies.
- [x] API, errors, idempotency, event evolution, observability, audit, SLI/SLO, capacity, DR, and runbooks are specified.
- [x] Every invariant has a concrete planned test; failure injection/replay scenarios are mapped.
- [x] P0/P1 documentation findings are closed; P2 design findings are closed.

## Evidence intentionally not claimable in Batch 0

- [ ] User/principal approval of this constitution.
- [ ] Compiled code and architecture tests.
- [ ] PostgreSQL grants/triggers/migrations executed.
- [ ] Runtime SLO/load evidence.
- [ ] Actual backup restore and replay game day.
- [ ] Deployment rollback and live alert/dashboard evidence.

Unchecked evidence is owned by later implementation batches and is not represented as passed.

## Approval result

After user approval, Batch 1 may create only the Money and Ledger Domain Kernel, migrations/roles needed for it, module verification, and mapped tests. Future services remain unscaffolded.
