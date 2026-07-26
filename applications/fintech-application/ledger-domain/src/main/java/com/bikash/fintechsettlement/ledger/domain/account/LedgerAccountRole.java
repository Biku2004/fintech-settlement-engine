package com.bikash.fintechsettlement.ledger.domain.account;

public enum LedgerAccountRole {
    PROCESSOR_CLEARING(LedgerAccountType.ASSET, ScopeKind.PLATFORM),
    SETTLEMENT_CASH(LedgerAccountType.ASSET, ScopeKind.PLATFORM),
    MERCHANT_PAYABLE(LedgerAccountType.LIABILITY, ScopeKind.MERCHANT),
    MERCHANT_RECEIVABLE(LedgerAccountType.ASSET, ScopeKind.MERCHANT),
    PLATFORM_FEE_REVENUE(LedgerAccountType.REVENUE, ScopeKind.PLATFORM),
    REFUND_LIABILITY(LedgerAccountType.LIABILITY, ScopeKind.MERCHANT),
    DISPUTE_RESERVE(LedgerAccountType.LIABILITY, ScopeKind.MERCHANT);

    private final LedgerAccountType accountType;
    private final ScopeKind requiredScope;

    LedgerAccountRole(LedgerAccountType accountType, ScopeKind requiredScope) {
        this.accountType = accountType;
        this.requiredScope = requiredScope;
    }

    public LedgerAccountType accountType() {
        return accountType;
    }

    public ScopeKind requiredScope() {
        return requiredScope;
    }
}
