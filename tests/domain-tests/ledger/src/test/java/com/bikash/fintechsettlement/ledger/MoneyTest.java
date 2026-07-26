package com.bikash.fintechsettlement.ledger;

import com.bikash.fintechsettlement.shared.money.CurrencyMismatchException;
import com.bikash.fintechsettlement.shared.money.InvalidMoneyAmountException;
import com.bikash.fintechsettlement.shared.money.Money;
import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {
    private static final Currency USD = Currency.getInstance("USD");

    @Test
    void exactMinorArithmeticAndCurrencySafety() {
        Money ten = new Money(1_000, USD);
        Money three = new Money(300, USD);
        assertEquals(1_300, ten.plus(three).amountMinor());
        assertEquals(700, ten.minus(three).amountMinor());
        assertEquals(3_000, ten.multiply(3).amountMinor());
        assertThrows(CurrencyMismatchException.class,
                () -> ten.plus(new Money(100, Currency.getInstance("EUR"))));
    }

    @Test
    void parsesCurrencyFractionDigitsExactly() {
        assertEquals(1234, Money.fromMajor("12.34", USD).amountMinor());
        assertEquals(12, Money.fromMajor("12", Currency.getInstance("JPY")).amountMinor());
        assertEquals(12_345, Money.fromMajor("12.345", Currency.getInstance("KWD")).amountMinor());
        assertThrows(InvalidMoneyAmountException.class, () -> Money.fromMajor("12.345", USD));
    }

    @Test
    void checkedArithmeticDetectsOverflow() {
        assertThrows(ArithmeticException.class,
                () -> new Money(Long.MAX_VALUE, USD).plus(new Money(1, USD)));
    }
}
