package com.bikash.fintechsettlement.ledger.domain.posting;

import java.util.Objects;
import java.util.regex.Pattern;

public record PostingPolicyReference(String policyId, int version) {
    private static final Pattern ID = Pattern.compile("[a-z][a-z0-9-]{1,63}");

    public PostingPolicyReference {
        policyId = Objects.requireNonNull(policyId, "policyId");
        if (!ID.matcher(policyId).matches()) {
            throw new IllegalArgumentException("invalid posting policy ID");
        }
        if (version <= 0) {
            throw new IllegalArgumentException("posting policy version must be positive");
        }
    }
}
