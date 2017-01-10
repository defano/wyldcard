package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

public class PencilTool extends AbstractPathTool {

    private Path2D path;

    public PencilTool() {
        super(PaintToolType.PENCIL);
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
        g.setPaint(Color.BLACK);
        g.draw(path);
    }
}
