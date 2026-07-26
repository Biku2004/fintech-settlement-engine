package com.bikash.fintechsettlement.ledger.domain.error;

public final class UnbalancedTransactionException extends LedgerDomainException {
    public UnbalancedTransactionException(String detail) {
        super(LedgerErrorCode.UNBALANCED_TRANSACTION, "Ledger transaction is unbalanced: " + detail);
    }
}
