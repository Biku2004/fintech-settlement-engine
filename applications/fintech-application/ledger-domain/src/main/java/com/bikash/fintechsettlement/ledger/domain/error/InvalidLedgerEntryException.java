package com.bikash.fintechsettlement.ledger.domain.error;

public final class InvalidLedgerEntryException extends LedgerDomainException {
    public InvalidLedgerEntryException(String detail) {
        super(LedgerErrorCode.INVALID_ENTRY, "Invalid ledger entry: " + detail);
    }
}
