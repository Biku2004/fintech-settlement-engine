# Database Connection Exhaustion

**Owner:** Platform/Database Owner  
**Severity:** SEV-2; SEV-1 if writes cannot commit or correctness evidence is uncertain

## Trigger
Pool pending >20% for 5 minutes, acquire timeout >1/min; critical pending >50% or timeout >10/min.

## Customer/business impact
API latency/errors, stalled outbox, and worker lag. External provider calls must not continue if local idempotency/state cannot be persisted safely.

## Immediate containment
Load-shed new state-changing work, pause noncritical workers/reports, keep pool reserved for correctness/recovery, and avoid indiscriminate pool-size increase.

## Diagnosis
Inspect active/idle/pending pool, long transactions, locks, slow queries, connection leaks, database max connections, recent traffic/deployments, and provider calls accidentally held inside transactions.

## Recovery
Cancel only verified safe runaway queries; rollback deployment/query regression; release leaked resources; resume workers gradually. Increase pool only with database capacity evidence.

## Verification
Pending and acquire timeouts return below warning; no long transaction holds external calls; outbox lag drains; invariant checks pass.

## Escalation and communication
Database owner immediately at critical; payment/ledger owners if commit outcomes are uncertain. Status updates include impact, safe workarounds, unknown counts, and next checkpoint without exposing internal evidence.

## Post-incident actions
Capture query plans, pool timeline, transaction traces, capacity headroom, and preventive test/action. Assign owners and due dates; add or strengthen an automated test/alert where possible.
