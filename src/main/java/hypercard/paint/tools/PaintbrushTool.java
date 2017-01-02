package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;

import java.awt.*;
import java.awt.geom.Line2D;

public class PaintbrushTool extends AbstractPathTool {

    private Point lastPoint;

    public PaintbrushTool() {
        super(PaintToolType.PAINTBRUSH);
    }

    @Override
    public void startPath(Graphics2D g, Stroke stroke, Paint paint, Point initialPoint) {
        lastPoint = initialPoint;
    }

    @Override
    public void addPoint(Graphics2D g, Stroke stroke, Paint paint, Point point) {
        g.setStroke(stroke);
        g.setPaint(paint);
        g.draw(new Line2D.Float(lastPoint, point));

        lastPoint = point;
    }
}
