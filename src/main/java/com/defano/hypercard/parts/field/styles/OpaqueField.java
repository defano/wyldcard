package com.defano.hypercard.parts.field.styles;

import com.defano.hypercard.parts.ToolEditablePart;

import javax.swing.*;

public class OpaqueField extends HyperCardTextField {

    public OpaqueField(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(BorderFactory.createEmptyBorder());
    }
}
