package com.bikash.fintechsettlement.ledger;

import com.bikash.fintechsettlement.shared.identity.UuidV7;
import com.bikash.fintechsettlement.shared.identity.UuidV7Generator;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IdentityTest {
    @Test
    void generatesUuidV7WithEmbeddedUnixMilliseconds() {
        Instant instant = Instant.parse("2026-07-26T00:00:00Z");
        var generator = new UuidV7Generator(Clock.fixed(instant, ZoneOffset.UTC), new Random(42));
        var id = generator.next();
        assertEquals(7, id.version());
        assertEquals(2, id.variant());
        assertEquals(instant.toEpochMilli(), UuidV7.unixEpochMillis(id));
    }
}
