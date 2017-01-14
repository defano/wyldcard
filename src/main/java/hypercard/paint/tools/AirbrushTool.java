package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;
import hypercard.paint.tools.base.AbstractPathTool;

import java.awt.*;
import java.awt.geom.Line2D;

public class AirbrushTool extends AbstractPathTool {

    private Point lastPoint;

    public AirbrushTool() {
        super(PaintToolType.AIRBRUSH);
    }

    @Override
    public void startPath(Graphics2D g, Stroke stroke, Paint paint, Point initialPoint) {
        lastPoint = initialPoint;
    }

    @Override
    public void addPoint(Graphics2D g, Stroke stroke, Paint paint, Point point) {
        g.setStroke(stroke);
        g.setPaint(paint);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
        g.draw(new Line2D.Float(lastPoint, point));

        lastPoint = point;
    }
}
