package com.defano.wyldcard.part.util;

import javax.swing.*;

/**
 * A WyldCard part that does not explicitly extend a Swing JComponent, but rather holds a reference to the component.
 */
public interface ContainerWrappedPart {
    JComponent getWrappedComponent();
}
