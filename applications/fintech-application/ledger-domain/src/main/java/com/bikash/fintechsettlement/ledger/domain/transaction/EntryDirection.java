package com.bikash.fintechsettlement.ledger.domain.transaction;

public enum EntryDirection {
    DEBIT,
    CREDIT;

    public EntryDirection opposite() {
        return this == DEBIT ? CREDIT : DEBIT;
    }
}
