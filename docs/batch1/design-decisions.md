# Batch 1 Design Decisions

## Money can be negative; ledger entries cannot

A balance can legitimately be negative. Therefore `Money` represents signed minor units. A `PostingLine` and `LedgerEntry` require a strictly positive magnitude, while `EntryDirection` carries debit/credit meaning.

## Canonical fingerprint excludes generated metadata

The posting fingerprint includes the financial source, policy, merchant, reversal reference, accounts, directions, amounts, and currencies. It excludes generated transaction ID, correlation ID, timestamps, reason text, and the idempotency key itself. This permits a safe replay to return the original result even when a retry receives new transport metadata.

## Source deduplication and idempotency are separate

- Idempotency protects one caller operation key.
- Source uniqueness protects one financial event even if a different key is supplied.

An identical duplicate returns the original transaction. A changed financial effect produces a conflict.

## Full reversal only

Batch 1 supports exact full reversal and prevents a second full reversal. Partial reversal accounting requires remaining-effective-amount persistence and is deferred to the refund/reversal persistence batch.

## In-memory kernel is proof, not production storage

The synchronized in-memory kernel proves state-independent semantics and concurrency outcomes. It is intentionally replaced by PostgreSQL uniqueness constraints and transactions later.
