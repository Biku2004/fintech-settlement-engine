package com.bikash.fintechsettlement.ledger.domain.kernel;

import com.bikash.fintechsettlement.ledger.domain.account.LedgerAccountId;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class AccountLockOrder {
    private AccountLockOrder() {}

    public static List<LedgerAccountId> sortedDistinct(Collection<LedgerAccountId> accountIds) {
        Objects.requireNonNull(accountIds, "accountIds");
        return accountIds.stream().distinct().sorted().toList();
    }
}
