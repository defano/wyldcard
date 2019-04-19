package com.defano.wyldcard.importer;

import com.defano.wyldcard.parts.stack.StackModel;

public interface ConversionStatusObserver {

    void onConversionFailed(String message, Exception cause);

    void onConversionSucceeded(StackModel importedStack);
}
