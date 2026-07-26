package com.bikash.fintechsettlement.ledger.domain.error;

public final class UnknownLedgerAccountException extends LedgerDomainException {
    public UnknownLedgerAccountException(String detail) {
        super(LedgerErrorCode.UNKNOWN_ACCOUNT, "Ledger account does not exist: " + detail);
    }
}
