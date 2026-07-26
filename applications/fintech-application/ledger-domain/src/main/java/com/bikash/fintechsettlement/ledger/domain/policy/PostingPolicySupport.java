package com.bikash.fintechsettlement.ledger.domain.policy;

import com.bikash.fintechsettlement.ledger.domain.account.LedgerAccount;
import com.bikash.fintechsettlement.ledger.domain.account.LedgerAccountRole;
import com.bikash.fintechsettlement.ledger.domain.error.InvalidPostingPolicyException;
import com.bikash.fintechsettlement.ledger.domain.identity.MerchantId;
import com.bikash.fintechsettlement.ledger.domain.posting.FinancialEventType;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingContext;
import com.bikash.fintechsettlement.shared.money.Money;

import java.util.Currency;

final class PostingPolicySupport {
    private PostingPolicySupport() {}

    static void requireSourceType(PostingContext context, FinancialEventType expected) {
        FinancialEventType actual = context.source().type();
        if (actual != expected) {
            throw new InvalidPostingPolicyException(
                    "expected source event type " + expected + " but received " + actual);
        }
    }

    static void requireRole(LedgerAccount account, LedgerAccountRole expected) {
        if (account.role() != expected) {
            throw new InvalidPostingPolicyException(
                    "expected account role " + expected + " but received " + account.role());
        }
    }

    static void requireMerchantOwner(LedgerAccount account, MerchantId merchantId) {
        if (!account.scope().belongsTo(merchantId)) {
            throw new InvalidPostingPolicyException(
                    account.role() + " account does not belong to merchant " + merchantId);
        }
    }

    static void requireCurrency(Currency expected, LedgerAccount... accounts) {
        for (LedgerAccount account : accounts) {
            if (!expected.equals(account.currency())) {
                throw new InvalidPostingPolicyException(
                        "account " + account.id() + " currency does not match " + expected.getCurrencyCode());
            }
        }
    }

    static void requireCurrency(Currency expected, Money... amounts) {
        for (Money amount : amounts) {
            if (!expected.equals(amount.currency())) {
                throw new InvalidPostingPolicyException(
                        "money currency does not match " + expected.getCurrencyCode());
            }
        }
    }
}
