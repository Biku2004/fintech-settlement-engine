# Reconciliation Backlog

**Owner:** Reconciliation Domain Owner  
**Severity:** SEV-3 warning; SEV-2 >1 day; SEV-1 >3 days or financial reporting deadline risk

## Trigger
Unmatched/open exception age or volume breaches threshold; importer/matcher checkpoint stalls.

## Customer/business impact
Unknown operations and external/internal differences remain unresolved; settlement/reporting confidence falls.

## Immediate containment
Prioritize unknown financial effects and high-value differences; pause low-priority imports if capacity constrained; preserve deterministic order/rules version.

## Diagnosis
Segment by classification/provider/currency/age, inspect import/match throughput, failed checkpoints, duplicate sources, rules changes, and analyst capacity.

## Recovery
Resume from checkpoints, scale bounded workers, correct schema/rules via new version, assign manual cases with evidence and SLA. Never auto-waive differences.

## Verification
Backlog/age trends down, no duplicate matches/resolutions, samples reproduce under recorded rule version, unknown-payment count decreases.

## Escalation and communication
Reconciliation and finance owners; incident commander at deadline/systemic mismatch. Status updates include impact, safe workarounds, unknown counts, and next checkpoint without exposing internal evidence.

## Post-incident actions
Document volume/cause, rule or capacity changes, analyst impact, and prevention. Assign owners and due dates; add or strengthen an automated test/alert where possible.
