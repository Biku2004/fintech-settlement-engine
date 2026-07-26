package com.bikash.fintechsettlement.ledger;

import com.bikash.fintechsettlement.ledger.domain.account.AccountScope;
import com.bikash.fintechsettlement.ledger.domain.account.LedgerAccount;
import com.bikash.fintechsettlement.ledger.domain.account.LedgerAccountId;
import com.bikash.fintechsettlement.ledger.domain.account.LedgerAccountRole;
import com.bikash.fintechsettlement.ledger.domain.account.LedgerAccountStatus;
import com.bikash.fintechsettlement.ledger.domain.error.InvalidLedgerAccountException;
import com.bikash.fintechsettlement.ledger.domain.error.InvalidLedgerEntryException;
import com.bikash.fintechsettlement.ledger.domain.error.InvalidPostingPolicyException;
import com.bikash.fintechsettlement.ledger.domain.kernel.LedgerKernel;
import com.bikash.fintechsettlement.ledger.domain.policy.CapturePostingPolicy;
import com.bikash.fintechsettlement.ledger.domain.policy.RefundPostingPolicy;
import com.bikash.fintechsettlement.ledger.domain.policy.ReversalPostingPolicy;
import com.bikash.fintechsettlement.ledger.domain.policy.ReservePostingPolicy;
import com.bikash.fintechsettlement.ledger.domain.policy.SettlementPostingPolicy;
import com.bikash.fintechsettlement.ledger.domain.posting.FinancialEventType;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingCommand;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingLine;
import com.bikash.fintechsettlement.ledger.domain.transaction.EntryDirection;
import com.bikash.fintechsettlement.shared.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static com.bikash.fintechsettlement.ledger.LedgerFixture.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PostingPolicySecurityTest {
    private LedgerKernel kernel;

    @BeforeEach
    void setUp() {
        kernel = new LedgerKernel(PLATFORM_ID);
        for (var account : new LedgerAccount[]{PROCESSOR, CASH, PAYABLE, RECEIVABLE, FEE, RESERVE}) {
            kernel.registerAccount(account);
        }
    }

    @Test
    void kernelRejectsCallerForgedCapturePolicyShape() {
        PostingCommand forged = PostingCommand.standard(
                context(101, "forged-capture", FinancialEventType.PAYMENT_CAPTURE_CONFIRMED),
                CapturePostingPolicy.POLICY,
                List.of(
                        new PostingLine(CASH, EntryDirection.DEBIT, new Money(1_000, USD)),
                        new PostingLine(PAYABLE, EntryDirection.CREDIT, new Money(1_000, USD))));

        assertThrows(InvalidPostingPolicyException.class, () -> kernel.post(forged));
        assertEquals(0, kernel.transactionCount());
    }

    @Test
    void buildersRejectMismatchedSourceEventTypes() {
        assertThrows(InvalidPostingPolicyException.class, () -> new CapturePostingPolicy().create(
                context(102, "wrong-capture-source", FinancialEventType.SETTLEMENT_CONFIRMED),
                PROCESSOR, PAYABLE, FEE, new Money(1_000, USD), new Money(100, USD)));

        assertThrows(InvalidPostingPolicyException.class, () -> new RefundPostingPolicy().create(
                context(103, "wrong-refund-source", FinancialEventType.PAYMENT_CAPTURE_CONFIRMED),
                PAYABLE, RECEIVABLE, FEE, PROCESSOR,
                new Money(500, USD), new Money(10, USD), new Money(490, USD)));

        assertThrows(InvalidPostingPolicyException.class, () -> new SettlementPostingPolicy().create(
                context(104, "wrong-settlement-source", FinancialEventType.RESERVE_HELD),
                PAYABLE, CASH, new Money(500, USD)));

        ReservePostingPolicy reserve = new ReservePostingPolicy();
        assertThrows(InvalidPostingPolicyException.class, () -> reserve.hold(
                context(105, "wrong-reserve-source", FinancialEventType.RESERVE_RELEASED),
                PAYABLE, RESERVE, new Money(100, USD)));
    }

    @Test
    void transactionRejectsDifferentPlatformOwners() {
        LedgerAccount foreignFee = new LedgerAccount(
                new LedgerAccountId(UUID.fromString("00000000-0000-7000-8000-000000000901")),
                LedgerAccountRole.PLATFORM_FEE_REVENUE,
                AccountScope.platform(UUID.fromString("00000000-0000-7000-8000-000000000999")),
                USD,
                LedgerAccountStatus.ACTIVE);
        assertThrows(InvalidLedgerAccountException.class, () -> kernel.registerAccount(foreignFee));
        assertEquals(0, kernel.transactionCount());
    }
    @Test
    void kernelRejectsDirectCommandWithMismatchedSourceType() {
        PostingCommand forged = PostingCommand.standard(
                context(107, "direct-wrong-source", FinancialEventType.SETTLEMENT_CONFIRMED),
                CapturePostingPolicy.POLICY,
                List.of(
                        new PostingLine(PROCESSOR, EntryDirection.DEBIT, new Money(1_000, USD)),
                        new PostingLine(PAYABLE, EntryDirection.CREDIT, new Money(900, USD)),
                        new PostingLine(FEE, EntryDirection.CREDIT, new Money(100, USD))));

        assertThrows(InvalidPostingPolicyException.class, () -> kernel.post(forged));
        assertEquals(0, kernel.transactionCount());
    }

    @Test
    void kernelRejectsOversizedForgedReversalBeforeCanonicalization() {
        var original = kernel.post(new CapturePostingPolicy().create(
                context(108, "reversal-original", FinancialEventType.PAYMENT_CAPTURE_CONFIRMED),
                PROCESSOR, PAYABLE, FEE, new Money(1_000, USD), new Money(100, USD))).transaction();
        PostingCommand forged = PostingCommand.reversal(
                context(109, "oversized-reversal", FinancialEventType.REVERSAL_CONFIRMED),
                ReversalPostingPolicy.POLICY,
                List.of(
                        new PostingLine(PROCESSOR, EntryDirection.CREDIT, new Money(200, USD)),
                        new PostingLine(PROCESSOR, EntryDirection.CREDIT, new Money(200, USD)),
                        new PostingLine(PAYABLE, EntryDirection.DEBIT, new Money(200, USD)),
                        new PostingLine(PAYABLE, EntryDirection.DEBIT, new Money(200, USD)),
                        new PostingLine(FEE, EntryDirection.DEBIT, new Money(200, USD))),
                original.id());

        assertThrows(InvalidPostingPolicyException.class, () -> kernel.post(forged));
        assertEquals(1, kernel.transactionCount());
    }

}
