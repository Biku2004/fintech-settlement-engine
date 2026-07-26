package com.bikash.fintechsettlement.shared.money;

import java.util.Currency;

public final class CurrencyMismatchException extends IllegalArgumentException {
    public CurrencyMismatchException(Currency expected, Currency actual) {
        super("Currency mismatch: expected " + expected.getCurrencyCode()
                + " but received " + actual.getCurrencyCode());
    }
}
