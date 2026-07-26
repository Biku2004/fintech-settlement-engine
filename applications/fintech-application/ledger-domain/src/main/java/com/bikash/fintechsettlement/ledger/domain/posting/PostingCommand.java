package com.bikash.fintechsettlement.ledger.domain.posting;

import com.bikash.fintechsettlement.ledger.domain.transaction.LedgerTransactionId;

import java.util.List;
import java.util.Objects;

public record PostingCommand(
        PostingContext context,
        PostingPolicyReference policy,
        List<PostingLine> lines,
        LedgerTransactionId reversalOf) {

    public PostingCommand {
        context = Objects.requireNonNull(context, "context");
        policy = Objects.requireNonNull(policy, "policy");
        lines = List.copyOf(Objects.requireNonNull(lines, "lines"));
    }

    public static PostingCommand standard(
            PostingContext context, PostingPolicyReference policy, List<PostingLine> lines) {
        return new PostingCommand(context, policy, lines, null);
    }

    public static PostingCommand reversal(
            PostingContext context,
            PostingPolicyReference policy,
            List<PostingLine> lines,
            LedgerTransactionId originalTransactionId) {
        return new PostingCommand(
                context,
                policy,
                lines,
                Objects.requireNonNull(originalTransactionId, "originalTransactionId"));
    }

    public boolean isReversal() {
        return reversalOf != null;
    }
}
