package com.defano.wyldcard.awt;

import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.awt.event.KeyListener;

public interface KeyboardManager {

    void start();

    void addGlobalKeyListener(KeyListener observer);

    boolean removeGlobalKeyListener(KeyListener observer);

    Long getBreakTime();

    boolean isShiftDown();

    boolean isAltOptionDown();

    boolean isCtrlCommandDown();

    boolean isPeeking(ExecutionContext context);
}
