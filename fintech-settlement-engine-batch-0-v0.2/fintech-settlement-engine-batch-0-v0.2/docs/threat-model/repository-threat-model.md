# Repository Threat Model

## 1. Scope

This threat model covers the future repository containing the fintech application, provider simulator, migrations, infrastructure, operational scripts, contracts, and tests. It is repository-wide and not limited to one endpoint.

## 2. Security objectives

1. Prevent unauthorized financial commands and cross-merchant access.
2. Prevent duplicate effective payments, ledger postings, settlements, refunds, and deliveries.
3. Preserve ledger integrity and history.
4. Verify external evidence before changing financial state.
5. Protect secrets and sensitive operational data.
6. Keep privileged actions attributable and reviewable.
7. Preserve availability under bounded abuse without weakening authorization.

## 3. Critical assets

- merchant identity and authorization relationships;
- payment amount, currency, state, and version;
- provider operation keys and event IDs;
- ledger accounts, transactions, entries, and posting keys;
- merchant payable and settlement evidence;
- reconciliation statements, matches, exceptions, and resolutions;
- provider and webhook signing secrets;
- idempotency records and canonical request hashes;
- outbox/inbox event identity;
- audit records;
- administrative and service privileges;
- backups and recovery credentials.

## 4. Threat actors

- unauthenticated internet attacker;
- authenticated merchant attempting cross-tenant access;
- malicious or compromised merchant integration;
- compromised operator account;
- over-privileged service identity;
- malicious callback sender;
- malicious webhook destination;
- compromised dependency or container image;
- accidental operator or developer error;
- faulty provider producing duplicated, delayed, corrupted, or conflicting evidence.

## 5. Trust boundaries

- client to public API;
- operator browser to privileged API;
- provider callback to webhook ingress;
- statement file to importer;
- fintech application to provider simulator;
- Payment to Ledger financial-integrity boundary;
- application to PostgreSQL and secret store;
- future broker to consumers;
- webhook delivery worker to merchant-controlled network destination;
- CI/CD and operational scripts to production-like resources.

## 6. Attacker-controlled inputs

JSON, headers, idempotency keys, URLs, path/query parameters, merchant references, callback bodies, callback headers, event IDs, CSV/object files, event payloads, pagination, filters, sort fields, replay selections, reconciliation reasons, and imported text.

## 7. Highest-risk abuse cases

### TM-01 Cross-merchant object access

Attacker changes payment or merchant identifiers to read or mutate another merchant’s resource.

Controls: ownership from principal, repository scoping, object-level authorization tests, non-enumerating errors.

### TM-02 Mass assignment of internal financial fields

Attacker supplies state, fee, provider, ledger account, settlement status, or merchant ownership.

Controls: explicit request DTOs, command mapping allowlists, schema tests, ignored-field rejection.

### TM-03 Idempotency bypass

Attacker repeats one financial intent through alternate endpoints, changed canonicalization, or key scopes.

Controls: operation-scoped keys, versioned semantic hash, database uniqueness, cross-route tests.

### TM-04 Forged or replayed provider callback

Attacker submits a fake success or replays a valid event.

Controls: HMAC/signature, timestamp window, constant-time comparison, event-ID uniqueness, raw-body verification.

### TM-05 Duplicate ledger posting

Retry, race, or event replay creates a second posting.

Controls: source-event uniqueness, posting key, transaction isolation, consumer inbox later, reconciliation.

### TM-06 Ledger tampering

Compromised application or operator edits historical entries.

Controls: no mutation API, immutable model, database grants/triggers or constraints, audit, integrity verification.

### TM-07 SSRF through merchant webhook endpoint

Attacker configures loopback, metadata, private, or rebinding destination.

Controls: URL policy, DNS/IP validation at registration and delivery, egress policy, redirect restrictions, bounded response.

### TM-08 Malicious statement import

Oversized, malformed, formula-bearing, or parser-abusive file exhausts resources or poisons output.

Controls: object quarantine, size/row/field limits, streaming parser, schema version, formula neutralization, timeouts.

### TM-09 Secret leakage

Secrets enter logs, traces, exceptions, source control, support exports, or audit payloads.

Controls: secret references, redaction allowlist, telemetry tests, scanning, rotation runbook.

### TM-10 Privileged resolution abuse

Operator marks discrepancy resolved without evidence or hides impact.

Controls: least privilege, reason/evidence requirement, maker-checker for high-risk actions later, immutable resolution history.

### TM-11 Race between capture, cancel, and refund

Concurrent commands violate financial limits or produce contradictory provider operations.

Controls: aggregate versioning, operation leases/status, state guards, provider operation keys, reconciliation.

### TM-12 Event poisoning and unsafe replay

Malformed or incompatible event repeatedly crashes consumers or replay duplicates effects.

Controls: schema validation, bounded retries, dead letter, payload hash, inbox deduplication, controlled replay authorization.

## 8. Security verification obligations

- tenant isolation integration suite;
- mass-assignment negative tests;
- idempotency conflict and concurrency tests;
- callback signature/replay tests;
- SSRF policy tests before webhook implementation;
- telemetry secret-redaction tests;
- database-role tests for ledger immutability;
- import limits and CSV injection tests;
- dependency, secret, and container scans;
- restore and replay exercises.

## 9. Assumptions

- Provider simulator is untrusted from the platform’s perspective even though both are developed in one repository.
- Development secrets are never promoted as production secrets.
- PostgreSQL durability depends on configured backup and managed infrastructure; objectives are not guarantees until tested.
- The first version uses JWTs with local development identities; production identity integration is deferred.

## v0.2 risk scoring and ownership

Likelihood and impact use Low/Medium/High/Critical. Cross-tenant access, forged callbacks, duplicate external effects, ledger mutation/imbalance, secret compromise, and unsafe restore are Critical impact and require preventive plus detective controls. Every high/critical risk has a domain/security owner, mapped tests, metrics/alerts where observable, and a runbook.

## Additional trust boundaries

- Identity provider/JWT verification boundary.
- CI/CD and artifact-signing boundary.
- Secret-manager boundary.
- Observability exporter/backend boundary.
- Backup/restore storage and restore-operator boundary.
- Later statement object-storage boundary.

## Security assumptions

JWT issuer/audience/signature and clock-skew are validated; operator sessions use stronger authentication in production design; runtime database roles are not migration roles; telemetry backends are not trusted with secrets/raw evidence; backups are encrypted and restore access is audited. These assumptions receive implementation tests before production claims.

