package com.bikash.fintechsettlement.shared.identity;

import java.util.Objects;
import java.util.UUID;

public final class UuidV7 {
    private UuidV7() {}

    public static UUID require(UUID value, String fieldName) {
        Objects.requireNonNull(value, fieldName);
        if (value.version() != 7 || value.variant() != 2) {
            throw new IllegalArgumentException(fieldName + " must be an RFC 9562 UUIDv7");
        }
        return value;
    }

    public static boolean isUuidV7(UUID value) {
        return value != null && value.version() == 7 && value.variant() == 2;
    }

    public static long unixEpochMillis(UUID value) {
        require(value, "value");
        return (value.getMostSignificantBits() >>> 16) & 0x0000_FFFF_FFFF_FFFFL;
    }
}
