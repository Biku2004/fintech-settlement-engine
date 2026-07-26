package com.bikash.fintechsettlement.ledger.domain.error;

public final class LedgerArithmeticException extends LedgerDomainException {
    public LedgerArithmeticException(String detail, ArithmeticException cause) {
        super(LedgerErrorCode.ARITHMETIC_OVERFLOW, "Ledger arithmetic overflow: " + detail, cause);
    }
}
