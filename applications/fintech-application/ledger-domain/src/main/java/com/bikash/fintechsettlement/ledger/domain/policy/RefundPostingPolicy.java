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

import java.util.ArrayList;
import java.util.List;

public final class RefundPostingPolicy {
    public static final PostingPolicyReference POLICY = new PostingPolicyReference("refund-v1", 1);

    public PostingCommand create(
            PostingContext context,
            LedgerAccount merchantPayable,
            LedgerAccount merchantReceivable,
            LedgerAccount platformFeeRevenue,
            LedgerAccount processorClearing,
            Money totalRefund,
            Money refundedFeeShare,
            Money availablePayable) {
        PostingPolicySupport.requireSourceType(context, FinancialEventType.PAYMENT_REFUND_CONFIRMED);
        var merchantId = context.requireMerchant();
        totalRefund.requirePositive("totalRefund");
        refundedFeeShare.requireNonNegative("refundedFeeShare").requireAtMost(totalRefund, "refundedFeeShare");
        Money merchantShare = totalRefund.minus(refundedFeeShare);
        availablePayable.requireNonNegative("availablePayable").requireAtMost(merchantShare, "availablePayable");
        Money shortfall = merchantShare.minus(availablePayable);

        PostingPolicySupport.requireCurrency(totalRefund.currency(), refundedFeeShare, availablePayable);
        PostingPolicySupport.requireCurrency(
                totalRefund.currency(), merchantPayable, merchantReceivable, platformFeeRevenue, processorClearing);
        PostingPolicySupport.requireRole(merchantPayable, LedgerAccountRole.MERCHANT_PAYABLE);
        PostingPolicySupport.requireRole(merchantReceivable, LedgerAccountRole.MERCHANT_RECEIVABLE);
        PostingPolicySupport.requireRole(platformFeeRevenue, LedgerAccountRole.PLATFORM_FEE_REVENUE);
        PostingPolicySupport.requireRole(processorClearing, LedgerAccountRole.PROCESSOR_CLEARING);
        PostingPolicySupport.requireMerchantOwner(merchantPayable, merchantId);
        PostingPolicySupport.requireMerchantOwner(merchantReceivable, merchantId);

        List<PostingLine> lines = new ArrayList<>();
        if (availablePayable.isPositive()) {
            lines.add(new PostingLine(merchantPayable, EntryDirection.DEBIT, availablePayable));
        }
        if (shortfall.isPositive()) {
            lines.add(new PostingLine(merchantReceivable, EntryDirection.DEBIT, shortfall));
        }
        if (refundedFeeShare.isPositive()) {
            lines.add(new PostingLine(platformFeeRevenue, EntryDirection.DEBIT, refundedFeeShare));
        }
        lines.add(new PostingLine(processorClearing, EntryDirection.CREDIT, totalRefund));
        return PostingCommand.standard(context, POLICY, lines);
    }
}
