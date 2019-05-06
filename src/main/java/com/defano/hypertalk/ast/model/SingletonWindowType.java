package com.defano.hypertalk.ast.model;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.window.WyldCardFrame;

public enum SingletonWindowType {
    CARD,
    MESSAGE,
    MESSAGE_WATCHER,
    VARIABLE_WATCHER,
    PATTERNS,
    TOOLS;

    public WyldCardFrame getWindow(ExecutionContext context) {
        switch (this) {
            case CARD:
                return WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack());
            case MESSAGE:
                return WyldCard.getInstance().getWindowManager().getMessageWindow();
            case MESSAGE_WATCHER:
                return WyldCard.getInstance().getWindowManager().getMessageWatcher();
            case VARIABLE_WATCHER:
                return WyldCard.getInstance().getWindowManager().getVariableWatcher();
            case PATTERNS:
                return WyldCard.getInstance().getWindowManager().getPatternsPalette();
            case TOOLS:
                return WyldCard.getInstance().getWindowManager().getPaintToolsPalette();
        }

        throw new IllegalArgumentException("Bug! Unimplemented window type.");
    }
}
