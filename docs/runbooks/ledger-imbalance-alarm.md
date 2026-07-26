# Ledger Imbalance Alarm

**Owner:** Ledger Domain Owner  
**Severity:** SEV-1

## Trigger
Any `ledger_imbalance_rejection_total` increment; confirmed unbalanced committed transaction is a critical zero-budget breach.

## Customer/business impact
Rejected attempts may indicate bug/abuse. A committed imbalance invalidates financial correctness and blocks releases/settlement.

## Immediate containment
Stop affected posting policy/consumer; preserve inputs sanitized; do not edit ledger; pause settlement using affected accounts/source types.

## Diagnosis
Identify policy version, source type/ID, arithmetic/rounding, entry directions, concurrency, migration, and whether rejection happened before commit. Run whole-ledger balance verification for suspected scope.

## Recovery
Fix policy/code; deploy safely; replay original source with same identity only after idempotency review. For committed defect, append approved reversal/adjustment—never mutation.

## Verification
Affected and global transactions balance; source uniqueness intact; reversal approval/audit present; alerts clear.

## Escalation and communication
Incident commander, finance owner, security if malicious input suspected, and executive/compliance path for committed imbalance. Status updates include impact, safe workarounds, unknown counts, and next checkpoint without exposing internal evidence.

## Post-incident actions
Mandatory root-cause review, invariant test, policy version change, impact calculation, and evidence retention. Assign owners and due dates; add or strengthen an automated test/alert where possible.
