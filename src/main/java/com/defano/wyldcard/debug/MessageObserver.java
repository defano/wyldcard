package com.defano.wyldcard.debug;

import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;

public interface MessageObserver {
    void onMessageReceived(String message, PartSpecifier target, boolean trapped);
}
