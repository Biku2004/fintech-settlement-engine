# Failed Deployment Rollback

**Owner:** Release/Service Owner  
**Severity:** SEV-2; SEV-1 for correctness/security regression

## Trigger
Health/SLO regression, migration incompatibility, invariant alarm, security failure, or worker duplication after deployment.

## Customer/business impact
API outage, incompatible schema, duplicate processing, or incorrect financial state.

## Immediate containment
Stop rollout; pause affected workers; preserve old/new versions and migration state; block further migrations.

## Diagnosis
Compare application version, Flyway history, feature flags, schema compatibility, error/trace changes, and invariant/consumer effects.

## Recovery
Prefer roll-forward for irreversible migration. Roll back application only if old version is schema-compatible. Restore database only under DR runbook—not as casual rollback. Resume workers one class at a time.

## Verification
Health/SLOs stable, migrations consistent, invariants pass, no duplicate events/postings, old/new compatibility confirmed.

## Escalation and communication
Release owner; database owner for migration; ledger/security owners for correctness/security. Status updates include impact, safe workarounds, unknown counts, and next checkpoint without exposing internal evidence.

## Post-incident actions
Record decision timeline, compatibility gap, rollback/roll-forward steps, and missing deployment test. Assign owners and due dates; add or strengthen an automated test/alert where possible.
