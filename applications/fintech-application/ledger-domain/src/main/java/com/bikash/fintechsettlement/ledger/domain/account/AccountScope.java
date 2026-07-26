package com.bikash.fintechsettlement.ledger.domain.account;

import com.bikash.fintechsettlement.ledger.domain.identity.MerchantId;
import com.bikash.fintechsettlement.shared.identity.UuidV7;

import java.util.Objects;
import java.util.UUID;

public record AccountScope(ScopeKind kind, UUID ownerId) {
    public AccountScope {
        kind = Objects.requireNonNull(kind, "kind");
        ownerId = UuidV7.require(ownerId, "ownerId");
    }

    public static AccountScope platform(UUID platformId) {
        return new AccountScope(ScopeKind.PLATFORM, platformId);
    }

    public static AccountScope merchant(MerchantId merchantId) {
        Objects.requireNonNull(merchantId, "merchantId");
        return new AccountScope(ScopeKind.MERCHANT, merchantId.value());
    }

    public boolean belongsTo(MerchantId merchantId) {
        return kind == ScopeKind.MERCHANT && ownerId.equals(merchantId.value());
    }
}
