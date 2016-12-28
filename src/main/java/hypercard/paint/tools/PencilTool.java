package hypercard.paint.tools;

import java.awt.*;

public class PencilTool extends AbstractBrushTool {

    public PencilTool() {
        super(ToolType.PENCIL);
    }

    @Override
    public void drawSegment(Graphics g, int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y2);
    }
}
