package com.bikash.fintechsettlement.ledger;

import com.bikash.fintechsettlement.ledger.domain.error.PostingIdempotencyConflictException;
import com.bikash.fintechsettlement.ledger.domain.error.ReversalNotAllowedException;
import com.bikash.fintechsettlement.ledger.domain.kernel.LedgerKernel;
import com.bikash.fintechsettlement.ledger.domain.policy.CapturePostingPolicy;
import com.bikash.fintechsettlement.ledger.domain.policy.ReversalPostingPolicy;
import com.bikash.fintechsettlement.ledger.domain.posting.FinancialEventType;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingOutcomeType;
import com.bikash.fintechsettlement.shared.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.Executors;

import static com.bikash.fintechsettlement.ledger.LedgerFixture.*;
import static org.junit.jupiter.api.Assertions.*;

class LedgerKernelTest {
    private LedgerKernel kernel;
    private CapturePostingPolicy capture;

    @BeforeEach
    void setUp() {
        kernel = new LedgerKernel();
        for (var account : new com.bikash.fintechsettlement.ledger.domain.account.LedgerAccount[]{PROCESSOR, CASH, PAYABLE, RECEIVABLE, FEE, RESERVE}) {
            kernel.registerAccount(account);
        }
        capture = new CapturePostingPolicy();
    }

    @Test
    void postsBalancedCaptureAndCalculatesNormalBalances() {
        var command = capture.create(context(1, "capture-1", FinancialEventType.PAYMENT_CAPTURE_CONFIRMED),
                PROCESSOR, PAYABLE, FEE, new Money(10_000, USD), new Money(300, USD));
        var outcome = kernel.post(command);
        assertEquals(PostingOutcomeType.CREATED, outcome.type());
        assertEquals(10_000, kernel.authoritativeBalance(PROCESSOR.id()).amountMinor());
        assertEquals(9_700, kernel.authoritativeBalance(PAYABLE.id()).amountMinor());
        assertEquals(300, kernel.authoritativeBalance(FEE.id()).amountMinor());
    }

    @Test
    void sameKeyAndEffectReplaysButChangedEffectConflicts() {
        var first = capture.create(context(2, "capture-2", FinancialEventType.PAYMENT_CAPTURE_CONFIRMED),
                PROCESSOR, PAYABLE, FEE, new Money(10_000, USD), new Money(300, USD));
        var replayContext = context(3, "capture-2", FinancialEventType.PAYMENT_CAPTURE_CONFIRMED);
        replayContext = new com.bikash.fintechsettlement.ledger.domain.posting.PostingContext(
                replayContext.transactionId(), replayContext.idempotencyKey(), first.context().source(),
                replayContext.correlationId(), replayContext.merchantId(), replayContext.occurredAt(), replayContext.recordedAt(), replayContext.reason());
        var replay = capture.create(replayContext, PROCESSOR, PAYABLE, FEE,
                new Money(10_000, USD), new Money(300, USD));
        assertEquals(PostingOutcomeType.CREATED, kernel.post(first).type());
        assertEquals(PostingOutcomeType.IDEMPOTENT_REPLAY, kernel.post(replay).type());

        var changed = capture.create(replayContext, PROCESSOR, PAYABLE, FEE,
                new Money(10_000, USD), new Money(301, USD));
        assertThrows(PostingIdempotencyConflictException.class, () -> kernel.post(changed));
    }

    @Test
    void concurrentDuplicateCommandsCreateOneTransaction() throws Exception {
        var command = capture.create(context(4, "capture-4", FinancialEventType.PAYMENT_CAPTURE_CONFIRMED),
                PROCESSOR, PAYABLE, FEE, new Money(10_000, USD), new Money(300, USD));
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var tasks = new ArrayList<java.util.concurrent.Future<PostingOutcomeType>>();
            for (int i = 0; i < 20; i++) {
                tasks.add(executor.submit(() -> kernel.post(command).type()));
            }
            long created = 0;
            for (var task : tasks) {
                if (task.get() == PostingOutcomeType.CREATED) created++;
            }
            assertEquals(1, created);
            assertEquals(1, kernel.transactionCount());
        }
    }

    @Test
    void fullReversalRestoresBalancesAndCannotRunTwice() {
        var original = kernel.post(capture.create(context(5, "capture-5", FinancialEventType.PAYMENT_CAPTURE_CONFIRMED),
                PROCESSOR, PAYABLE, FEE, new Money(10_000, USD), new Money(300, USD))).transaction();
        var reversalPolicy = new ReversalPostingPolicy();
        var reversal = reversalPolicy.createFullReversal(
                context(6, "reverse-5", FinancialEventType.REVERSAL_CONFIRMED), original, kernel.accountMap());
        kernel.post(reversal);
        assertEquals(0, kernel.authoritativeBalance(PROCESSOR.id()).amountMinor());
        assertEquals(0, kernel.authoritativeBalance(PAYABLE.id()).amountMinor());
        assertEquals(0, kernel.authoritativeBalance(FEE.id()).amountMinor());

        var second = reversalPolicy.createFullReversal(
                context(7, "reverse-5-again", FinancialEventType.REVERSAL_CONFIRMED), original, kernel.accountMap());
        assertThrows(ReversalNotAllowedException.class, () -> kernel.post(second));
    }
}
