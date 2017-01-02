package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;

import java.awt.*;

public class ArrowTool extends AbstractPaintTool {

    public ArrowTool() {
        super(PaintToolType.ARROW);
        setToolCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
}
