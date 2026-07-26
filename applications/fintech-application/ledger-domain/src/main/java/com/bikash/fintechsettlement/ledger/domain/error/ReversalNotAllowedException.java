package com.bikash.fintechsettlement.ledger.domain.error;

public final class ReversalNotAllowedException extends LedgerDomainException {
    public ReversalNotAllowedException(String detail) {
        super(LedgerErrorCode.REVERSAL_NOT_ALLOWED, "Reversal is not allowed: " + detail);
    }
}
