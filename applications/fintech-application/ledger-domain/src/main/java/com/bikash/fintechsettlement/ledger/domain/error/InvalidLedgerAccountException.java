package com.bikash.fintechsettlement.ledger.domain.error;

public final class InvalidLedgerAccountException extends LedgerDomainException {
    public InvalidLedgerAccountException(String detail) {
        super(LedgerErrorCode.INVALID_ACCOUNT, "Invalid ledger account: " + detail);
    }
}
