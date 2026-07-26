package com.bikash.fintechsettlement.ledger.domain.posting;

import com.bikash.fintechsettlement.ledger.domain.identity.CorrelationId;
import com.bikash.fintechsettlement.ledger.domain.identity.MerchantId;
import com.bikash.fintechsettlement.ledger.domain.transaction.LedgerTransactionId;

import java.time.Instant;
import java.util.Objects;

public record PostingContext(
        LedgerTransactionId transactionId,
        PostingIdempotencyKey idempotencyKey,
        SourceReference source,
        CorrelationId correlationId,
        MerchantId merchantId,
        Instant occurredAt,
        Instant recordedAt,
        String reason) {

    public PostingContext {
        transactionId = Objects.requireNonNull(transactionId, "transactionId");
        idempotencyKey = Objects.requireNonNull(idempotencyKey, "idempotencyKey");
        source = Objects.requireNonNull(source, "source");
        correlationId = Objects.requireNonNull(correlationId, "correlationId");
        occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
        recordedAt = Objects.requireNonNull(recordedAt, "recordedAt");
        reason = Objects.requireNonNull(reason, "reason").trim();
        if (reason.isEmpty() || reason.length() > 256) {
            throw new IllegalArgumentException("posting reason must be 1-256 characters");
        }
        if (recordedAt.isBefore(occurredAt)) {
            throw new IllegalArgumentException("recordedAt cannot precede occurredAt");
        }
    }

    public MerchantId requireMerchant() {
        if (merchantId == null) {
            throw new IllegalArgumentException("merchantId is required for this posting policy");
        }
        return merchantId;
    }
}
