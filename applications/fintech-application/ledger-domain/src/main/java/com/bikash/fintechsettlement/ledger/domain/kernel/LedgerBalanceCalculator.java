package com.bikash.fintechsettlement.ledger.domain.kernel;

import com.bikash.fintechsettlement.ledger.domain.account.LedgerAccount;
import com.bikash.fintechsettlement.ledger.domain.error.LedgerArithmeticException;
import com.bikash.fintechsettlement.ledger.domain.transaction.EntryDirection;
import com.bikash.fintechsettlement.ledger.domain.transaction.LedgerTransaction;
import com.bikash.fintechsettlement.shared.money.Money;

import java.util.Collection;

public final class LedgerBalanceCalculator {
    public Money calculate(LedgerAccount account, Collection<LedgerTransaction> transactions) {
        long debits = 0L;
        long credits = 0L;
        try {
            for (LedgerTransaction transaction : transactions) {
                for (var entry : transaction.entries()) {
                    if (!entry.accountId().equals(account.id())) {
                        continue;
                    }
                    if (entry.direction() == EntryDirection.DEBIT) {
                        debits = Math.addExact(debits, entry.amount().amountMinor());
                    } else {
                        credits = Math.addExact(credits, entry.amount().amountMinor());
                    }
                }
            }
            long normalBalance = account.normalBalance() == EntryDirection.DEBIT
                    ? Math.subtractExact(debits, credits)
                    : Math.subtractExact(credits, debits);
            return new Money(normalBalance, account.currency());
        } catch (ArithmeticException exception) {
            throw new LedgerArithmeticException("calculating authoritative balance", exception);
        }
    }
}
