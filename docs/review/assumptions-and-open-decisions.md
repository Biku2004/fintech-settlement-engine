# Assumptions and Remaining Open Decisions

## Fixed for Batch 1

- Java 25, Spring Boot 4.1.
- Modular monolith plus separate provider simulator.
- jOOQ, Flyway, PostgreSQL.
- Spring Modulith plus ArchUnit.
- UUIDv7/PostgreSQL `uuid`.
- Java `long` and PostgreSQL `BIGINT` minor units.
- Runtime DB role plus immutability triggers.
- Base package `com.bikash.fintechsettlement`.
- API uses `amountMinor` plus currency.
- Fee baseline: integer basis points/fixed minor units and HALF_UP policy version.

## Intentionally deferred without blocking Batch 1

- Exact identity provider/vendor; JWT contract remains fixed.
- Cloud provider/managed PostgreSQL/secret manager.
- Kafka/Redis/Debezium vendors and sizing until Batch 9.
- Legal retention confirmation before production claims.
- Non-zero reconciliation tolerance; baseline is exact.
- Real-provider sandbox adapter, which is optional and separate.

Any deferred choice must be resolved before code or operational claims depend on it.
