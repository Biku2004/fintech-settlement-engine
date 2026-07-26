package com.bikash.fintechsettlement.ledger.domain.account;

import com.bikash.fintechsettlement.ledger.domain.transaction.EntryDirection;

public enum LedgerAccountType {
    ASSET(EntryDirection.DEBIT),
    LIABILITY(EntryDirection.CREDIT),
    REVENUE(EntryDirection.CREDIT),
    EXPENSE(EntryDirection.DEBIT),
    EQUITY(EntryDirection.CREDIT);

    private final EntryDirection normalBalance;

    LedgerAccountType(EntryDirection normalBalance) {
        this.normalBalance = normalBalance;
    }

    public EntryDirection normalBalance() {
        return normalBalance;
    }
}
