package com.bikash.fintechsettlement.ledger.domain.error;

public final class LedgerAccountClosedException extends LedgerDomainException {
    public LedgerAccountClosedException(String detail) {
        super(LedgerErrorCode.ACCOUNT_CLOSED, "Ledger account is closed: " + detail);
    }
}
