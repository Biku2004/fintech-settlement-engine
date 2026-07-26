# Stage 1 Module Boundaries

## Dependency direction

```text
bootstrap -> payment application ports
bootstrap -> ledger application ports
payment application -> payment domain
payment application -> ledger posting port
payment infrastructure -> payment application/domain
ledger application -> ledger domain
ledger infrastructure -> ledger application/domain
outbox infrastructure -> module event ports
shared.money <- payment domain, ledger domain
```

Prohibited:

- ledger depending on payment;
- payment importing ledger persistence records;
- domain importing Spring, jOOQ, HTTP, or serialization classes;
- controllers calling repositories directly;
- one module reading another module's owned tables except through approved Stage 1 query views documented here.

## Public module interfaces

- `PaymentCommands`, `PaymentQueries`
- `LedgerPostingPort`, `LedgerQueries`
- `OutboxAppendPort`
- `AuditAppendPort`
- provider adapter ports for authorize/capture/cancel/query

Spring Modulith and ArchUnit tests enforce these rules in Batch 1 onward.
