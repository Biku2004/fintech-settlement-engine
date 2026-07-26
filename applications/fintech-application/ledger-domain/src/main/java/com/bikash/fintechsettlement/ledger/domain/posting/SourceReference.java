package com.bikash.fintechsettlement.ledger.domain.posting;

import com.bikash.fintechsettlement.shared.identity.UuidV7;

import java.util.Objects;
import java.util.UUID;

public record SourceReference(FinancialEventType type, UUID sourceId) {
    public SourceReference {
        type = Objects.requireNonNull(type, "type");
        sourceId = UuidV7.require(sourceId, "sourceId");
    }
}
