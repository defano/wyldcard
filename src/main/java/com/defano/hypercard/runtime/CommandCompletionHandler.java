package com.defano.hypercard.runtime;

import com.defano.hypertalk.ast.common.PassedCommand;

/**
 * An handler for HyperCard command script execution completion.
 */
public interface CommandCompletionHandler {

    /**
     * Invoked after a command has been sent to a part and its message passing hierarchy.
     * @param command The command that was passed.
     * @param trappedInScript True if the part or another part in the message passing hierarchy trapped the command,
     *                        false otherwise.
     */
    void onCommandCompleted(PassedCommand command, boolean trappedInScript);
}
