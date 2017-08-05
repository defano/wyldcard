/*
 * OpaqueField
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.field.styles;

import com.defano.hypercard.parts.ToolEditablePart;

import javax.swing.*;

public class OpaqueField extends AbstractTextField {

    public OpaqueField(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(BorderFactory.createEmptyBorder());
    }
}
