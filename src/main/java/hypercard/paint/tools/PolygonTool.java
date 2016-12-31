package hypercard.paint.tools;

import java.awt.*;

public class PolygonTool extends AbstractPolylineTool {

    public PolygonTool() {
        super(PaintToolType.POLYGON);
    }

    @Override
    protected void drawPolyline(Graphics g, int[] xPoints, int[] yPoints) {
        g.drawPolyline(xPoints, yPoints, xPoints.length);
    }

    @Override
    protected void drawPolygon(Graphics g, int[] xPoints, int[] yPoints) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawPolygon(xPoints, yPoints, xPoints.length);

        if (getFill() != null) {
            g2d.setPaint(getFill());
            g2d.fillPolygon(xPoints, yPoints, xPoints.length);
        }
    }
}
