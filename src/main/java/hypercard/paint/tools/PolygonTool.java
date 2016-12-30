package hypercard.paint.tools;

import java.awt.*;

public class PolygonTool extends AbstractPolylineTool {

    public PolygonTool() {
        super(ToolType.POLYGON);
    }

    @Override
    protected void drawPolyline(Graphics g, int[] xPoints, int[] yPoints) {
        g.drawPolyline(xPoints, yPoints, xPoints.length);
    }

    @Override
    protected void drawPolygon(Graphics g, int[] xPoints, int[] yPoints) {
        g.drawPolygon(xPoints, yPoints, xPoints.length);
    }
}
