package com.bikash.fintechsettlement.ledger.domain.transaction;

import com.bikash.fintechsettlement.ledger.domain.account.AccountScope;
import com.bikash.fintechsettlement.ledger.domain.account.LedgerAccountId;
import com.bikash.fintechsettlement.ledger.domain.account.LedgerAccountRole;
import com.bikash.fintechsettlement.ledger.domain.error.InvalidLedgerEntryException;
import com.bikash.fintechsettlement.shared.money.Money;

import java.util.Objects;

public record LedgerEntry(
        int sequence,
        LedgerAccountId accountId,
        LedgerAccountRole accountRole,
        AccountScope accountScope,
        EntryDirection direction,
        Money amount) {

    public LedgerEntry {
        if (sequence <= 0) {
            throw new InvalidLedgerEntryException("entry sequence must be positive");
        }
        accountId = Objects.requireNonNull(accountId, "accountId");
        accountRole = Objects.requireNonNull(accountRole, "accountRole");
        accountScope = Objects.requireNonNull(accountScope, "accountScope");
        direction = Objects.requireNonNull(direction, "direction");
        amount = Objects.requireNonNull(amount, "amount");
        if (!amount.isPositive()) {
            throw new InvalidLedgerEntryException("entry amount must be strictly positive");
        }
    }

    public LedgerEntry inverted(int newSequence) {
        return new LedgerEntry(
                newSequence, accountId, accountRole, accountScope, direction.opposite(), amount);
    }
}
