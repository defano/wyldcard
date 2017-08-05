package com.defano.hypercard.runtime;

/**
 * An handler for HyperCard command script execution completion.
 */
public interface MessageCompletionObserver {

    /**
     * Invoked after a command has been sent to a part and its message passing hierarchy.
     * @param command The command that was passed.
     * @param wasTrapped True if the part or another part in the message passing hierarchy trapped the command,
     *                        false otherwise.
     */
    void onMessagePassingCompletion(String command, boolean wasTrapped);
}
