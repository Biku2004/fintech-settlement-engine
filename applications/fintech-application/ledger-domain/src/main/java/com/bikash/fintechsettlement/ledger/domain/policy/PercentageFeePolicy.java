package com.bikash.fintechsettlement.ledger.domain.policy;

import com.bikash.fintechsettlement.ledger.domain.error.LedgerArithmeticException;
import com.bikash.fintechsettlement.shared.money.InvalidMoneyAmountException;
import com.bikash.fintechsettlement.shared.money.Money;

/** Integer basis-point fee plus fixed minor units, rounded HALF_UP. */
public record PercentageFeePolicy(int basisPoints, long fixedMinor) {
    private static final long BASIS_POINT_DIVISOR = 10_000L;

    public PercentageFeePolicy {
        if (basisPoints < 0 || basisPoints > 10_000) {
            throw new IllegalArgumentException("basisPoints must be between 0 and 10000");
        }
        if (fixedMinor < 0) {
            throw new IllegalArgumentException("fixedMinor must not be negative");
        }
    }

    public Money calculate(Money gross) {
        gross.requirePositive("gross");
        try {
            long scaled = Math.multiplyExact(gross.amountMinor(), basisPoints);
            long quotient = scaled / BASIS_POINT_DIVISOR;
            long remainder = scaled % BASIS_POINT_DIVISOR;
            if (remainder * 2L >= BASIS_POINT_DIVISOR) {
                quotient = Math.addExact(quotient, 1L);
            }
            long total = Math.addExact(quotient, fixedMinor);
            Money fee = new Money(total, gross.currency());
            if (fee.compareTo(gross) > 0) {
                throw new InvalidMoneyAmountException("calculated fee exceeds gross amount");
            }
            return fee;
        } catch (ArithmeticException exception) {
            throw new LedgerArithmeticException("calculating percentage fee", exception);
        }
    }
}
