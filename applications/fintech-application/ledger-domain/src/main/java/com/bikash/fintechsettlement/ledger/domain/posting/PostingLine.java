package com.bikash.fintechsettlement.ledger.domain.posting;

import com.bikash.fintechsettlement.ledger.domain.account.LedgerAccount;
import com.bikash.fintechsettlement.ledger.domain.error.InvalidLedgerEntryException;
import com.bikash.fintechsettlement.ledger.domain.transaction.EntryDirection;
import com.bikash.fintechsettlement.shared.money.CurrencyMismatchException;
import com.bikash.fintechsettlement.shared.money.Money;

import java.util.Objects;

public record PostingLine(LedgerAccount account, EntryDirection direction, Money amount) {
    public PostingLine {
        account = Objects.requireNonNull(account, "account");
        direction = Objects.requireNonNull(direction, "direction");
        amount = Objects.requireNonNull(amount, "amount");
        if (!amount.isPositive()) {
            throw new InvalidLedgerEntryException("entry amount must be strictly positive");
        }
        if (!account.currency().equals(amount.currency())) {
            throw new CurrencyMismatchException(account.currency(), amount.currency());
        }
    }
}
