package com.defano.wyldcard.awt.keyboard;

import java.awt.*;
import java.awt.event.KeyEvent;

public class DeferredKeyEvent extends KeyEvent {

    public DeferredKeyEvent(Component source, int id, int modifiers, int keyCode, char keyChar, int keyLocation) {
        super(source, id, 0, modifiers, keyCode, keyChar, keyLocation);
    }
}
