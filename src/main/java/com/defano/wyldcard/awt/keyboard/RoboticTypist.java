package com.defano.wyldcard.awt.keyboard;

import com.defano.hypertalk.ast.model.enums.ArrowDirection;
import com.defano.hypertalk.exception.HtException;

import java.awt.*;

public interface RoboticTypist {

    void typeEnter(Component c);
    void type(ArrowDirection arrowKey) throws HtException;
    void type(String string, ModifierKey... modifierKeys) throws HtException;
}
