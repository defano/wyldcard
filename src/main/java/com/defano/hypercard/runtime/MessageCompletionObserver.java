package com.defano.hypercard.runtime;

import com.defano.hypertalk.exception.HtException;

/**
 * An handler for HyperCard command script execution completion.
 */
public interface MessageCompletionObserver {

    /**
     * Invoked after a command has been sent to a part and its message passing hierarchy.
     * @param command The command that was passed.
     * @param wasTrapped True if the part or another part in the message passing hierarchy trapped the command,
     *                        false otherwise.
     * @param error Non-null if an error occurred while handling the message
     */
    void onMessagePassingCompletion(String command, boolean wasTrapped, HtException error);
}
