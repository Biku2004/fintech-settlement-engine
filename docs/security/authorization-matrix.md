# Authorization and Service-Identity Matrix

| Action | Merchant principal | Ops analyst | Reconciliation analyst | Security operator | Service identity |
|---|---:|---:|---:|---:|---:|
| Create/authorize/capture/cancel owned payment | Yes | No | No | No | API service executes after principal authorization |
| Read owned payment/timeline | Yes | Entitled support view | Read for investigation | Read security view | Query service |
| Query provider operation | No | Yes | Yes for reconciliation case | No | Payment worker |
| Manual unknown resolution | No | Propose | Approve/execute with evidence | Audit review | Reconciliation service only |
| Reverse ledger posting | No | No | Propose | No | Ledger service after approved command |
| Replay dead-letter event | No | No | Limited | Security approval for sensitive events | Replay tool identity |
| Rotate secret | No | No | No | Yes | Secret-rotation identity |
| Restore database | No | No | Verify finance | Approve secret/audit controls | Dedicated restore identity |

Rules:

- authorization uses action plus resource scope; roles alone are insufficient;
- merchant IDs in request bodies never grant access;
- service identities have no interactive login and narrow audience/scope;
- policy lookup failure is deny-by-default;
- privileged financial resolution requires reason/evidence and production two-person approval target.
