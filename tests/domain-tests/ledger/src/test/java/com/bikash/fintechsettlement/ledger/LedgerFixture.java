package com.bikash.fintechsettlement.ledger;

import com.bikash.fintechsettlement.ledger.domain.account.*;
import com.bikash.fintechsettlement.ledger.domain.identity.CorrelationId;
import com.bikash.fintechsettlement.ledger.domain.identity.MerchantId;
import com.bikash.fintechsettlement.ledger.domain.identity.PlatformId;
import com.bikash.fintechsettlement.ledger.domain.posting.*;
import com.bikash.fintechsettlement.ledger.domain.transaction.LedgerTransactionId;

import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

final class LedgerFixture {
    static final Currency USD = Currency.getInstance("USD");
    static final UUID PLATFORM = UUID.fromString("00000000-0000-7000-8000-000000000001");
    static final PlatformId PLATFORM_ID = new PlatformId(PLATFORM);
    static final MerchantId MERCHANT = new MerchantId(UUID.fromString("00000000-0000-7000-8000-000000000002"));
    static final Instant TIME = Instant.parse("2026-07-26T00:00:00Z");

    static final LedgerAccount PROCESSOR = account("00000000-0000-7000-8000-000000000101", LedgerAccountRole.PROCESSOR_CLEARING, AccountScope.platform(PLATFORM));
    static final LedgerAccount CASH = account("00000000-0000-7000-8000-000000000102", LedgerAccountRole.SETTLEMENT_CASH, AccountScope.platform(PLATFORM));
    static final LedgerAccount PAYABLE = account("00000000-0000-7000-8000-000000000103", LedgerAccountRole.MERCHANT_PAYABLE, AccountScope.merchant(MERCHANT));
    static final LedgerAccount RECEIVABLE = account("00000000-0000-7000-8000-000000000104", LedgerAccountRole.MERCHANT_RECEIVABLE, AccountScope.merchant(MERCHANT));
    static final LedgerAccount FEE = account("00000000-0000-7000-8000-000000000105", LedgerAccountRole.PLATFORM_FEE_REVENUE, AccountScope.platform(PLATFORM));
    static final LedgerAccount RESERVE = account("00000000-0000-7000-8000-000000000106", LedgerAccountRole.DISPUTE_RESERVE, AccountScope.merchant(MERCHANT));

    static LedgerAccount account(String id, LedgerAccountRole role, AccountScope scope) {
        return new LedgerAccount(new LedgerAccountId(UUID.fromString(id)), role, scope, USD, LedgerAccountStatus.ACTIVE);
    }

    static PostingContext context(int suffix, String key, FinancialEventType type) {
        return new PostingContext(
                new LedgerTransactionId(UUID.fromString(String.format("00000000-0000-7000-8000-%012d", suffix))),
                new PostingIdempotencyKey(key),
                new SourceReference(type, UUID.fromString(String.format("10000000-0000-7000-8000-%012d", suffix))),
                new CorrelationId(UUID.fromString(String.format("20000000-0000-7000-8000-%012d", suffix))),
                MERCHANT,
                TIME,
                TIME,
                "test posting");
    }
}
