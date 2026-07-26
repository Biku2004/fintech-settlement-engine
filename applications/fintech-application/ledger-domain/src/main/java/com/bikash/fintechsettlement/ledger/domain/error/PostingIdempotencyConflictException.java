package com.bikash.fintechsettlement.ledger.domain.error;

public final class PostingIdempotencyConflictException extends LedgerDomainException {
    public PostingIdempotencyConflictException(String detail) {
        super(LedgerErrorCode.POSTING_IDEMPOTENCY_CONFLICT, "Posting idempotency key conflicts with the original posting: " + detail);
    }
}
