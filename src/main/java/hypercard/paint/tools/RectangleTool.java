package hypercard.paint.tools;

import java.awt.*;

public class RectangleTool extends AbstractShapeTool {

    public RectangleTool() {
        super(ToolType.RECTANGLE);
    }

    @Override
    public void drawShape(Graphics g, int x1, int y1, int x2, int y2) {
        g.drawRect(x1, y1, x2 - x1, y2 - y1);
    }
}
