package hypercard.paint.tools;

import java.awt.*;

public class RoundRectangleTool extends AbstractShapeTool {

    public RoundRectangleTool() {
        super(ToolType.ROUND_RECTANGLE);
    }

    @Override
    public void drawShape(Graphics g, int x1, int y1, int width, int height) {
        g.drawRoundRect(x1, y1, width, height, 10, 10);

    }
}
