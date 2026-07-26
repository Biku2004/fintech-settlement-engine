package com.bikash.fintechsettlement.ledger.domain.error;

import java.util.Objects;

public class LedgerDomainException extends RuntimeException {
    private final LedgerErrorCode code;

    public LedgerDomainException(LedgerErrorCode code, String message) {
        super(message);
        this.code = Objects.requireNonNull(code, "code");
    }

    public LedgerDomainException(LedgerErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = Objects.requireNonNull(code, "code");
    }

    public LedgerErrorCode code() {
        return code;
    }
}
