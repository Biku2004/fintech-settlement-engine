# ADR-008 — jOOQ, Spring Modulith, and ArchUnit

**Status:** Accepted

## Context
The ledger needs explicit SQL/locking while the monolith needs enforceable logical boundaries.

## Decision
Use jOOQ for persistence, Spring Modulith for application-module definition/verification, and ArchUnit for dependency rules. Domain code remains framework-independent.

## Alternatives
Spring Data JDBC was considered but rejected for the ledger baseline because critical SQL and locking should remain explicit. Maven modules alone were rejected because they do not fully express runtime/application boundaries.

## Consequences
Build setup includes jOOQ generation and architecture tests. Developers must understand SQL. Module interfaces become deliberate.

## Revisit criteria
A tool creates unacceptable build/runtime cost without reducing correctness, and an alternative proves equivalent explicitness and boundary enforcement.
