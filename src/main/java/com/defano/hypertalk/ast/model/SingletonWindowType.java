package com.defano.hypertalk.ast.model;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.WyldCardFrame;
import com.defano.wyldcard.window.WindowManager;

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
                return WindowManager.getInstance().getWindowForStack(context, context.getCurrentStack());
            case MESSAGE:
                return WindowManager.getInstance().getMessageWindow();
            case MESSAGE_WATCHER:
                return WindowManager.getInstance().getMessageWatcher();
            case VARIABLE_WATCHER:
                return WindowManager.getInstance().getVariableWatcher();
            case PATTERNS:
                return WindowManager.getInstance().getPatternsPalette();
            case TOOLS:
                return WindowManager.getInstance().getPaintToolsPalette();
        }

        throw new IllegalArgumentException("Bug! Unimplemented window type.");
    }
}
