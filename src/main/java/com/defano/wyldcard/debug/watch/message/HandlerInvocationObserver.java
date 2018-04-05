package com.defano.wyldcard.debug.watch.message;

import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;

public interface HandlerInvocationObserver {
    void onHandlerInvoked(String thread, String message, PartSpecifier recipient, int stackDepth, boolean wasHandled);
}
