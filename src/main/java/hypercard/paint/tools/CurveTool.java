package hypercard.paint.tools;

import java.awt.*;
import java.awt.geom.Path2D;

public class CurveTool extends AbstractPolylineTool {

    public CurveTool() {
        super(PaintToolType.CURVE);
    }

    @Override
    protected void drawPolyline(Graphics2D g, Stroke stroke, Paint paint, int[] xPoints, int[] yPoints) {
        g.setPaint(paint);
        g.setStroke(stroke);
        g.draw(xPoints.length % 3 == 0 ? renderFullCurve(xPoints, yPoints) : renderPartialCurve(xPoints, yPoints));
    }

    @Override
    protected void drawPolygon(Graphics2D g, Stroke stroke, Paint strokePaint, int[] xPoints, int[] yPoints) {
        g.setPaint(strokePaint);
        g.setStroke(stroke);
        g.draw(renderFullCurve(xPoints, yPoints));
    }

    @Override
    protected void fillPolygon(Graphics2D g, Paint fillPaint, int[] xPoints, int[] yPoints) {
        // Not fillable
    }

    private Shape renderFullCurve(int[] xPoints, int[] yPoints) {
        Path2D path = new Path2D.Double();
        path.moveTo(xPoints[0], yPoints[0]);

        for (int curveIndex = 0; curveIndex <= xPoints.length - 3; curveIndex += 3) {
            path.curveTo(xPoints[curveIndex], yPoints[curveIndex], xPoints[curveIndex + 1], yPoints[curveIndex + 1], xPoints[curveIndex + 2], yPoints[curveIndex + 2]);
        }

        return path;
    }

    private Shape renderPartialCurve(int[] xPoints, int[] yPoints) {
        int curveIndex;
        Path2D path = new Path2D.Double();
        path.moveTo(xPoints[0], yPoints[0]);

        for (curveIndex = 0; curveIndex <= xPoints.length - 3; curveIndex += 3) {
            path.curveTo(xPoints[curveIndex], yPoints[curveIndex], xPoints[curveIndex + 1], yPoints[curveIndex + 1], xPoints[curveIndex + 2], yPoints[curveIndex + 2]);
        }

        for (int lineIndex = curveIndex; lineIndex < xPoints.length; lineIndex++) {
            path.lineTo(xPoints[lineIndex], yPoints[lineIndex]);
        }

        return path;
    }
}
