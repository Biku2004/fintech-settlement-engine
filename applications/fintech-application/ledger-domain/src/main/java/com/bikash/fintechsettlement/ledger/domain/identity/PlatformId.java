package com.bikash.fintechsettlement.ledger.domain.identity;

import com.bikash.fintechsettlement.shared.identity.UuidV7;

import java.util.UUID;

/** Identifies the platform boundary that owns platform-scoped ledger accounts. */
public record PlatformId(UUID value) {
    public PlatformId {
        value = UuidV7.require(value, "platformId");
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
