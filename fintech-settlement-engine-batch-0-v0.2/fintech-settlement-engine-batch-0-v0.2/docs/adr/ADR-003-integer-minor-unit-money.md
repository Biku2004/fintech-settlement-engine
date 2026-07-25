# ADR-003 — Integer Minor-Unit Money

**Status:** Accepted

## Context
Binary floating point cannot represent many decimal financial values exactly.

## Decision
Use Java `long` and PostgreSQL `BIGINT` minor units plus ISO currency. Use checked arithmetic and configured amount limits. Public APIs use integer `amountMinor`. Fee division uses recorded `HALF_UP` policy in the baseline.

## Alternatives
- `double`/`float`: rejected for precision.
- unrestricted `BigDecimal`: rejected as the primary stored representation because scale mistakes remain possible; it may be used transiently only behind currency-aware conversion.
- PostgreSQL `numeric`: rejected for baseline domain consistency and simpler overflow limits.

## Consequences
Currency exponent validation is mandatory. Limits must leave arithmetic headroom. All conversions occur at boundaries.

## Revisit criteria
A required instrument cannot be represented in supported minor units or configured maximum values exceed safe 64-bit arithmetic.
