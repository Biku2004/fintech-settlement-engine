package com.bikash.fintechsettlement.ledger.domain.error;

public final class DuplicateLedgerAccountException extends LedgerDomainException {
    public DuplicateLedgerAccountException(String detail) {
        super(LedgerErrorCode.DUPLICATE_ACCOUNT, "Ledger account already exists: " + detail);
    }
}
