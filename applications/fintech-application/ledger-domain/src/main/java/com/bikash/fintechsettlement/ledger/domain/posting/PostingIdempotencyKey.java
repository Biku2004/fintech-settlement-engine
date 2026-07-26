package com.bikash.fintechsettlement.ledger.domain.posting;

import java.util.Objects;
import java.util.regex.Pattern;

public record PostingIdempotencyKey(String value) {
    private static final Pattern ALLOWED = Pattern.compile("[A-Za-z0-9._:-]{1,128}");

    public PostingIdempotencyKey {
        value = Objects.requireNonNull(value, "value");
        if (!ALLOWED.matcher(value).matches()) {
            throw new IllegalArgumentException("posting idempotency key must be 1-128 safe characters");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
