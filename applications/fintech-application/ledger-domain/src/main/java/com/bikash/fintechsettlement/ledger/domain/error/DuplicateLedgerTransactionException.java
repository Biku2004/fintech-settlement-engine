package com.bikash.fintechsettlement.ledger.domain.error;

public final class DuplicateLedgerTransactionException extends LedgerDomainException {
    public DuplicateLedgerTransactionException(String detail) {
        super(LedgerErrorCode.DUPLICATE_TRANSACTION, "Ledger transaction already exists: " + detail);
    }
}
