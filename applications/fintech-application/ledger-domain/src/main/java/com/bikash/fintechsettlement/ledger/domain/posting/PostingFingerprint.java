package com.bikash.fintechsettlement.ledger.domain.posting;

import java.util.Objects;

public record PostingFingerprint(String sha256Hex) {
    public PostingFingerprint {
        sha256Hex = Objects.requireNonNull(sha256Hex, "sha256Hex");
        if (!sha256Hex.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("posting fingerprint must be lowercase SHA-256 hex");
        }
    }
}
