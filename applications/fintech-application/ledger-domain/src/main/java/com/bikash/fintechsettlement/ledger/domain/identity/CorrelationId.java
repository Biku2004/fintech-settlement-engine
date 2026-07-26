package com.bikash.fintechsettlement.ledger.domain.identity;

import com.bikash.fintechsettlement.shared.identity.UuidOrder;
import com.bikash.fintechsettlement.shared.identity.UuidV7;

import java.util.Objects;
import java.util.UUID;

public record CorrelationId(UUID value) implements Comparable<CorrelationId> {
    public CorrelationId {
        value = UuidV7.require(value, "value");
    }

    @Override
    public int compareTo(CorrelationId other) {
        return UuidOrder.compare(value, other.value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
