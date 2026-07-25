# Audit Event Specification

Audit events are append-only security/business evidence, separate from debug logs.

Required fields:

```text
auditEventId, eventType, schemaVersion, occurredAt, recordedAt,
actorType, actorId, actorRoles, serviceIdentity,
action, targetType, targetId, merchantId,
reason, evidenceReferences, correlationId, sourceIpClass, outcome
```

Privileged events include manual unknown resolution, reconciliation resolution, reversal, settlement cancellation, dead-letter replay, secret rotation, role changes, and backup restore.

Rules:

- reason is mandatory where human judgment changes financial classification;
- raw secrets and imported evidence payloads are referenced, not embedded;
- application runtime can append and read authorized audit views but cannot update/delete;
- audit retention target is seven years for financial/privileged evidence in the production design, subject to final legal review;
- access to audit search is itself audited.
