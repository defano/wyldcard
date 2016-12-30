package hypercard.paint.tools;

import java.awt.*;

public class RectangleTool extends AbstractShapeTool {

    public RectangleTool() {
        super(ToolType.RECTANGLE);
    }

    @Override
    public void drawShape(Graphics g, int x1, int y1, int width, int height) {
        g.drawRect(x1, y1, width, height);
    }
}
