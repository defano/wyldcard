package hypercard.paint.tools;

import java.awt.*;

public class RoundRectangleTool extends AbstractShapeTool {

    public RoundRectangleTool() {
        super(ToolType.ROUND_RECTANGLE);
    }

    @Override
    public void drawShape(Graphics g, int x1, int y1, int x2, int y2) {
        g.drawRoundRect(x1, y1, x2 - x1, y2 - y1, 10, 10);

    }
}
