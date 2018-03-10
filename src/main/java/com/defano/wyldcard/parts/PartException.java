package com.defano.wyldcard.parts;

import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

/**
 * Represents an error that occurs when creating, removing or modifying a part.
 */
public class PartException extends HtSemanticException {
    public PartException(String message) {
        super(message);
    }

    public PartException(HtException cause) {
        super(cause);
    }
}
