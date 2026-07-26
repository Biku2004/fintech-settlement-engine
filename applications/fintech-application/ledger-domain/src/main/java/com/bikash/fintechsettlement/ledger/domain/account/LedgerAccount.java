package com.bikash.fintechsettlement.ledger.domain.account;

import com.bikash.fintechsettlement.ledger.domain.error.InvalidLedgerAccountException;
import com.bikash.fintechsettlement.ledger.domain.transaction.EntryDirection;

import java.util.Currency;
import java.util.Objects;

public record LedgerAccount(
        LedgerAccountId id,
        LedgerAccountRole role,
        AccountScope scope,
        Currency currency,
        LedgerAccountStatus status) {

    public LedgerAccount {
        id = Objects.requireNonNull(id, "id");
        role = Objects.requireNonNull(role, "role");
        scope = Objects.requireNonNull(scope, "scope");
        currency = Objects.requireNonNull(currency, "currency");
        status = Objects.requireNonNull(status, "status");
        if (role.requiredScope() != scope.kind()) {
            throw new InvalidLedgerAccountException(
                    role + " requires " + role.requiredScope() + " scope, not " + scope.kind());
        }
        if (currency.getDefaultFractionDigits() < 0 || currency.getDefaultFractionDigits() > 3) {
            throw new InvalidLedgerAccountException("unsupported currency " + currency.getCurrencyCode());
        }
    }

    public LedgerAccountType accountType() {
        return role.accountType();
    }

    public EntryDirection normalBalance() {
        return accountType().normalBalance();
    }

    public boolean isActive() {
        return status == LedgerAccountStatus.ACTIVE;
    }
}
