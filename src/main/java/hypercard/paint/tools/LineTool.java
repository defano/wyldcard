package hypercard.paint.tools;

import java.awt.*;

public class LineTool extends AbstractLineTool {

    public LineTool() {
        super(ToolType.LINE);
    }

    @Override
    public void drawLine(Graphics g, int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y2);
    }
}
