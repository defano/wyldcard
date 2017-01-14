package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;
import hypercard.paint.tools.base.AbstractPaintTool;

import java.awt.*;

public class ArrowTool extends AbstractPaintTool {

    public ArrowTool() {
        super(PaintToolType.ARROW);
        setToolCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
}
