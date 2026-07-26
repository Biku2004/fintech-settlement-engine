package com.bikash.fintechsettlement.ledger.domain.policy;

import com.bikash.fintechsettlement.ledger.domain.posting.PostingCommand;

import java.util.Objects;

/**
 * Opaque proof that a posting command passed the authoritative policy gate.
 *
 * <p>The constructor is package-private so only policy validation code can mint this proof.</p>
 */
public final class ValidatedPostingCommand {
    private final PostingCommand command;

    ValidatedPostingCommand(PostingCommand command) {
        this.command = Objects.requireNonNull(command, "command");
    }

    public PostingCommand command() {
        return command;
    }
}
