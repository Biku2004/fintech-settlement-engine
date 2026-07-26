package com.bikash.fintechsettlement.shared.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Immutable money value represented as signed integer minor units.
 *
 * <p>Negative values are allowed because account balances can be negative. Ledger entry magnitudes
 * are validated separately and must be strictly positive.</p>
 */
public record Money(long amountMinor, Currency currency) implements Comparable<Money> {

    public Money {
        currency = Objects.requireNonNull(currency, "currency");
        requireSupportedScale(currency);
    }

    public static Money ofMinor(long amountMinor, String currencyCode) {
        Objects.requireNonNull(currencyCode, "currencyCode");
        return new Money(amountMinor, Currency.getInstance(currencyCode));
    }

    public static Money zero(Currency currency) {
        return new Money(0L, currency);
    }

    public static Money fromMajor(String majorAmount, Currency currency) {
        Objects.requireNonNull(majorAmount, "majorAmount");
        Objects.requireNonNull(currency, "currency");
        int scale = requireSupportedScale(currency);
        try {
            BigDecimal parsed = new BigDecimal(majorAmount).setScale(scale, RoundingMode.UNNECESSARY);
            return new Money(parsed.movePointRight(scale).longValueExact(), currency);
        } catch (NumberFormatException | ArithmeticException exception) {
            throw new InvalidMoneyAmountException(
                    "Amount '" + majorAmount + "' is not an exact " + currency.getCurrencyCode()
                            + " value with " + scale + " fractional digits",
                    exception);
        }
    }

    public BigDecimal toMajor() {
        return BigDecimal.valueOf(amountMinor, fractionDigits());
    }

    public Money plus(Money other) {
        requireSameCurrency(other);
        return new Money(Math.addExact(amountMinor, other.amountMinor), currency);
    }

    public Money minus(Money other) {
        requireSameCurrency(other);
        return new Money(Math.subtractExact(amountMinor, other.amountMinor), currency);
    }

    public Money multiply(long factor) {
        return new Money(Math.multiplyExact(amountMinor, factor), currency);
    }

    public Money negate() {
        return new Money(Math.negateExact(amountMinor), currency);
    }

    public Money abs() {
        return amountMinor >= 0 ? this : negate();
    }

    public Money requirePositive(String fieldName) {
        if (!isPositive()) {
            throw new InvalidMoneyAmountException(fieldName + " must be greater than zero");
        }
        return this;
    }

    public Money requireNonNegative(String fieldName) {
        if (isNegative()) {
            throw new InvalidMoneyAmountException(fieldName + " must not be negative");
        }
        return this;
    }

    public Money requireAtMost(Money upperBound, String fieldName) {
        requireSameCurrency(upperBound);
        if (compareTo(upperBound) > 0) {
            throw new InvalidMoneyAmountException(fieldName + " exceeds its allowed maximum");
        }
        return this;
    }

    public Money requireWithinAbsoluteLimit(long maximumAbsoluteMinor, String fieldName) {
        if (maximumAbsoluteMinor <= 0) {
            throw new IllegalArgumentException("maximumAbsoluteMinor must be positive");
        }
        if (amountMinor == Long.MIN_VALUE || Math.abs(amountMinor) > maximumAbsoluteMinor) {
            throw new InvalidMoneyAmountException(fieldName + " exceeds the configured business limit");
        }
        return this;
    }

    public boolean isZero() {
        return amountMinor == 0L;
    }

    public boolean isPositive() {
        return amountMinor > 0L;
    }

    public boolean isNegative() {
        return amountMinor < 0L;
    }

    public int fractionDigits() {
        return requireSupportedScale(currency);
    }

    public String currencyCode() {
        return currency.getCurrencyCode();
    }

    public String toPlainString() {
        return currencyCode() + " " + toMajor().toPlainString();
    }

    @Override
    public int compareTo(Money other) {
        requireSameCurrency(other);
        return Long.compare(amountMinor, other.amountMinor);
    }

    private void requireSameCurrency(Money other) {
        Objects.requireNonNull(other, "other");
        if (!currency.equals(other.currency)) {
            throw new CurrencyMismatchException(currency, other.currency);
        }
    }

    private static int requireSupportedScale(Currency currency) {
        int scale = currency.getDefaultFractionDigits();
        if (scale < 0 || scale > 3) {
            throw new UnsupportedCurrencyException(currency);
        }
        return scale;
    }
}
