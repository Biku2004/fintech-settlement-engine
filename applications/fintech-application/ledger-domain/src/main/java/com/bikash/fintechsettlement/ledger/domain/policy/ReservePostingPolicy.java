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

public final class ReservePostingPolicy {
    public static final PostingPolicyReference HOLD_POLICY = new PostingPolicyReference("reserve-hold-v1", 1);
    public static final PostingPolicyReference RELEASE_POLICY = new PostingPolicyReference("reserve-release-v1", 1);

    public PostingCommand hold(
            PostingContext context,
            LedgerAccount merchantPayable,
            LedgerAccount disputeReserve,
            Money amount) {
        PostingPolicySupport.requireSourceType(context, FinancialEventType.RESERVE_HELD);
        validate(context, merchantPayable, disputeReserve, amount);
        return PostingCommand.standard(context, HOLD_POLICY, List.of(
                new PostingLine(merchantPayable, EntryDirection.DEBIT, amount),
                new PostingLine(disputeReserve, EntryDirection.CREDIT, amount)));
    }

    public PostingCommand release(
            PostingContext context,
            LedgerAccount merchantPayable,
            LedgerAccount disputeReserve,
            Money amount) {
        PostingPolicySupport.requireSourceType(context, FinancialEventType.RESERVE_RELEASED);
        validate(context, merchantPayable, disputeReserve, amount);
        return PostingCommand.standard(context, RELEASE_POLICY, List.of(
                new PostingLine(disputeReserve, EntryDirection.DEBIT, amount),
                new PostingLine(merchantPayable, EntryDirection.CREDIT, amount)));
    }

    private void validate(
            PostingContext context,
            LedgerAccount merchantPayable,
            LedgerAccount disputeReserve,
            Money amount) {
        var merchantId = context.requireMerchant();
        amount.requirePositive("reserve amount");
        PostingPolicySupport.requireRole(merchantPayable, LedgerAccountRole.MERCHANT_PAYABLE);
        PostingPolicySupport.requireRole(disputeReserve, LedgerAccountRole.DISPUTE_RESERVE);
        PostingPolicySupport.requireMerchantOwner(merchantPayable, merchantId);
        PostingPolicySupport.requireMerchantOwner(disputeReserve, merchantId);
        PostingPolicySupport.requireCurrency(amount.currency(), merchantPayable, disputeReserve);
    }
}
