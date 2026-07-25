# Provider Outage or Degradation

**Owner:** Payment Service Owner  
**Severity:** SEV-2; SEV-1 if duplicate/lost financial effect is suspected

## Trigger
Provider success <98% or p95 >3 s for 10 minutes; critical <90% or p95 >8 s; unknown operation age breach.

## Customer/business impact
Authorize/capture/cancel/refund commands may reject, time out, or become unknown. Merchants may be unable to complete operations; unsafe retries can duplicate effects.

## Immediate containment
Open circuit/load-shed by provider operation; preserve operation keys; disable independent retries for unknowns; keep reads available; assign owner to aged unknowns.

## Diagnosis
Check provider spans, result classification, DNS/TLS/connect/read timeout split, bulkhead use, circuit state, recent deployments, simulator fault mode, and query endpoint. Sample operation IDs without exposing payloads.

## Recovery
If definitely pre-execution, allow same-key retry. If ambiguous, query original operation and process signed evidence. Gradually close circuit after healthy probes. Never generate a new business key to bypass unknown state.

## Verification
Success/latency stable for 30 minutes; unknown backlog decreasing; no duplicate source events/postings; merchant APIs and timeline evidence correct.

## Escalation and communication
Payment owner at warning; incident commander and finance/ledger owner at critical or any suspected duplicate/loss. Security if callback/signature anomaly. Status updates include impact, safe workarounds, unknown counts, and next checkpoint without exposing internal evidence.

## Post-incident actions
Document provider timeline, classifications, unknown-resolution time, false retries prevented, SLO budget, and adapter/runbook changes. Assign owners and due dates; add or strengthen an automated test/alert where possible.
