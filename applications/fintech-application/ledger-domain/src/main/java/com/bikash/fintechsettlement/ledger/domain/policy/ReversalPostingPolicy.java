package com.bikash.fintechsettlement.ledger.domain.policy;

import com.bikash.fintechsettlement.ledger.domain.account.LedgerAccount;
import com.bikash.fintechsettlement.ledger.domain.account.LedgerAccountId;
import com.bikash.fintechsettlement.ledger.domain.error.ReversalNotAllowedException;
import com.bikash.fintechsettlement.ledger.domain.posting.FinancialEventType;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingCommand;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingContext;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingLine;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingPolicyReference;
import com.bikash.fintechsettlement.ledger.domain.transaction.LedgerTransaction;

import java.util.List;
import java.util.Map;

public final class ReversalPostingPolicy {
    public static final PostingPolicyReference POLICY = new PostingPolicyReference("reversal-v1", 1);

    public PostingCommand createFullReversal(
            PostingContext context,
            LedgerTransaction original,
            Map<LedgerAccountId, LedgerAccount> accounts) {
        if (original.isReversal()) {
            throw new ReversalNotAllowedException("a reversal transaction cannot itself be reversed");
        }
        if (context.source().type() != FinancialEventType.REVERSAL_CONFIRMED
                && context.source().type() != FinancialEventType.MANUAL_ADJUSTMENT_APPROVED) {
            throw new ReversalNotAllowedException("reversal requires an approved reversal source event");
        }
        List<PostingLine> lines = original.entries().stream()
                .map(entry -> {
                    LedgerAccount account = accounts.get(entry.accountId());
                    if (account == null) {
                        throw new ReversalNotAllowedException(
                                "original account is unavailable: " + entry.accountId());
                    }
                    return new PostingLine(account, entry.direction().opposite(), entry.amount());
                })
                .toList();
        return PostingCommand.reversal(context, POLICY, lines, original.id());
    }
}
