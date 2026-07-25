# Dead-Letter Replay

**Owner:** Owning Consumer Domain Owner  
**Severity:** Change-controlled operation; SEV-2 when backlog affects business deadline

## Trigger
Validated dead-letter records after root cause fixed; automatic replay is prohibited.

## Customer/business impact
Replay can duplicate external or financial effects if identity/idempotency is not intact.

## Immediate containment
Freeze selected records; verify no active consumer is independently processing them; preserve original payload/schema/event ID.

## Diagnosis
Classify transient versus poison/schema/security issue; inspect consumer inbox/effect records, original correlation/causation, and target permissions.

## Recovery
Approve exact record set; dry-run deserialization/authorization; replay with original IDs in small batch; stop on unexpected result.

## Verification
Each event is consumed once effectively, inbox/effect state correct, DLQ decreases, no new unknown/duplicate side effect.

## Escalation and communication
Domain owner and platform owner; security approval for sensitive/forged events; finance approval for financial effects. Status updates include impact, safe workarounds, unknown counts, and next checkpoint without exposing internal evidence.

## Post-incident actions
Record approvers, query/filter, IDs, before/after state, metrics, and missing compatibility tests. Assign owners and due dates; add or strengthen an automated test/alert where possible.
