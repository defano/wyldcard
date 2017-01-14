package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;
import hypercard.paint.tools.base.AbstractPathTool;

import java.awt.*;
import java.awt.geom.Path2D;

public class PaintbrushTool extends AbstractPathTool {

    private Path2D path;

    public PaintbrushTool() {
        super(PaintToolType.PAINTBRUSH);
    }

    @Override
    public void startPath(Graphics2D g, Stroke stroke, Paint paint, Point initialPoint) {
        path = new Path2D.Double();
        path.moveTo(initialPoint.getX(), initialPoint.getY());
    }

    @Override
    public void addPoint(Graphics2D g, Stroke stroke, Paint paint, Point point) {
        path.lineTo(point.getX(), point.getY());

        g.setStroke(stroke);
        g.setPaint(paint);
        g.draw(path);
    }
}
