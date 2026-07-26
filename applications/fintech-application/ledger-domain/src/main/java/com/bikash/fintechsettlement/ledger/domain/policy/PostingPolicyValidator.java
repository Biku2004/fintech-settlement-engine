package com.bikash.fintechsettlement.ledger.domain.policy;

import com.bikash.fintechsettlement.ledger.domain.account.LedgerAccountRole;
import com.bikash.fintechsettlement.ledger.domain.error.InvalidPostingPolicyException;
import com.bikash.fintechsettlement.ledger.domain.posting.FinancialEventType;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingCommand;
import com.bikash.fintechsettlement.ledger.domain.posting.PostingPolicyReference;
import com.bikash.fintechsettlement.ledger.domain.transaction.EntryDirection;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Authoritative posting-policy gate used by the ledger kernel.
 *
 * <p>Policy builders provide a convenient and safe construction API, but callers can deserialize or
 * otherwise construct {@link PostingCommand} values directly. The kernel therefore revalidates the
 * policy identity, source event type, reversal mode, and permitted account-role/direction shape
 * before any idempotency registration or ledger mutation.</p>
 */
public final class PostingPolicyValidator {

    public ValidatedPostingCommand validate(PostingCommand command) {
        Objects.requireNonNull(command, "command");
        PostingPolicyReference policy = command.policy();

        if (policy.equals(CapturePostingPolicy.POLICY)) {
            validateCapture(command);
        } else if (policy.equals(RefundPostingPolicy.POLICY)) {
            validateRefund(command);
        } else if (policy.equals(SettlementPostingPolicy.POLICY)) {
            validateSettlement(command);
        } else if (policy.equals(ReservePostingPolicy.HOLD_POLICY)) {
            validateReserveHold(command);
        } else if (policy.equals(ReservePostingPolicy.RELEASE_POLICY)) {
            validateReserveRelease(command);
        } else if (policy.equals(ReversalPostingPolicy.POLICY)) {
            validateReversal(command);
        } else {
            throw new InvalidPostingPolicyException(
                    "unsupported posting policy " + policy.policyId() + " version " + policy.version());
        }
        return new ValidatedPostingCommand(command);
    }

    private static void validateCapture(PostingCommand command) {
        requireStandard(command);
        requireMerchant(command);
        requireSourceType(command, FinancialEventType.PAYMENT_CAPTURE_CONFIRMED);
        requireShape(command,
                rule(LedgerAccountRole.PROCESSOR_CLEARING, EntryDirection.DEBIT, 1, 1),
                rule(LedgerAccountRole.MERCHANT_PAYABLE, EntryDirection.CREDIT, 0, 1),
                rule(LedgerAccountRole.PLATFORM_FEE_REVENUE, EntryDirection.CREDIT, 0, 1));
        requireLineCount(command, 2, 3);
    }

    private static void validateRefund(PostingCommand command) {
        requireStandard(command);
        requireMerchant(command);
        requireSourceType(command, FinancialEventType.PAYMENT_REFUND_CONFIRMED);
        requireShape(command,
                rule(LedgerAccountRole.PROCESSOR_CLEARING, EntryDirection.CREDIT, 1, 1),
                rule(LedgerAccountRole.MERCHANT_PAYABLE, EntryDirection.DEBIT, 0, 1),
                rule(LedgerAccountRole.MERCHANT_RECEIVABLE, EntryDirection.DEBIT, 0, 1),
                rule(LedgerAccountRole.PLATFORM_FEE_REVENUE, EntryDirection.DEBIT, 0, 1));
        requireLineCount(command, 2, 4);
    }

    private static void validateSettlement(PostingCommand command) {
        requireStandard(command);
        requireMerchant(command);
        requireSourceType(command, FinancialEventType.SETTLEMENT_CONFIRMED);
        requireShape(command,
                rule(LedgerAccountRole.MERCHANT_PAYABLE, EntryDirection.DEBIT, 1, 1),
                rule(LedgerAccountRole.SETTLEMENT_CASH, EntryDirection.CREDIT, 1, 1));
        requireLineCount(command, 2, 2);
    }

    private static void validateReserveHold(PostingCommand command) {
        requireStandard(command);
        requireMerchant(command);
        requireSourceType(command, FinancialEventType.RESERVE_HELD);
        requireShape(command,
                rule(LedgerAccountRole.MERCHANT_PAYABLE, EntryDirection.DEBIT, 1, 1),
                rule(LedgerAccountRole.DISPUTE_RESERVE, EntryDirection.CREDIT, 1, 1));
        requireLineCount(command, 2, 2);
    }

    private static void validateReserveRelease(PostingCommand command) {
        requireStandard(command);
        requireMerchant(command);
        requireSourceType(command, FinancialEventType.RESERVE_RELEASED);
        requireShape(command,
                rule(LedgerAccountRole.DISPUTE_RESERVE, EntryDirection.DEBIT, 1, 1),
                rule(LedgerAccountRole.MERCHANT_PAYABLE, EntryDirection.CREDIT, 1, 1));
        requireLineCount(command, 2, 2);
    }

    private static void validateReversal(PostingCommand command) {
        if (!command.isReversal()) {
            throw new InvalidPostingPolicyException("reversal-v1 requires reversalOf");
        }
        FinancialEventType type = command.context().source().type();
        if (type != FinancialEventType.REVERSAL_CONFIRMED
                && type != FinancialEventType.MANUAL_ADJUSTMENT_APPROVED) {
            throw new InvalidPostingPolicyException(
                    "reversal-v1 does not accept source event type " + type);
        }
        // Current posting policies produce at most four entries. Bound forged reversal
        // commands before canonicalization and exact-original comparison to prevent
        // attacker-controlled oversized lists from consuming unbounded CPU/memory.
        requireLineCount(command, 2, 4);
    }

    private static void requireMerchant(PostingCommand command) {
        if (command.context().merchantId() == null) {
            throw new InvalidPostingPolicyException(
                    command.policy().policyId() + " requires merchantId");
        }
    }

    private static void requireStandard(PostingCommand command) {
        if (command.isReversal()) {
            throw new InvalidPostingPolicyException(
                    command.policy().policyId() + " cannot be used as a reversal policy");
        }
    }

    private static void requireSourceType(PostingCommand command, FinancialEventType expected) {
        FinancialEventType actual = command.context().source().type();
        if (actual != expected) {
            throw new InvalidPostingPolicyException(
                    command.policy().policyId() + " requires source event type " + expected
                            + " but received " + actual);
        }
    }

    private static void requireLineCount(PostingCommand command, int minimum, int maximum) {
        int count = command.lines().size();
        if (count < minimum || count > maximum) {
            throw new InvalidPostingPolicyException(
                    command.policy().policyId() + " requires " + minimum
                            + (minimum == maximum ? "" : "-" + maximum) + " entries");
        }
    }

    private static void requireShape(PostingCommand command, LineRule... rules) {
        Map<RoleDirection, Integer> counts = new HashMap<>();
        Map<RoleDirection, LineRule> allowed = new HashMap<>();
        for (LineRule rule : rules) {
            allowed.put(rule.key(), rule);
        }

        for (var line : command.lines()) {
            RoleDirection key = new RoleDirection(line.account().role(), line.direction());
            if (!allowed.containsKey(key)) {
                throw new InvalidPostingPolicyException(
                        command.policy().policyId() + " does not permit "
                                + key.role() + " " + key.direction());
            }
            counts.merge(key, 1, Integer::sum);
        }

        for (LineRule rule : rules) {
            int count = counts.getOrDefault(rule.key(), 0);
            if (count < rule.minimum() || count > rule.maximum()) {
                throw new InvalidPostingPolicyException(
                        command.policy().policyId() + " requires " + describe(rule));
            }
        }
    }

    private static String describe(LineRule rule) {
        String amount = rule.minimum() == rule.maximum()
                ? Integer.toString(rule.minimum())
                : rule.minimum() + "-" + rule.maximum();
        return amount + " " + rule.role() + " " + rule.direction() + " entries";
    }

    private static LineRule rule(
            LedgerAccountRole role, EntryDirection direction, int minimum, int maximum) {
        return new LineRule(role, direction, minimum, maximum);
    }

    private record RoleDirection(LedgerAccountRole role, EntryDirection direction) {}

    private record LineRule(
            LedgerAccountRole role,
            EntryDirection direction,
            int minimum,
            int maximum) {
        private LineRule {
            Objects.requireNonNull(role, "role");
            Objects.requireNonNull(direction, "direction");
            if (minimum < 0 || maximum < minimum) {
                throw new IllegalArgumentException("invalid posting policy line cardinality");
            }
        }

        private RoleDirection key() {
            return new RoleDirection(role, direction);
        }
    }
}
