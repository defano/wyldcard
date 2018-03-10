package com.defano.wyldcard.parts.field.styles;

import com.defano.wyldcard.parts.ToolEditablePart;

import javax.swing.*;

public class OpaqueField extends HyperCardTextField {

    public OpaqueField(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(BorderFactory.createEmptyBorder());
    }
}
