# Observability Specification

## Structured logs

Required fields:

```text
timestamp, level, service, module, environment,
traceId, spanId, correlationId, eventName, outcome, errorCode
```

Contextual identifiers may include `merchantId`, `paymentId`, `operationId`, `attemptId`, `eventId`, and `ledgerTransactionId`. High-cardinality IDs are logs/traces only, never unbounded metric labels.

Forbidden fields include secrets, authorization headers, raw callback bodies, raw statement rows, account credentials, full personal data, stack traces in public responses, and canonical request bodies containing sensitive metadata.

## Trace spans

```text
http.request
payment.create
payment.authorize
provider.authorize
payment.capture
provider.capture
payment.cancel
provider.cancel
provider.query
ledger.post
outbox.append
outbox.publish
reconciliation.import
reconciliation.match
webhook.deliver
```

Provider spans record provider name, operation type, sanitized result class, timeout class, and retry count—not payloads or credentials.

## Metrics

RED:

- `http_server_requests_total`
- `http_server_errors_total`
- `http_server_duration_seconds`

Dependency/saturation:

- `db_pool_active`, `db_pool_pending`, `db_pool_timeout_total`
- `provider_requests_inflight`, `provider_latency_seconds`, `provider_timeout_total`
- `provider_circuit_state`, `provider_bulkhead_rejection_total`
- `outbox_oldest_unpublished_age_seconds`, `outbox_publish_failure_total`
- `consumer_lag`, `dead_letter_total`, `retry_total`
- `transaction_retry_total`, `deadlock_total`, `optimistic_lock_conflict_total`

Domain:

- `payment_state_transition_total{from,to,operation}`
- `payment_unknown_total{operation}`
- `ledger_posting_total{policy,outcome}`
- `ledger_imbalance_rejection_total`
- `settlement_exception_total{classification}`
- `reconciliation_difference_amount_minor{currency,classification}`

Metric labels are bounded catalogs. Never label by merchant, payment, provider reference, or free-text error.

## Correlation

The edge accepts or creates a safe correlation ID. Internal events preserve `correlationId` and `causationId`. Trace propagation never replaces stable business operation IDs.

## Telemetry tests

- canary secrets and card-like strings do not appear in logs/traces;
- all state-changing requests produce correlation IDs;
- all provider attempts create spans and outcome metrics;
- imbalance rejection increments the alarm metric without entry payloads;
- unknown operations generate a metric and searchable sanitized log.
