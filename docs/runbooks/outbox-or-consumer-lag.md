# Outbox or Consumer Lag

**Owner:** Messaging/Platform Owner  
**Severity:** SEV-2; SEV-1 if retention/storage or financial downstream deadline at risk

## Trigger
Oldest outbox >60 s warning, >300 s critical; consumer lag exceeds documented processing window.

## Customer/business impact
Committed financial state remains safe but downstream settlement/webhook/read models become stale; storage may grow.

## Immediate containment
Do not republish with new event IDs; pause noncritical producers if storage headroom falls; scale/resume healthy consumers within limits.

## Diagnosis
Check claim leases, failed rows, publisher throughput, broker/consumer health later, poison event, DB locks, retention, and schema compatibility.

## Recovery
Release expired leases, fix dependency/schema issue, replay stable event IDs, isolate poison records to dead letter, resume bounded batches.

## Verification
Oldest age/lag drains, no duplicate effective consumer effect, storage headroom restored, inbox counts consistent.

## Escalation and communication
Platform owner; downstream domain owner when deadline or correctness visibility affected. Status updates include impact, safe workarounds, unknown counts, and next checkpoint without exposing internal evidence.

## Post-incident actions
Document event IDs, root cause, backlog curve, capacity model, and replay evidence. Assign owners and due dates; add or strengthen an automated test/alert where possible.
