package com.bikash.fintechsettlement.ledger.domain.kernel;

import com.bikash.fintechsettlement.ledger.domain.account.LedgerAccountId;
import com.bikash.fintechsettlement.shared.money.Money;

import java.time.Instant;
import java.util.Objects;

public record BalanceSnapshot(
        LedgerAccountId accountId,
        Money balance,
        long includedTransactionCount,
        Instant createdAt) {
    public BalanceSnapshot {
        accountId = Objects.requireNonNull(accountId, "accountId");
        balance = Objects.requireNonNull(balance, "balance");
        if (includedTransactionCount < 0) {
            throw new IllegalArgumentException("includedTransactionCount must not be negative");
        }
        createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }
}
