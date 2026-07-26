package com.bikash.fintechsettlement.ledger;

import com.bikash.fintechsettlement.ledger.domain.kernel.LedgerKernel;
import com.bikash.fintechsettlement.ledger.domain.policy.CapturePostingPolicy;
import com.bikash.fintechsettlement.ledger.domain.posting.FinancialEventType;
import com.bikash.fintechsettlement.shared.money.Money;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.LongRange;

import static com.bikash.fintechsettlement.ledger.LedgerFixture.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LedgerPropertyTest {
    @Property(tries = 500, seed = "20260726")
    void everyGeneratedCaptureBalances(
            @ForAll @LongRange(min = 1, max = 10_000_000) long gross,
            @ForAll @LongRange(min = 0, max = 10_000) long requestedFee) {
        long fee = Math.min(gross, requestedFee);
        LedgerKernel kernel = new LedgerKernel();
        kernel.registerAccount(PROCESSOR);
        kernel.registerAccount(PAYABLE);
        kernel.registerAccount(FEE);
        long suffix = (gross % 900_000) + 100_000;
        var context = context((int) suffix, "property-" + gross + "-" + fee,
                FinancialEventType.PAYMENT_CAPTURE_CONFIRMED);
        var transaction = kernel.post(new CapturePostingPolicy().create(
                context, PROCESSOR, PAYABLE, FEE, new Money(gross, USD), new Money(fee, USD))).transaction();
        assertEquals(transaction.debitTotal(), transaction.creditTotal());
    }
}
