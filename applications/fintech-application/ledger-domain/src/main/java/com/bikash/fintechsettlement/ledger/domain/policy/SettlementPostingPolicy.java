package com.bikash.fintechsettlement.ledger.domain.policy;

import com.bikash.fintechsettlement.ledger.domain.account.LedgerAccount;
import com.bikash.fintechsettlement.ledger.domain.account.LedgerAccountRole;
import com.bikash.fintechsettlement.ledger.domain.posting.FinancialEventType;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingCommand;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingContext;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingLine;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingPolicyReference;
import com.bikash.fintechsettlement.ledger.domain.transaction.EntryDirection;
import com.bikash.fintechsettlement.shared.money.Money;

import java.util.List;

public final class SettlementPostingPolicy {
    public static final PostingPolicyReference POLICY = new PostingPolicyReference("settlement-v1", 1);

    public PostingCommand create(
            PostingContext context,
            LedgerAccount merchantPayable,
            LedgerAccount settlementCash,
            Money payout) {
        PostingPolicySupport.requireSourceType(context, FinancialEventType.SETTLEMENT_CONFIRMED);
        var merchantId = context.requireMerchant();
        payout.requirePositive("payout");
        PostingPolicySupport.requireRole(merchantPayable, LedgerAccountRole.MERCHANT_PAYABLE);
        PostingPolicySupport.requireRole(settlementCash, LedgerAccountRole.SETTLEMENT_CASH);
        PostingPolicySupport.requireMerchantOwner(merchantPayable, merchantId);
        PostingPolicySupport.requireCurrency(payout.currency(), merchantPayable, settlementCash);
        return PostingCommand.standard(context, POLICY, List.of(
                new PostingLine(merchantPayable, EntryDirection.DEBIT, payout),
                new PostingLine(settlementCash, EntryDirection.CREDIT, payout)));
    }
}
