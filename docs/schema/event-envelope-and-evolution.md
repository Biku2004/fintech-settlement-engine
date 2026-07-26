# Event Envelope, Partitioning, and Evolution

Every event contains:

```text
eventId, eventType, schemaVersion, occurredAt, recordedAt,
producer, aggregateType, aggregateId, partitionKey,
correlationId, causationId, tenantId when applicable, payload
```

## Rules

- `eventId` and original `occurredAt` remain unchanged during replay.
- Consumers ignore unknown optional fields.
- Breaking semantic changes use a new major schema/event type.
- Schema files live under future `contracts/event-schemas/`; examples and compatibility tests are required before publication.
- Payloads are classified as Public, Internal, Confidential, or Restricted. Restricted data is prohibited from ordinary events.
- Event payloads contain references rather than secrets/raw provider or statement evidence.

## Partition keys

- Payment lifecycle: `paymentId`.
- Ledger posting: stable `ledgerAccountGroupId` or source aggregate according to consumer ordering need.
- Settlement: `merchantId`.
- Webhook delivery: `endpointId`.

Global ordering is never assumed.

## Retention and deprecation

- Stage 1 outbox rows retain payload at least 30 days after successful publication; immutable business records remain in owned tables.
- Later Kafka baseline: 7-day operational retention; replayable financial events target 30 days unless object/archive policy is used.
- Consumers support the previous major version through a documented migration window.
- Deprecated fields remain readable until all registered consumers prove migration.
