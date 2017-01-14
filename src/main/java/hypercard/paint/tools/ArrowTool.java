package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;
import hypercard.paint.tools.base.PaintTool;

import java.awt.*;

public class ArrowTool extends PaintTool {

    public ArrowTool() {
        super(PaintToolType.ARROW);
        setToolCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
}
