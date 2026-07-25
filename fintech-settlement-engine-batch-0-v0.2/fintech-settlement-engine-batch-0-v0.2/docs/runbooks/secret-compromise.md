# Secret Compromise

**Owner:** Security Incident Commander  
**Severity:** SEV-1

## Trigger
Confirmed/suspected exposure, anomalous use, secret in telemetry/artifact, or provider notification.

## Customer/business impact
Forged callbacks, unauthorized provider/API access, data exposure, or fraudulent operations.

## Immediate containment
Disable/revoke affected secret, restrict ingress/egress, preserve evidence, rotate related credentials, and block suspect callbacks/operations.

## Diagnosis
Identify secret type, scope, exposure window, logs/artifacts/access, calls signed/authorized with it, tenant/provider impact, and secondary credentials.

## Recovery
Issue new secret through manager, deploy dual-key verification only for controlled rotation window, update clients/providers, invalidate old key, reconcile suspicious operations.

## Verification
Old secret rejected; new secret works; telemetry scan clean; suspicious operations classified; audit complete.

## Escalation and communication
Security commander immediately; provider/merchant/legal/compliance notification according to impact. Status updates include impact, safe workarounds, unknown counts, and next checkpoint without exposing internal evidence.

## Post-incident actions
Mandatory postmortem, secret inventory update, redaction/scan test, access reduction, and notification evidence. Assign owners and due dates; add or strengthen an automated test/alert where possible.
