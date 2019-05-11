package com.defano.wyldcard.cursor;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.part.stack.StackNavigationObserver;

public interface CursorManager extends StackNavigationObserver {
    void start();

    void setActiveCursor(HyperCardCursor cursor);

    void setActiveCursor(Value cursorName);

    HyperCardCursor getActiveCursor();
}
