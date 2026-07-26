package com.bikash.fintechsettlement.ledger.domain.error;

public final class InvalidPostingPolicyException extends LedgerDomainException {
    public InvalidPostingPolicyException(String detail) {
        super(LedgerErrorCode.INVALID_POSTING_POLICY, "Posting violates its policy: " + detail);
    }
}
