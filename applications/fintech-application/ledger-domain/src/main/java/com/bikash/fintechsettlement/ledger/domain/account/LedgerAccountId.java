package com.bikash.fintechsettlement.ledger.domain.account;

import com.bikash.fintechsettlement.shared.identity.UuidOrder;
import com.bikash.fintechsettlement.shared.identity.UuidV7;

import java.util.Objects;
import java.util.UUID;

public record LedgerAccountId(UUID value) implements Comparable<LedgerAccountId> {
    public LedgerAccountId {
        value = UuidV7.require(value, "value");
    }

    @Override
    public int compareTo(LedgerAccountId other) {
        return UuidOrder.compare(value, other.value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
