package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;
import hypercard.paint.tools.base.AbstractPolylineTool;

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
        g.draw(renderCurvePath(xPoints, yPoints));
    }

    @Override
    protected void drawPolygon(Graphics2D g, Stroke stroke, Paint strokePaint, int[] xPoints, int[] yPoints) {
        g.setPaint(strokePaint);
        g.setStroke(stroke);
        g.draw(renderCurvePath(xPoints, yPoints));
    }

    @Override
    protected void fillPolygon(Graphics2D g, Paint fillPaint, int[] xPoints, int[] yPoints) {
        // Not fillable
    }

    private Shape renderCurvePath(int[] xPoints, int[] yPoints) {
        Path2D path = new Path2D.Double();
        path.moveTo(xPoints[0], yPoints[0]);

        int curveIndex;
        for (curveIndex = 0; curveIndex <= xPoints.length - 3; curveIndex += 3) {
            path.curveTo(xPoints[curveIndex], yPoints[curveIndex], xPoints[curveIndex + 1], yPoints[curveIndex + 1], xPoints[curveIndex + 2], yPoints[curveIndex + 2]);
        }

        if (xPoints.length >= 3 && xPoints.length - curveIndex == 2) {
            path.curveTo(xPoints[xPoints.length - 3], yPoints[xPoints.length - 3], xPoints[xPoints.length - 2], yPoints[xPoints.length - 2], xPoints[xPoints.length - 1], yPoints[xPoints.length - 1]);
        } else {
            path.lineTo(xPoints[xPoints.length - 1], yPoints[xPoints.length - 1]);
        }

        return path;
    }
}
