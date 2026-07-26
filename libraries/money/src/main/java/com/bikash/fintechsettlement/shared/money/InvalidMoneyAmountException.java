package com.bikash.fintechsettlement.shared.money;

public final class InvalidMoneyAmountException extends IllegalArgumentException {
    public InvalidMoneyAmountException(String message) {
        super(message);
    }

    public InvalidMoneyAmountException(String message, Throwable cause) {
        super(message, cause);
    }
}
