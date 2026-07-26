package com.bikash.fintechsettlement.shared.identity;

import java.util.UUID;

public final class UuidOrder {
    private UuidOrder() {}

    public static int compare(UUID left, UUID right) {
        int most = Long.compareUnsigned(left.getMostSignificantBits(), right.getMostSignificantBits());
        return most != 0 ? most : Long.compareUnsigned(left.getLeastSignificantBits(), right.getLeastSignificantBits());
    }
}
