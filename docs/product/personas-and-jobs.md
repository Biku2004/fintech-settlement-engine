# Personas, Jobs, and Permissions

| Persona | Primary job | Allowed decisions | Prohibited |
|---|---|---|---|
| Merchant Developer | Integrate commands, query status, validate callbacks | create/authorize/capture/cancel owned payments; configure owned endpoint later | choose provider/internal state/accounts; access other merchants |
| Merchant Finance User | Explain gross, fee, refund, payable, settlement | read owned financial timelines and exports | mutate ledger or resolve platform exceptions |
| Payment Operations Analyst | Investigate unknown/rejected attempts | query evidence, request reconciliation, propose resolution | retry unknown financial effect independently |
| Reconciliation Analyst | Match statements and resolve exceptions | classify and resolve with evidence/reason | delete original evidence or bypass ledger rules |
| Support Analyst | Explain public status safely | read sanitized owned views based on support entitlement | view secrets/raw payloads; perform financial correction |
| Security/Compliance Operator | Review privileged/security events | rotate secrets, investigate replay/access anomalies | alter financial history |
| Service Operator | Maintain availability/recovery | pause/resume workers, rollback, restore under runbook | manually mark payment/ledger success without evidence |

## Permission model

Permissions are action-based and resource-scoped, for example:

```text
payment:create, payment:authorize, payment:capture, payment:cancel,
payment:read, ledger:read-owned, reconciliation:read,
reconciliation:resolve, ledger:reverse, event:replay,
secret:rotate, restore:execute, audit:read
```

Merchant scope comes from authenticated claims and server-side membership. Elevated operations require role, reason, evidence, audit, and often two-person approval in the production design.
