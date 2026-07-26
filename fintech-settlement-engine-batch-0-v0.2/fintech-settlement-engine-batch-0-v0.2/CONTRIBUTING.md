# Contributing

## Before changing the kernel

1. Identify the Batch 0 invariant affected.
2. Add or update a deterministic test.
3. Preserve framework independence in `shared.money` and `ledger.domain`.
4. Run `./scripts/verify-batch1.sh`.
5. Document any deferred persistence or operational proof rather than simulating it.

## Coding rules

- Use integer minor units.
- Use checked arithmetic.
- Keep collections immutable at domain boundaries.
- Represent corrections as new postings.
- Keep idempotency identity separate from source financial-event identity.
- Do not add Spring, jOOQ, HTTP, or serialization imports to the domain modules.
