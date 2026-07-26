package com.bikash.fintechsettlement.ledger.domain.posting;

import com.bikash.fintechsettlement.ledger.domain.transaction.LedgerTransaction;

import java.util.Objects;

public record PostingOutcome(PostingOutcomeType type, LedgerTransaction transaction) {
    public PostingOutcome {
        type = Objects.requireNonNull(type, "type");
        transaction = Objects.requireNonNull(transaction, "transaction");
    }
}
