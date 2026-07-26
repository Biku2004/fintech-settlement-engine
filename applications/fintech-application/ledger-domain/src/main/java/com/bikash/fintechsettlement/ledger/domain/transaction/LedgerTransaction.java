package com.bikash.fintechsettlement.ledger.domain.transaction;

import com.bikash.fintechsettlement.ledger.domain.kernel.LedgerKernel;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingContext;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingPolicyReference;
import com.bikash.fintechsettlement.shared.money.Money;

import java.util.Currency;
import java.util.List;
import java.util.Objects;

public final class LedgerTransaction {
    private final LedgerTransactionId id;
    private final PostingContext context;
    private final PostingPolicyReference policy;
    private final Currency currency;
    private final List<LedgerEntry> entries;
    private final Money debitTotal;
    private final Money creditTotal;
    private final LedgerTransactionId reversalOf;
    private final String checksum;

    private LedgerTransaction(
            LedgerTransactionId id,
            PostingContext context,
            PostingPolicyReference policy,
            Currency currency,
            List<LedgerEntry> entries,
            Money debitTotal,
            Money creditTotal,
            LedgerTransactionId reversalOf,
            String checksum) {
        this.id = Objects.requireNonNull(id, "id");
        this.context = Objects.requireNonNull(context, "context");
        this.policy = Objects.requireNonNull(policy, "policy");
        this.currency = Objects.requireNonNull(currency, "currency");
        this.entries = List.copyOf(entries);
        this.debitTotal = Objects.requireNonNull(debitTotal, "debitTotal");
        this.creditTotal = Objects.requireNonNull(creditTotal, "creditTotal");
        this.reversalOf = reversalOf;
        this.checksum = Objects.requireNonNull(checksum, "checksum");
    }

    static LedgerTransaction createForKernel(
            LedgerKernel.Access access,
            LedgerTransactionId id,
            PostingContext context,
            PostingPolicyReference policy,
            Currency currency,
            List<LedgerEntry> entries,
            Money debitTotal,
            Money creditTotal,
            LedgerTransactionId reversalOf,
            String checksum) {
        Objects.requireNonNull(access, "access");
        return new LedgerTransaction(
                id, context, policy, currency, entries, debitTotal, creditTotal, reversalOf, checksum);
    }

    public LedgerTransactionId id() { return id; }
    public PostingContext context() { return context; }
    public PostingPolicyReference policy() { return policy; }
    public Currency currency() { return currency; }
    public List<LedgerEntry> entries() { return entries; }
    public Money debitTotal() { return debitTotal; }
    public Money creditTotal() { return creditTotal; }
    public LedgerTransactionId reversalOf() { return reversalOf; }
    public String checksum() { return checksum; }
    public boolean isReversal() { return reversalOf != null; }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof LedgerTransaction transaction && id.equals(transaction.id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
