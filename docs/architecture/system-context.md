# System Context

```mermaid
flowchart LR
    Merchant[Merchant Application] -->|Authenticated payment API| Platform[Fintech Settlement Engine]
    Finance[Merchant Finance User] -->|Merchant-scoped read access| Platform
    Ops[Platform Operations Analyst] -->|Exception and recovery workflows| Platform
    Admin[Platform Administrator] -->|Access and policy management| Platform
    Platform -->|Idempotent provider operations| Simulator[Provider Simulator]
    Simulator -->|Signed callbacks and statements| Platform
    Platform -->|Future merchant events| MerchantWebhook[Merchant Webhook Endpoint]
    Platform -->|Operational telemetry| Telemetry[Logs, Metrics, Traces, Alerts]
```

## System responsibilities

The platform owns:

- payment lifecycle;
- provider attempt history;
- immutable ledger;
- settlement calculation and payout state;
- reconciliation evidence and resolutions;
- idempotency and event publication;
- merchant isolation and audit.

The provider simulator owns:

- simulated external operation result;
- provider operation reference;
- provider event IDs;
- configurable delay, loss, duplication, and ordering scenarios;
- simulated provider statements.

The platform never delegates authorization, tenant isolation, ledger correctness, or operator permissions to the provider simulator.
