package com.bikash.fintechsettlement.ledger.domain.kernel;

import com.bikash.fintechsettlement.ledger.domain.account.LedgerAccount;
import com.bikash.fintechsettlement.ledger.domain.account.LedgerAccountId;
import com.bikash.fintechsettlement.ledger.domain.account.ScopeKind;
import com.bikash.fintechsettlement.ledger.domain.error.DuplicateLedgerAccountException;
import com.bikash.fintechsettlement.ledger.domain.error.DuplicateLedgerTransactionException;
import com.bikash.fintechsettlement.ledger.domain.error.InvalidLedgerAccountException;
import com.bikash.fintechsettlement.ledger.domain.error.PostingIdempotencyConflictException;
import com.bikash.fintechsettlement.ledger.domain.error.ReversalNotAllowedException;
import com.bikash.fintechsettlement.ledger.domain.error.SourceFinancialEventConflictException;
import com.bikash.fintechsettlement.ledger.domain.error.UnknownLedgerAccountException;
import com.bikash.fintechsettlement.ledger.domain.identity.PlatformId;
import com.bikash.fintechsettlement.ledger.domain.policy.PostingPolicyValidator;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingCanonicalizer;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingCommand;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingFingerprint;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingIdempotencyKey;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingOutcome;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingOutcomeType;
import com.bikash.fintechsettlement.ledger.domain.posting.SourceReference;
import com.bikash.fintechsettlement.ledger.domain.transaction.LedgerTransaction;
import com.bikash.fintechsettlement.ledger.domain.transaction.LedgerTransactionFactory;
import com.bikash.fintechsettlement.ledger.domain.transaction.LedgerTransactionId;
import com.bikash.fintechsettlement.shared.money.Money;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Thread-safe in-memory proof of ledger domain semantics.
 *
 * <p>This is not the production repository. Batch 4 replaces its storage with PostgreSQL/jOOQ while
 * retaining the same domain rules and idempotency outcomes.</p>
 */
public final class LedgerKernel {
    /**
     * Unforgeable construction capability for ledger transactions.
     *
     * <p>The type is public only so the transaction package can name it. Its constructor is private
     * and no instance is exposed, so classpath split-package code cannot manufacture the capability.
     * Reflection or unsafe code already executing inside this JVM is outside the Batch 1 threat model.</p>
     */
    public static final class Access {
        private Access() {}
    }
    private final Map<LedgerAccountId, LedgerAccount> accounts = new LinkedHashMap<>();
    private final Map<LedgerTransactionId, LedgerTransaction> transactions = new LinkedHashMap<>();
    private final Map<PostingIdempotencyKey, Registration> byIdempotency = new LinkedHashMap<>();
    private final Map<SourceReference, Registration> bySource = new LinkedHashMap<>();
    private final Map<LedgerTransactionId, LedgerTransactionId> reversalByOriginal = new LinkedHashMap<>();
    private final PostingPolicyValidator policyValidator = new PostingPolicyValidator();
    private final PostingCanonicalizer canonicalizer = new PostingCanonicalizer();
    private final LedgerTransactionFactory transactionFactory = LedgerTransactionFactory.forKernel(
            new Access(), canonicalizer);
    private final LedgerBalanceCalculator balanceCalculator = new LedgerBalanceCalculator();
    private final PlatformId platformId;
    private final Clock clock;

    public LedgerKernel(PlatformId platformId) {
        this(platformId, Clock.systemUTC());
    }

    public LedgerKernel(PlatformId platformId, Clock clock) {
        this.platformId = Objects.requireNonNull(platformId, "platformId");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public synchronized void registerAccount(LedgerAccount account) {
        Objects.requireNonNull(account, "account");
        if (account.scope().kind() == ScopeKind.PLATFORM
                && !account.scope().ownerId().equals(platformId.value())) {
            throw new InvalidLedgerAccountException(
                    "platform-scoped account does not belong to this ledger kernel");
        }
        if (accounts.putIfAbsent(account.id(), account) != null) {
            throw new DuplicateLedgerAccountException(account.id().toString());
        }
    }

    public synchronized PostingOutcome post(PostingCommand command) {
        Objects.requireNonNull(command, "command");
        var validatedCommand = policyValidator.validate(command);
        PostingFingerprint fingerprint = canonicalizer.fingerprint(command);

        Registration existingKey = byIdempotency.get(command.context().idempotencyKey());
        if (existingKey != null) {
            if (!existingKey.fingerprint().equals(fingerprint)) {
                throw new PostingIdempotencyConflictException(command.context().idempotencyKey().value());
            }
            return new PostingOutcome(
                    PostingOutcomeType.IDEMPOTENT_REPLAY, transactions.get(existingKey.transactionId()));
        }

        Registration existingSource = bySource.get(command.context().source());
        if (existingSource != null) {
            if (!existingSource.fingerprint().equals(fingerprint)) {
                throw new SourceFinancialEventConflictException(command.context().source().toString());
            }
            byIdempotency.put(command.context().idempotencyKey(), existingSource);
            return new PostingOutcome(
                    PostingOutcomeType.SOURCE_REPLAY, transactions.get(existingSource.transactionId()));
        }

        if (transactions.containsKey(command.context().transactionId())) {
            throw new DuplicateLedgerTransactionException(command.context().transactionId().toString());
        }

        validateAccountsRegistered(command);
        validateReversal(command);
        LedgerTransaction transaction = transactionFactory.create(validatedCommand);
        Registration registration = new Registration(fingerprint, transaction.id());
        transactions.put(transaction.id(), transaction);
        byIdempotency.put(command.context().idempotencyKey(), registration);
        bySource.put(command.context().source(), registration);
        if (transaction.reversalOf() != null) {
            reversalByOriginal.put(transaction.reversalOf(), transaction.id());
        }
        return new PostingOutcome(PostingOutcomeType.CREATED, transaction);
    }

    public synchronized LedgerTransaction findTransaction(LedgerTransactionId transactionId) {
        LedgerTransaction transaction = transactions.get(transactionId);
        if (transaction == null) {
            throw new IllegalArgumentException("unknown ledger transaction: " + transactionId);
        }
        return transaction;
    }

    public synchronized LedgerAccount findAccount(LedgerAccountId accountId) {
        LedgerAccount account = accounts.get(accountId);
        if (account == null) {
            throw new UnknownLedgerAccountException(accountId.toString());
        }
        return account;
    }

    public synchronized Map<LedgerAccountId, LedgerAccount> accountMap() {
        return Map.copyOf(accounts);
    }

    public synchronized List<LedgerTransaction> transactions() {
        return List.copyOf(transactions.values());
    }

    public synchronized long transactionCount() {
        return transactions.size();
    }

    public synchronized Money authoritativeBalance(LedgerAccountId accountId) {
        return balanceCalculator.calculate(findAccount(accountId), transactions.values());
    }

    public synchronized BalanceSnapshot createSnapshot(LedgerAccountId accountId) {
        return new BalanceSnapshot(
                accountId, authoritativeBalance(accountId), transactions.size(), clock.instant());
    }

    public List<LedgerAccountId> lockOrder(PostingCommand command) {
        PostingCommand validated = policyValidator.validate(
                Objects.requireNonNull(command, "command")).command();
        Collection<LedgerAccountId> ids = validated.lines().stream()
                .map(line -> line.account().id())
                .toList();
        return AccountLockOrder.sortedDistinct(ids);
    }

    private void validateAccountsRegistered(PostingCommand command) {
        for (var line : command.lines()) {
            LedgerAccount registered = accounts.get(line.account().id());
            if (registered == null) {
                throw new UnknownLedgerAccountException(line.account().id().toString());
            }
            if (!registered.equals(line.account())) {
                throw new UnknownLedgerAccountException(
                        line.account().id() + " does not match the registered immutable account definition");
            }
        }
    }

    private void validateReversal(PostingCommand command) {
        if (!command.isReversal()) {
            return;
        }
        LedgerTransaction original = transactions.get(command.reversalOf());
        if (original == null) {
            throw new ReversalNotAllowedException("original transaction does not exist");
        }
        if (original.isReversal()) {
            throw new ReversalNotAllowedException("a reversal cannot reverse another reversal");
        }
        if (reversalByOriginal.containsKey(original.id())) {
            throw new ReversalNotAllowedException("original transaction is already fully reversed");
        }

        List<String> expected = original.entries().stream()
                .map(entry -> entry.accountId() + "|" + entry.direction().opposite() + "|"
                        + entry.amount().amountMinor() + "|" + entry.amount().currencyCode())
                .sorted()
                .toList();
        List<String> actual = command.lines().stream()
                .map(line -> line.account().id() + "|" + line.direction() + "|"
                        + line.amount().amountMinor() + "|" + line.amount().currencyCode())
                .sorted()
                .toList();
        if (!expected.equals(actual)) {
            throw new ReversalNotAllowedException("full reversal lines are not the exact inverse of the original");
        }
    }

    private record Registration(PostingFingerprint fingerprint, LedgerTransactionId transactionId) {}
}
