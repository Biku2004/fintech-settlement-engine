# Security Policy

## Project safety boundary

This portfolio project must never receive, store, log, or process real card numbers, CVV values, real provider credentials, or production merchant secrets. Use only synthetic identifiers and the provider simulator.

## Reporting

Do not open a public issue containing a credential, personal data, or exploit payload. Record the affected component, invariant, reproduction using synthetic data, and the expected financial/security impact.

## Non-negotiable controls

- No framework or persistence concern may weaken ledger invariants.
- No correction mutates committed ledger history.
- No identifier is authorization.
- No logs contain payment payloads, secrets, or untrusted free text.
