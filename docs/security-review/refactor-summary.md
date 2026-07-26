# Security Refactor Summary

## Choke points added

1. `LedgerKernel.post` performs policy validation before replay registration or state mutation.
2. `PostingPolicyValidator` centrally allowlists policy version, source type, account-role/direction shape, cardinality, merchant requirement, reversal mode, and reversal input bounds.
3. Policy builders independently reject the wrong source type, giving early caller feedback while preserving kernel-side defense in depth.
4. `LedgerKernel` owns one `PlatformId`; foreign platform accounts cannot be registered.
5. `LedgerTransactionFactory` requires an unforgeable kernel capability and validated command.
6. `LedgerTransaction` is privately constructed.

## Why the refactor is narrow

The patch does not introduce Spring, persistence, HTTP, serialization, or a new service. Existing posting-policy APIs and ledger outcomes remain intact. The changes are limited to the integrity boundaries needed to prevent unapproved financial objects and cross-scope postings.

## Files changed for security behavior

- `LedgerKernel.java`
- `PlatformId.java`
- `PostingPolicyValidator.java`
- `ValidatedPostingCommand.java`
- `PostingPolicySupport.java`
- capture/refund/settlement/reserve policy builders
- `LedgerTransactionFactory.java`
- `LedgerTransaction.java`
- security tests, self-test, and verification script

## Compatibility impact

`LedgerKernel` now requires `PlatformId`, and direct factory construction is intentionally removed. Those are security-relevant breaking changes appropriate for a pre-1.0 domain kernel. Normal posting through registered accounts and policy builders remains supported.
