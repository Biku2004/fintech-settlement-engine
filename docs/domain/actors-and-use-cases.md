# Actors, Permissions, and Use Cases

## Merchant API client

### May

- create a payment intent for its authenticated merchant;
- authorize, capture, cancel, and later refund its own payment when state permits;
- retrieve its own payment and timeline;
- register merchant webhook endpoints in a later batch.

### May not

- choose arbitrary internal ledger accounts;
- set payment state, provider result, fee result, or settlement status;
- supply another merchant ID as authority;
- read another merchant’s resources;
- bypass idempotency.

## Merchant finance user

### May

- read payments, ledger views scoped to its merchant, settlements, and reconciliation summaries;
- export merchant-owned reports.

### May not

- execute provider operations;
- resolve platform reconciliation exceptions;
- view provider secrets or platform-wide accounts.

## Platform operations analyst

### May

- inspect provider attempts and unknown states;
- initiate approved provider status queries;
- start reconciliation runs;
- perform permitted resolution actions with a reason;
- request controlled event replay.

### May not

- edit or delete ledger history;
- mark an unbalanced posting valid;
- bypass maker-checker controls for high-risk operations when introduced;
- silently suppress discrepancies.

## Platform administrator

### May

- manage merchant and operator access;
- configure provider simulator scenarios;
- rotate secrets;
- manage operational policy under audit.

### May not

- extract secret material through ordinary APIs;
- alter historical audit records.

## Provider simulator

Produces deterministic and configurable scenarios:

- success;
- business rejection;
- failure before execution;
- success with lost response;
- delayed response;
- duplicate callback;
- out-of-order callback;
- unknown event type.

## Automated workers

Workers act under service identities and least-privilege permissions. Each worker must have a narrow purpose, such as outbox publication, reconciliation, settlement calculation, or webhook delivery.
