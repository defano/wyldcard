package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;
import hypercard.paint.tools.base.AbstractPathTool;

import java.awt.*;
import java.awt.geom.Path2D;

public class EraserTool extends AbstractPathTool {

    private Path2D path;

    public EraserTool() {
        super(PaintToolType.ERASER);

        // Eraser is basically a paintbrush whose stroke "clears" the pixels underneath it via the DST_OUT composite mode
        setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1.0f));
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
        g.setPaint(Color.WHITE);
        g.draw(path);
    }
}
