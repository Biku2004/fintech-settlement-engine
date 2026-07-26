# ADR-009 — UUIDv7 Identifiers

**Status:** Accepted

## Context
Identifiers must be globally unique, non-enumerable, and index-friendly.

## Decision
Generate UUIDv7 in the application for aggregate, operation, event, posting, and audit IDs; store as PostgreSQL `uuid`. Provider IDs remain opaque strings.

## Alternatives
Sequences reveal ordering/counts and complicate extraction. UUIDv4 has poorer index locality. ULID requires a non-native database representation or conversion conventions.

## Consequences
Clock quality is monitored, but uniqueness does not rely solely on time. IDs are never used as authorization.

## Revisit criteria
A platform-standard identifier with equal safety/locality becomes mandatory.
