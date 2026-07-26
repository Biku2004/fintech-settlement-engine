package com.bikash.fintechsettlement.ledger.domain.transaction;

import com.bikash.fintechsettlement.ledger.domain.account.ScopeKind;
import com.bikash.fintechsettlement.ledger.domain.error.InvalidLedgerEntryException;
import com.bikash.fintechsettlement.ledger.domain.error.LedgerAccountClosedException;
import com.bikash.fintechsettlement.ledger.domain.error.LedgerArithmeticException;
import com.bikash.fintechsettlement.ledger.domain.error.ReversalNotAllowedException;
import com.bikash.fintechsettlement.ledger.domain.error.UnbalancedTransactionException;
import com.bikash.fintechsettlement.ledger.domain.kernel.LedgerKernel;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingCanonicalizer;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingCommand;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingLine;
import com.bikash.fintechsettlement.ledger.domain.policy.ValidatedPostingCommand;
import com.bikash.fintechsettlement.shared.money.Money;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class LedgerTransactionFactory {
    private static final Comparator<PostingLine> ENTRY_ORDER = Comparator
            .comparing((PostingLine line) -> line.account().id())
            .thenComparing(line -> line.direction().name())
            .thenComparingLong(line -> line.amount().amountMinor())
            .thenComparing(line -> line.account().role().name());

    private final LedgerKernel.Access access;
    private final PostingCanonicalizer canonicalizer;

    private LedgerTransactionFactory(
            LedgerKernel.Access access, PostingCanonicalizer canonicalizer) {
        this.access = Objects.requireNonNull(access, "access");
        this.canonicalizer = Objects.requireNonNull(canonicalizer, "canonicalizer");
    }

    /**
     * Creates the factory only for the ledger kernel. The access capability cannot be
     * constructed outside `LedgerKernel`, including by split-package code.
     */
    public static LedgerTransactionFactory forKernel(
            LedgerKernel.Access access, PostingCanonicalizer canonicalizer) {
        return new LedgerTransactionFactory(access, canonicalizer);
    }

    public LedgerTransaction create(ValidatedPostingCommand validatedCommand) {
        PostingCommand command = Objects.requireNonNull(validatedCommand, "validatedCommand").command();
        List<PostingLine> sorted = command.lines().stream().sorted(ENTRY_ORDER).toList();
        if (sorted.size() < 2) {
            throw new InvalidLedgerEntryException("a transaction requires at least two entries");
        }
        if (command.reversalOf() != null && command.reversalOf().equals(command.context().transactionId())) {
            throw new ReversalNotAllowedException("a transaction cannot reverse itself");
        }

        Currency currency = sorted.getFirst().amount().currency();
        long debits = 0L;
        long credits = 0L;
        boolean hasDebit = false;
        boolean hasCredit = false;
        List<LedgerEntry> entries = new ArrayList<>(sorted.size());
        Set<UUID> platformOwners = new HashSet<>();
        var merchantId = command.context().merchantId();

        try {
            for (int index = 0; index < sorted.size(); index++) {
                PostingLine line = sorted.get(index);
                if (!line.account().isActive()) {
                    throw new LedgerAccountClosedException(line.account().id().toString());
                }
                if (!currency.equals(line.amount().currency())) {
                    throw new InvalidLedgerEntryException("all entries must use the same currency");
                }
                if (line.account().scope().kind() == ScopeKind.PLATFORM) {
                    platformOwners.add(line.account().scope().ownerId());
                } else if (line.account().scope().kind() == ScopeKind.MERCHANT) {
                    if (merchantId == null || !line.account().scope().belongsTo(merchantId)) {
                        throw new InvalidLedgerEntryException(
                                "merchant-scoped account does not belong to posting merchant");
                    }
                }
                if (line.direction() == EntryDirection.DEBIT) {
                    hasDebit = true;
                    debits = Math.addExact(debits, line.amount().amountMinor());
                } else {
                    hasCredit = true;
                    credits = Math.addExact(credits, line.amount().amountMinor());
                }
                entries.add(new LedgerEntry(
                        index + 1,
                        line.account().id(),
                        line.account().role(),
                        line.account().scope(),
                        line.direction(),
                        line.amount()));
            }
        } catch (ArithmeticException exception) {
            throw new LedgerArithmeticException("summing ledger entries", exception);
        }

        if (platformOwners.size() > 1) {
            throw new InvalidLedgerEntryException(
                    "platform-scoped accounts must share one platform owner");
        }

        if (!hasDebit || !hasCredit) {
            throw new InvalidLedgerEntryException("a transaction requires at least one debit and one credit");
        }
        if (debits != credits) {
            throw new UnbalancedTransactionException("debits=" + debits + ", credits=" + credits);
        }

        Money debitTotal = new Money(debits, currency);
        Money creditTotal = new Money(credits, currency);
        String checksumInput = command.context().transactionId() + "|"
                + command.context().recordedAt() + "|" + canonicalizer.canonicalForm(command);
        String checksum = PostingCanonicalizer.sha256Hex(checksumInput);

        return LedgerTransaction.createForKernel(
                access,
                command.context().transactionId(),
                command.context(),
                command.policy(),
                currency,
                entries,
                debitTotal,
                creditTotal,
                command.reversalOf(),
                checksum);
    }
}
