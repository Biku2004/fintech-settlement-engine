package com.bikash.fintechsettlement.shared.identity;

import java.security.SecureRandom;
import java.time.Clock;
import java.util.Objects;
import java.util.UUID;
import java.util.random.RandomGenerator;

/** Generates RFC 9562 UUIDv7 values using a 48-bit Unix-millisecond timestamp. */
public final class UuidV7Generator {
    private static final long TIMESTAMP_MASK = 0x0000_FFFF_FFFF_FFFFL;
    private static final long RANDOM_B_MASK = 0x3FFF_FFFF_FFFF_FFFFL;
    private static final long RFC_4122_VARIANT = 0x8000_0000_0000_0000L;

    private final Clock clock;
    private final RandomGenerator random;

    public UuidV7Generator() {
        this(Clock.systemUTC(), new SecureRandom());
    }

    public UuidV7Generator(Clock clock, RandomGenerator random) {
        this.clock = Objects.requireNonNull(clock, "clock");
        this.random = Objects.requireNonNull(random, "random");
    }

    public UUID next() {
        long timestamp = clock.millis();
        if (timestamp < 0 || timestamp > TIMESTAMP_MASK) {
            throw new IllegalStateException("clock is outside UUIDv7 timestamp range");
        }
        long randomA = random.nextInt(1 << 12);
        long mostSignificantBits = (timestamp << 16) | 0x7000L | randomA;
        long leastSignificantBits = RFC_4122_VARIANT | (random.nextLong() & RANDOM_B_MASK);
        return new UUID(mostSignificantBits, leastSignificantBits);
    }
}
