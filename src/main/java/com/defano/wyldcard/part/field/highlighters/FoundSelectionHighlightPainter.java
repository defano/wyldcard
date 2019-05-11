package com.defano.wyldcard.part.field.highlighters;

import javax.swing.text.*;
import java.awt.*;

public class FoundSelectionHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {

    public FoundSelectionHighlightPainter() {
        super(new Color(0xff, 0x00, 0x00, 0x40));
    }
}
