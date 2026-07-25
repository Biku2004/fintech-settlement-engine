# Security Invariants

| ID | Invariant |
|---|---|
| S-001 | No state-changing operation executes without an authenticated principal or service identity. |
| S-002 | Merchant authorization is derived from the authenticated principal, never trusted from request body ownership fields. |
| S-003 | A merchant can access only its own payments, settlements, webhook configuration, and permitted ledger views. |
| S-004 | Provider callbacks are processed only after signature, timestamp, and replay-window validation. |
| S-005 | Raw card numbers, CVV, bank credentials, and real payment credentials are not accepted or stored. |
| S-006 | Provider and signing secrets never appear in logs, traces, exceptions, API responses, or audit payloads. |
| S-007 | User-controlled internal state, provider choice, fee, ledger account, and settlement fields are rejected or ignored. |
| S-008 | Every privileged action records actor, role, action, target, reason where required, correlation ID, and timestamp. |
| S-009 | Audit records are append-only through the application. |
| S-010 | Idempotency cannot be bypassed through alternate routes, aliases, or API versions for the same operation. |
| S-011 | Request bodies, headers, files, list filters, and pagination have explicit size and rate limits. |
| S-012 | Errors reveal stable public codes but not stack traces, SQL, secrets, internal topology, or cross-tenant existence. |
| S-013 | Outbound merchant webhook URLs are validated against SSRF policy before activation and before delivery. |
| S-014 | Dependency and container vulnerabilities are scanned in CI with defined severity gates. |
| S-015 | Database roles enforce least privilege by module and deny mutation of immutable ledger history. |
| S-016 | Service-to-service identities are distinct from user identities and have narrow permissions. |
| S-017 | Replay, reconciliation, reversal, and secret-rotation operations require elevated permissions. |
| S-018 | Authentication or authorization failure is fail-closed; unavailable policy storage never grants access. |
| S-019 | Sensitive fields have explicit telemetry redaction tests. |
| S-020 | Security controls have integration tests, not only configuration review. |
