package com.bikash.fintechsettlement.shared.money;

import java.util.Currency;

public final class UnsupportedCurrencyException extends IllegalArgumentException {
    public UnsupportedCurrencyException(Currency currency) {
        super("Currency has no supported ISO minor-unit definition: " + currency.getCurrencyCode());
    }
}
