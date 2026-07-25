# Stuck Settlement Batch

**Owner:** Settlement Domain Owner  
**Severity:** SEV-2; SEV-1 near payout deadline or duplicate-risk

## Trigger
Batch age >2x expected or no checkpoint progress; critical >4x or `PAYOUT_UNKNOWN` past threshold.

## Customer/business impact
Merchant payout delay, growing payable, possible duplicate payout if state is bypassed.

## Immediate containment
Acquire/confirm single batch lease; block second payout; isolate merchant batch so unrelated merchants continue.

## Diagnosis
Inspect state, lease owner/expiry, checkpoint, item count/totals, payout key, provider evidence, configuration version, and latest audit action.

## Recovery
Resume calculation from checkpoint, release expired lease safely, query unknown payout using original key, or move to `REQUIRES_REVIEW`. Never rebuild a submitted batch with a new payout key.

## Verification
Batch reaches valid next state; totals repeat from inputs; one payout instruction; merchant partitions progress.

## Escalation and communication
Settlement owner; finance owner for payout unknown; incident commander at deadline/duplicate risk. Status updates include impact, safe workarounds, unknown counts, and next checkpoint without exposing internal evidence.

## Post-incident actions
Record merchant impact, lease/worker cause, payout evidence, and capacity/retry improvements. Assign owners and due dates; add or strengthen an automated test/alert where possible.
