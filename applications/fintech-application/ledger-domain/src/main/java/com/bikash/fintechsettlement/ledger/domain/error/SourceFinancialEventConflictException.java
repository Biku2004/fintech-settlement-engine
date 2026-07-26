package com.bikash.fintechsettlement.ledger.domain.error;

public final class SourceFinancialEventConflictException extends LedgerDomainException {
    public SourceFinancialEventConflictException(String detail) {
        super(LedgerErrorCode.SOURCE_FINANCIAL_EVENT_CONFLICT, "Source financial event conflicts with the original posting: " + detail);
    }
}
