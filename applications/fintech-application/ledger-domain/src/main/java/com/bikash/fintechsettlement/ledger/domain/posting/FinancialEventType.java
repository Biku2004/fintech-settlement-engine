package com.bikash.fintechsettlement.ledger.domain.posting;

public enum FinancialEventType {
    PAYMENT_CAPTURE_CONFIRMED,
    PAYMENT_REFUND_CONFIRMED,
    SETTLEMENT_CONFIRMED,
    RESERVE_HELD,
    RESERVE_RELEASED,
    REVERSAL_CONFIRMED,
    MANUAL_ADJUSTMENT_APPROVED
}
