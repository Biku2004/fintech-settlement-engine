package com.bikash.fintechsettlement.selftest;

import com.bikash.fintechsettlement.ledger.domain.account.*;
import com.bikash.fintechsettlement.ledger.domain.error.*;
import com.bikash.fintechsettlement.ledger.domain.identity.CorrelationId;
import com.bikash.fintechsettlement.ledger.domain.identity.MerchantId;
import com.bikash.fintechsettlement.ledger.domain.kernel.AccountLockOrder;
import com.bikash.fintechsettlement.ledger.domain.kernel.LedgerKernel;
import com.bikash.fintechsettlement.ledger.domain.policy.*;
import com.bikash.fintechsettlement.ledger.domain.posting.*;
import com.bikash.fintechsettlement.ledger.domain.transaction.*;
import com.bikash.fintechsettlement.shared.identity.UuidV7;
import com.bikash.fintechsettlement.shared.identity.UuidV7Generator;
import com.bikash.fintechsettlement.shared.money.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public final class Batch1SelfTest {
    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final UUID PLATFORM_ID = uuid(1);
    private static final MerchantId MERCHANT = new MerchantId(uuid(2));
    private static final Instant TIME = Instant.parse("2026-07-26T00:00:00Z");

    private static final LedgerAccount PROCESSOR = account(101, LedgerAccountRole.PROCESSOR_CLEARING, AccountScope.platform(PLATFORM_ID), USD);
    private static final LedgerAccount CASH = account(102, LedgerAccountRole.SETTLEMENT_CASH, AccountScope.platform(PLATFORM_ID), USD);
    private static final LedgerAccount PAYABLE = account(103, LedgerAccountRole.MERCHANT_PAYABLE, AccountScope.merchant(MERCHANT), USD);
    private static final LedgerAccount RECEIVABLE = account(104, LedgerAccountRole.MERCHANT_RECEIVABLE, AccountScope.merchant(MERCHANT), USD);
    private static final LedgerAccount FEE = account(105, LedgerAccountRole.PLATFORM_FEE_REVENUE, AccountScope.platform(PLATFORM_ID), USD);
    private static final LedgerAccount RESERVE = account(106, LedgerAccountRole.DISPUTE_RESERVE, AccountScope.merchant(MERCHANT), USD);

    private int passed;

    public static void main(String[] args) throws Exception {
        new Batch1SelfTest().run();
    }

    private void run() throws Exception {
        test("UUIDv7 generation", this::uuidV7Generation);
        test("money exact arithmetic", this::moneyExactArithmetic);
        test("money currency mismatch", this::moneyCurrencyMismatch);
        test("money ISO fraction digits", this::moneyFractionDigits);
        test("money overflow", this::moneyOverflow);
        test("account role scope", this::accountRoleScope);
        test("capture posting", this::capturePosting);
        test("zero-fee capture", this::zeroFeeCapture);
        test("unbalanced rejected", this::unbalancedRejected);
        test("one-sided rejected", this::oneSidedRejected);
        test("deterministic entry order", this::deterministicEntryOrder);
        test("idempotent replay", this::idempotentReplay);
        test("idempotency conflict", this::idempotencyConflict);
        test("source replay", this::sourceReplay);
        test("source conflict", this::sourceConflict);
        test("concurrent duplicate", this::concurrentDuplicate);
        test("normal balances", this::normalBalances);
        test("full reversal", this::fullReversal);
        test("duplicate reversal rejected", this::duplicateReversalRejected);
        test("transaction immutable", this::transactionImmutable);
        test("account lock order", this::accountLockOrder);
        test("refund shortfall", this::refundShortfall);
        test("settlement policy", this::settlementPolicy);
        test("reserve hold release", this::reserveHoldRelease);
        test("fee half-up", this::feeHalfUp);
        test("snapshot derived", this::snapshotDerived);
        test("unknown account rejected", this::unknownAccountRejected);
        test("closed account rejected", this::closedAccountRejected);
        test("randomized captures remain balanced", this::randomizedCapturesRemainBalanced);
        System.out.println("BATCH 1 SELF-TEST PASSED: " + passed + " checks");
    }

    private void uuidV7Generation() {
        UuidV7Generator generator = new UuidV7Generator(Clock.fixed(TIME, ZoneOffset.UTC), new Random(42L));
        UUID id = generator.next();
        check(UuidV7.isUuidV7(id), "UUID version and variant");
        check(UuidV7.unixEpochMillis(id) == TIME.toEpochMilli(), "UUID timestamp");
    }

    private void moneyExactArithmetic() {
        Money ten = new Money(1_000, USD);
        check(ten.plus(new Money(250, USD)).amountMinor() == 1_250, "addition");
        check(ten.minus(new Money(250, USD)).amountMinor() == 750, "subtraction");
        check(ten.multiply(3).amountMinor() == 3_000, "multiplication");
        check(ten.negate().amountMinor() == -1_000, "negation");
    }

    private void moneyCurrencyMismatch() {
        expect(CurrencyMismatchException.class, () -> new Money(100, USD).plus(new Money(100, EUR)));
    }

    private void moneyFractionDigits() {
        check(Money.fromMajor("12.34", USD).amountMinor() == 1_234, "USD conversion");
        check(Money.fromMajor("12", Currency.getInstance("JPY")).amountMinor() == 12, "JPY conversion");
        check(Money.fromMajor("12.345", Currency.getInstance("KWD")).amountMinor() == 12_345, "KWD conversion");
        expect(InvalidMoneyAmountException.class, () -> Money.fromMajor("12.345", USD));
    }

    private void moneyOverflow() {
        expect(ArithmeticException.class, () -> new Money(Long.MAX_VALUE, USD).plus(new Money(1, USD)));
        expect(InvalidMoneyAmountException.class,
                () -> new Money(Long.MIN_VALUE, USD).requireWithinAbsoluteLimit(1_000_000, "amount"));
    }

    private void accountRoleScope() {
        expect(InvalidLedgerAccountException.class, () -> account(
                999, LedgerAccountRole.MERCHANT_PAYABLE, AccountScope.platform(PLATFORM_ID), USD));
        check(PAYABLE.accountType() == LedgerAccountType.LIABILITY, "payable type");
        check(PAYABLE.normalBalance() == EntryDirection.CREDIT, "payable normal balance");
    }

    private void capturePosting() {
        LedgerKernel kernel = kernel();
        var tx = kernel.post(capture(1, "capture-1", 10_000, 300)).transaction();
        check(tx.entries().size() == 3, "capture entry count");
        check(tx.debitTotal().amountMinor() == 10_000, "capture debit");
        check(tx.creditTotal().amountMinor() == 10_000, "capture credit");
        check(tx.checksum().matches("[0-9a-f]{64}"), "checksum");
    }

    private void zeroFeeCapture() {
        LedgerKernel kernel = kernel();
        var tx = kernel.post(capture(2, "capture-2", 10_000, 0)).transaction();
        check(tx.entries().size() == 2, "zero fee omits zero entry");
    }

    private void unbalancedRejected() {
        LedgerKernel kernel = kernel();
        PostingContext context = context(3, "bad-balance", FinancialEventType.MANUAL_ADJUSTMENT_APPROVED);
        PostingCommand command = PostingCommand.standard(context, new PostingPolicyReference("manual-v1", 1), List.of(
                new PostingLine(PROCESSOR, EntryDirection.DEBIT, money(100)),
                new PostingLine(PAYABLE, EntryDirection.CREDIT, money(99))));
        expect(UnbalancedTransactionException.class, () -> kernel.post(command));
        check(kernel.transactionCount() == 0, "unbalanced creates no transaction");
    }

    private void oneSidedRejected() {
        LedgerKernel kernel = kernel();
        PostingCommand command = PostingCommand.standard(
                context(4, "one-sided", FinancialEventType.MANUAL_ADJUSTMENT_APPROVED),
                new PostingPolicyReference("manual-v1", 1),
                List.of(
                        new PostingLine(PROCESSOR, EntryDirection.DEBIT, money(50)),
                        new PostingLine(CASH, EntryDirection.DEBIT, money(50))));
        expect(InvalidLedgerEntryException.class, () -> kernel.post(command));
    }

    private void deterministicEntryOrder() {
        LedgerKernel left = kernel();
        LedgerKernel right = kernel();
        PostingContext c1 = context(5, "order-a", FinancialEventType.MANUAL_ADJUSTMENT_APPROVED);
        PostingContext c2 = context(6, "order-b", FinancialEventType.MANUAL_ADJUSTMENT_APPROVED);
        SourceReference sameSource = c1.source();
        c2 = new PostingContext(c2.transactionId(), c2.idempotencyKey(), sameSource, c2.correlationId(), c2.merchantId(), c2.occurredAt(), c2.recordedAt(), c2.reason());
        var policy = new PostingPolicyReference("manual-v1", 1);
        var linesA = List.of(
                new PostingLine(FEE, EntryDirection.CREDIT, money(10)),
                new PostingLine(PROCESSOR, EntryDirection.DEBIT, money(100)),
                new PostingLine(PAYABLE, EntryDirection.CREDIT, money(90)));
        var linesB = List.of(linesA.get(1), linesA.get(2), linesA.get(0));
        var a = left.post(PostingCommand.standard(c1, policy, linesA)).transaction();
        var b = right.post(PostingCommand.standard(c2, policy, linesB)).transaction();
        check(a.entries().stream().map(LedgerEntry::accountId).toList()
                .equals(b.entries().stream().map(LedgerEntry::accountId).toList()), "canonical order");
    }

    private void idempotentReplay() {
        LedgerKernel kernel = kernel();
        PostingCommand original = capture(7, "same-key", 10_000, 300);
        PostingContext replayContext = context(8, "same-key", FinancialEventType.PAYMENT_CAPTURE_CONFIRMED);
        replayContext = replaceSource(replayContext, original.context().source());
        PostingCommand replay = new CapturePostingPolicy().create(replayContext, PROCESSOR, PAYABLE, FEE, money(10_000), money(300));
        check(kernel.post(original).type() == PostingOutcomeType.CREATED, "created");
        PostingOutcome outcome = kernel.post(replay);
        check(outcome.type() == PostingOutcomeType.IDEMPOTENT_REPLAY, "replay status");
        check(kernel.transactionCount() == 1, "single effect");
    }

    private void idempotencyConflict() {
        LedgerKernel kernel = kernel();
        PostingCommand original = capture(9, "conflict-key", 10_000, 300);
        kernel.post(original);
        PostingContext changedContext = replaceSource(
                context(10, "conflict-key", FinancialEventType.PAYMENT_CAPTURE_CONFIRMED),
                original.context().source());
        PostingCommand changed = new CapturePostingPolicy().create(changedContext, PROCESSOR, PAYABLE, FEE, money(10_000), money(301));
        expect(PostingIdempotencyConflictException.class, () -> kernel.post(changed));
        check(kernel.transactionCount() == 1, "conflict no second effect");
    }

    private void sourceReplay() {
        LedgerKernel kernel = kernel();
        PostingCommand original = capture(11, "key-a", 10_000, 300);
        kernel.post(original);
        PostingContext context = replaceSource(
                context(12, "key-b", FinancialEventType.PAYMENT_CAPTURE_CONFIRMED), original.context().source());
        PostingCommand duplicateSource = new CapturePostingPolicy().create(context, PROCESSOR, PAYABLE, FEE, money(10_000), money(300));
        check(kernel.post(duplicateSource).type() == PostingOutcomeType.SOURCE_REPLAY, "source replay");
        check(kernel.transactionCount() == 1, "source single effect");
    }

    private void sourceConflict() {
        LedgerKernel kernel = kernel();
        PostingCommand original = capture(13, "src-key-a", 10_000, 300);
        kernel.post(original);
        PostingContext context = replaceSource(
                context(14, "src-key-b", FinancialEventType.PAYMENT_CAPTURE_CONFIRMED), original.context().source());
        PostingCommand conflict = new CapturePostingPolicy().create(context, PROCESSOR, PAYABLE, FEE, money(10_000), money(400));
        expect(SourceFinancialEventConflictException.class, () -> kernel.post(conflict));
    }

    private void concurrentDuplicate() throws Exception {
        LedgerKernel kernel = kernel();
        PostingCommand command = capture(15, "concurrent", 10_000, 300);
        List<Callable<PostingOutcomeType>> tasks = new ArrayList<>();
        for (int i = 0; i < 32; i++) tasks.add(() -> kernel.post(command).type());
        try (var pool = Executors.newFixedThreadPool(8)) {
            var results = pool.invokeAll(tasks);
            long created = 0;
            for (var result : results) if (result.get() == PostingOutcomeType.CREATED) created++;
            check(created == 1, "one creator");
            check(kernel.transactionCount() == 1, "one transaction");
        }
    }

    private void normalBalances() {
        LedgerKernel kernel = kernel();
        kernel.post(capture(16, "balances", 10_000, 300));
        check(kernel.authoritativeBalance(PROCESSOR.id()).amountMinor() == 10_000, "asset debit balance");
        check(kernel.authoritativeBalance(PAYABLE.id()).amountMinor() == 9_700, "liability credit balance");
        check(kernel.authoritativeBalance(FEE.id()).amountMinor() == 300, "revenue credit balance");
    }

    private void fullReversal() {
        LedgerKernel kernel = kernel();
        LedgerTransaction original = kernel.post(capture(17, "reverse-original", 10_000, 300)).transaction();
        PostingCommand reversal = new ReversalPostingPolicy().createFullReversal(
                context(18, "reverse", FinancialEventType.REVERSAL_CONFIRMED), original, kernel.accountMap());
        LedgerTransaction reversed = kernel.post(reversal).transaction();
        check(reversed.reversalOf().equals(original.id()), "reversal link");
        check(kernel.authoritativeBalance(PROCESSOR.id()).isZero(), "processor restored");
        check(kernel.authoritativeBalance(PAYABLE.id()).isZero(), "payable restored");
        check(kernel.authoritativeBalance(FEE.id()).isZero(), "fee restored");
    }

    private void duplicateReversalRejected() {
        LedgerKernel kernel = kernel();
        LedgerTransaction original = kernel.post(capture(19, "reverse-original-2", 10_000, 300)).transaction();
        ReversalPostingPolicy policy = new ReversalPostingPolicy();
        kernel.post(policy.createFullReversal(context(20, "reverse-1", FinancialEventType.REVERSAL_CONFIRMED), original, kernel.accountMap()));
        PostingCommand second = policy.createFullReversal(context(21, "reverse-2", FinancialEventType.REVERSAL_CONFIRMED), original, kernel.accountMap());
        expect(ReversalNotAllowedException.class, () -> kernel.post(second));
    }

    private void transactionImmutable() {
        LedgerKernel kernel = kernel();
        LedgerTransaction tx = kernel.post(capture(22, "immutable", 10_000, 300)).transaction();
        expect(UnsupportedOperationException.class, () -> tx.entries().clear());
        check(kernel.findTransaction(tx.id()).checksum().equals(tx.checksum()), "unchanged checksum");
    }

    private void accountLockOrder() {
        List<LedgerAccountId> input = List.of(FEE.id(), PROCESSOR.id(), PAYABLE.id(), PROCESSOR.id());
        List<LedgerAccountId> sorted = AccountLockOrder.sortedDistinct(input);
        check(sorted.size() == 3, "distinct locks");
        check(sorted.equals(sorted.stream().sorted().toList()), "sorted locks");
    }

    private void refundShortfall() {
        LedgerKernel kernel = kernel();
        PostingCommand refund = new RefundPostingPolicy().create(
                context(23, "refund", FinancialEventType.PAYMENT_REFUND_CONFIRMED),
                PAYABLE, RECEIVABLE, FEE, PROCESSOR,
                money(5_000), money(150), money(2_000));
        LedgerTransaction tx = kernel.post(refund).transaction();
        long receivableDebit = tx.entries().stream()
                .filter(e -> e.accountId().equals(RECEIVABLE.id()) && e.direction() == EntryDirection.DEBIT)
                .mapToLong(e -> e.amount().amountMinor()).sum();
        check(receivableDebit == 2_850, "merchant shortfall");
    }

    private void settlementPolicy() {
        LedgerKernel kernel = kernel();
        PostingCommand command = new SettlementPostingPolicy().create(
                context(24, "settle", FinancialEventType.SETTLEMENT_CONFIRMED), PAYABLE, CASH, money(9_700));
        check(kernel.post(command).transaction().entries().size() == 2, "settlement entries");
    }

    private void reserveHoldRelease() {
        LedgerKernel kernel = kernel();
        ReservePostingPolicy policy = new ReservePostingPolicy();
        kernel.post(policy.hold(context(25, "hold", FinancialEventType.RESERVE_HELD), PAYABLE, RESERVE, money(1_000)));
        kernel.post(policy.release(context(26, "release", FinancialEventType.RESERVE_RELEASED), PAYABLE, RESERVE, money(1_000)));
        check(kernel.authoritativeBalance(PAYABLE.id()).isZero(), "payable restored");
        check(kernel.authoritativeBalance(RESERVE.id()).isZero(), "reserve restored");
    }

    private void feeHalfUp() {
        PercentageFeePolicy policy = new PercentageFeePolicy(250, 0);
        check(policy.calculate(money(101)).amountMinor() == 3, "2.5 percent half-up");
        check(new PercentageFeePolicy(0, 50).calculate(money(100)).amountMinor() == 50, "fixed fee");
    }

    private void snapshotDerived() {
        LedgerKernel kernel = kernel();
        kernel.post(capture(27, "snapshot", 10_000, 300));
        var snapshot = kernel.createSnapshot(PAYABLE.id());
        check(snapshot.balance().amountMinor() == 9_700, "snapshot balance");
        check(snapshot.includedTransactionCount() == 1, "snapshot count");
    }

    private void unknownAccountRejected() {
        LedgerKernel kernel = kernel();
        LedgerAccount unknown = account(999, LedgerAccountRole.PROCESSOR_CLEARING, AccountScope.platform(PLATFORM_ID), USD);
        PostingCommand command = PostingCommand.standard(
                context(28, "unknown", FinancialEventType.MANUAL_ADJUSTMENT_APPROVED),
                new PostingPolicyReference("manual-v1", 1),
                List.of(new PostingLine(unknown, EntryDirection.DEBIT, money(100)),
                        new PostingLine(PAYABLE, EntryDirection.CREDIT, money(100))));
        expect(UnknownLedgerAccountException.class, () -> kernel.post(command));
    }

    private void closedAccountRejected() {
        LedgerKernel kernel = kernel();
        LedgerAccount closed = new LedgerAccount(PROCESSOR.id(), PROCESSOR.role(), PROCESSOR.scope(), USD, LedgerAccountStatus.CLOSED);
        // Register a separate closed account ID to preserve immutable account definition.
        closed = new LedgerAccount(new LedgerAccountId(uuid(888)), PROCESSOR.role(), PROCESSOR.scope(), USD, LedgerAccountStatus.CLOSED);
        kernel.registerAccount(closed);
        PostingCommand command = PostingCommand.standard(
                context(29, "closed", FinancialEventType.MANUAL_ADJUSTMENT_APPROVED),
                new PostingPolicyReference("manual-v1", 1),
                List.of(new PostingLine(closed, EntryDirection.DEBIT, money(100)),
                        new PostingLine(PAYABLE, EntryDirection.CREDIT, money(100))));
        expect(LedgerAccountClosedException.class, () -> kernel.post(command));
    }

    private void randomizedCapturesRemainBalanced() {
        Random random = new Random(20260726L);
        for (int i = 0; i < 2_000; i++) {
            long gross = 1L + random.nextLong(10_000_000L);
            long fee = random.nextLong(gross + 1L);
            LedgerKernel kernel = kernel();
            LedgerTransaction transaction = kernel.post(capture(1000 + i, "random-" + i, gross, fee)).transaction();
            check(transaction.debitTotal().equals(transaction.creditTotal()), "random transaction balance");
            long entryDebits = transaction.entries().stream()
                    .filter(entry -> entry.direction() == EntryDirection.DEBIT)
                    .mapToLong(entry -> entry.amount().amountMinor())
                    .sum();
            long entryCredits = transaction.entries().stream()
                    .filter(entry -> entry.direction() == EntryDirection.CREDIT)
                    .mapToLong(entry -> entry.amount().amountMinor())
                    .sum();
            check(entryDebits == entryCredits, "random entry sum");
        }
    }

    private LedgerKernel kernel() {
        LedgerKernel kernel = new LedgerKernel(Clock.fixed(TIME, ZoneOffset.UTC));
        for (LedgerAccount account : List.of(PROCESSOR, CASH, PAYABLE, RECEIVABLE, FEE, RESERVE)) {
            kernel.registerAccount(account);
        }
        return kernel;
    }

    private PostingCommand capture(int suffix, String key, long gross, long fee) {
        return new CapturePostingPolicy().create(
                context(suffix, key, FinancialEventType.PAYMENT_CAPTURE_CONFIRMED),
                PROCESSOR, PAYABLE, FEE, money(gross), money(fee));
    }

    private PostingContext context(int suffix, String key, FinancialEventType type) {
        return new PostingContext(
                new LedgerTransactionId(uuid(10_000 + suffix)),
                new PostingIdempotencyKey(key),
                new SourceReference(type, uuid(20_000 + suffix)),
                new CorrelationId(uuid(30_000 + suffix)),
                MERCHANT,
                TIME,
                TIME,
                "batch-1 self-test");
    }

    private PostingContext replaceSource(PostingContext context, SourceReference source) {
        return new PostingContext(
                context.transactionId(), context.idempotencyKey(), source, context.correlationId(),
                context.merchantId(), context.occurredAt(), context.recordedAt(), context.reason());
    }

    private static Money money(long minor) {
        return new Money(minor, USD);
    }

    private static LedgerAccount account(int suffix, LedgerAccountRole role, AccountScope scope, Currency currency) {
        return new LedgerAccount(new LedgerAccountId(uuid(suffix)), role, scope, currency, LedgerAccountStatus.ACTIVE);
    }

    private static UUID uuid(long suffix) {
        return UUID.fromString(String.format("00000000-0000-7000-8000-%012d", suffix));
    }

    private void test(String name, CheckedRunnable runnable) throws Exception {
        try {
            runnable.run();
            passed++;
            System.out.println("PASS  " + name);
        } catch (Throwable throwable) {
            System.err.println("FAIL  " + name + ": " + throwable);
            throw throwable;
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static <T extends Throwable> void expect(Class<T> type, CheckedRunnable action) {
        try {
            action.run();
        } catch (Throwable throwable) {
            if (type.isInstance(throwable)) return;
            throw new AssertionError("expected " + type.getSimpleName() + " but got " + throwable, throwable);
        }
        throw new AssertionError("expected " + type.getSimpleName() + " but no exception was thrown");
    }

    @FunctionalInterface
    private interface CheckedRunnable {
        void run() throws Exception;
    }
}
