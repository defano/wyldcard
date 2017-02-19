/*
 * FieldView
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.fields;

import com.defano.hypercard.parts.model.PropertyChangeObserver;

import javax.swing.text.JTextComponent;

public interface FieldView extends PropertyChangeObserver {
    String getText();
    JTextComponent getTextComponent();
    void setEditable(boolean editable);
    void partOpened();
}
