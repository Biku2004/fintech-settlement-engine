package com.bikash.fintechsettlement.ledger.domain.posting;

import com.bikash.fintechsettlement.ledger.domain.account.AccountScope;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.stream.Collectors;

public final class PostingCanonicalizer {
    private static final Comparator<PostingLine> LINE_ORDER = Comparator
            .comparing((PostingLine line) -> line.account().id())
            .thenComparing(line -> line.direction().name())
            .thenComparingLong(line -> line.amount().amountMinor())
            .thenComparing(line -> line.account().role().name());

    public PostingFingerprint fingerprint(PostingCommand command) {
        return new PostingFingerprint(sha256Hex(canonicalForm(command)));
    }

    public String canonicalForm(PostingCommand command) {
        String merchant = command.context().merchantId() == null
                ? "-"
                : command.context().merchantId().toString();
        String reversal = command.reversalOf() == null ? "-" : command.reversalOf().toString();
        String lines = command.lines().stream()
                .sorted(LINE_ORDER)
                .map(this::canonicalLine)
                .collect(Collectors.joining(";"));

        return String.join("|",
                command.context().source().type().name(),
                command.context().source().sourceId().toString(),
                command.policy().policyId(),
                Integer.toString(command.policy().version()),
                merchant,
                reversal,
                lines);
    }

    public static String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is required by the Java platform", exception);
        }
    }

    private String canonicalLine(PostingLine line) {
        AccountScope scope = line.account().scope();
        return String.join(":",
                line.account().id().toString(),
                line.account().role().name(),
                scope.kind().name(),
                scope.ownerId().toString(),
                line.direction().name(),
                Long.toString(line.amount().amountMinor()),
                line.amount().currencyCode());
    }
}
