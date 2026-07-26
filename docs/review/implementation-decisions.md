# Batch 1 Implementation Decisions

These decisions close the v0.1 P0 implementation blockers.

## Persistence: jOOQ

jOOQ is selected because ledger writes require visible SQL, deliberate locking, explicit transactions, generated database types, and easy inspection of uniqueness/check constraints. Spring Data JDBC was rejected for the baseline because it provides less direct control over complex posting and locking statements.

## Module enforcement: Spring Modulith plus ArchUnit

Spring Modulith defines and verifies application modules. ArchUnit adds explicit rules such as:

- `payment` may call the public `ledger` application port but never ledger persistence classes;
- `ledger` cannot depend on payment;
- domain packages cannot depend on Spring MVC or jOOQ;
- one module cannot access another module's internal package.

## Identifier strategy: UUIDv7

Application-generated UUIDv7 values are used for public and internal aggregate IDs. They are stored as PostgreSQL `uuid`, remain globally unique, avoid sequence disclosure, and have better index locality than random UUIDv4. External provider IDs remain opaque strings.

## Money and database numeric type

Domain values use Java `long` minor units and `Math.addExact`, `subtractExact`, and `multiplyExact`. PostgreSQL stores values in `BIGINT`. Every endpoint and database constraint enforces a configured maximum absolute amount below `Long.MAX_VALUE` to leave arithmetic headroom.

## Ledger immutability

Committed ledger transactions and entries are protected by all of:

1. no update/delete method in application ports;
2. runtime database role lacks `UPDATE` and `DELETE` on immutable tables;
3. triggers reject mutation even through an incorrectly privileged runtime path;
4. a separate migration/maintenance role is audited and not used by the application;
5. correction occurs only through reversal or adjustment postings.

## Base package and modules

Base package: `com.bikash.fintechsettlement`

Initial modules:

```text
bootstrap
shared.money
shared.identity
payment
ledger
outbox
audit
providersimulator (separate application)
testsupport
```

Settlement and reconciliation packages may exist as documentation namespaces but are not scaffolded until their batches.
