package com.defano.wyldcard.parts.editor;

import java.awt.*;
import java.awt.event.AWTEventListener;

public interface PartEditManager extends AWTEventListener, KeyEventDispatcher {
    void start();
}
