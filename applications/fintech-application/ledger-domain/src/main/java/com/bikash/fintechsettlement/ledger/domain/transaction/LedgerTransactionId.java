package com.bikash.fintechsettlement.ledger.domain.transaction;

import com.bikash.fintechsettlement.shared.identity.UuidOrder;
import com.bikash.fintechsettlement.shared.identity.UuidV7;

import java.util.Objects;
import java.util.UUID;

public record LedgerTransactionId(UUID value) implements Comparable<LedgerTransactionId> {
    public LedgerTransactionId {
        value = UuidV7.require(value, "value");
    }

    @Override
    public int compareTo(LedgerTransactionId other) {
        return UuidOrder.compare(value, other.value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
