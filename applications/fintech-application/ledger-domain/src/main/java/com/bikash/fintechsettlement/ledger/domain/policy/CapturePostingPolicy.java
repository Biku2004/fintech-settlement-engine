package com.bikash.fintechsettlement.ledger.domain.policy;

import com.bikash.fintechsettlement.ledger.domain.account.LedgerAccount;
import com.bikash.fintechsettlement.ledger.domain.account.LedgerAccountRole;
import com.bikash.fintechsettlement.ledger.domain.error.InvalidPostingPolicyException;
import com.bikash.fintechsettlement.ledger.domain.posting.FinancialEventType;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingCommand;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingContext;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingLine;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingPolicyReference;
import com.bikash.fintechsettlement.ledger.domain.transaction.EntryDirection;
import com.bikash.fintechsettlement.shared.money.Money;

import java.util.ArrayList;
import java.util.List;

public final class CapturePostingPolicy {
    public static final PostingPolicyReference POLICY = new PostingPolicyReference("capture-v1", 1);

    public PostingCommand create(
            PostingContext context,
            LedgerAccount processorClearing,
            LedgerAccount merchantPayable,
            LedgerAccount platformFeeRevenue,
            Money gross,
            Money fee) {
        PostingPolicySupport.requireSourceType(context, FinancialEventType.PAYMENT_CAPTURE_CONFIRMED);
        var merchantId = context.requireMerchant();
        gross.requirePositive("gross");
        fee.requireNonNegative("fee").requireAtMost(gross, "fee");
        PostingPolicySupport.requireCurrency(gross.currency(), fee);
        PostingPolicySupport.requireCurrency(
                gross.currency(), processorClearing, merchantPayable, platformFeeRevenue);
        PostingPolicySupport.requireRole(processorClearing, LedgerAccountRole.PROCESSOR_CLEARING);
        PostingPolicySupport.requireRole(merchantPayable, LedgerAccountRole.MERCHANT_PAYABLE);
        PostingPolicySupport.requireRole(platformFeeRevenue, LedgerAccountRole.PLATFORM_FEE_REVENUE);
        PostingPolicySupport.requireMerchantOwner(merchantPayable, merchantId);

        Money net = gross.minus(fee);
        List<PostingLine> lines = new ArrayList<>();
        lines.add(new PostingLine(processorClearing, EntryDirection.DEBIT, gross));
        if (net.isPositive()) {
            lines.add(new PostingLine(merchantPayable, EntryDirection.CREDIT, net));
        }
        if (fee.isPositive()) {
            lines.add(new PostingLine(platformFeeRevenue, EntryDirection.CREDIT, fee));
        }
        if (lines.size() < 2) {
            throw new InvalidPostingPolicyException("capture must create at least two non-zero entries");
        }
        return PostingCommand.standard(context, POLICY, lines);
    }
}
