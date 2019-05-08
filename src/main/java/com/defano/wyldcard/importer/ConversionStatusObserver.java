package com.defano.wyldcard.importer;

import com.defano.wyldcard.parts.stack.StackModel;

public interface ConversionStatusObserver {

    /**
     * Invoked to indicate the conversion of a HyperCard stack to a WyldCard {@link StackModel} has failed.
     *
     * @param message A human-readable messaging indicating the error.
     * @param cause   An exception providing greater detail.
     */
    void onConversionFailed(String message, Exception cause);

    /**
     * Invoked to indicate the conversion of a HyperCard stack to a WyldCard {@link StackModel} has succeeded.
     *
     * @param importedStack The converted stack, in WyldCard format.
     */
    void onConversionSucceeded(StackModel importedStack);
}
