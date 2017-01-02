package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;

import java.awt.*;
import java.awt.geom.Line2D;

public class EraserTool extends AbstractPathTool {

    private Point lastPoint;

    public EraserTool() {
        super(PaintToolType.ERASER);

        // Eraser is basically a paintbrush whose stroke "clears" the pixels underneath it via the DST_OUT composite mode
        setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1.0f));
    }

    @Override
    public void startPath(Graphics2D g, Stroke stroke, Paint paint, Point initialPoint) {
        lastPoint = initialPoint;
    }

    @Override
    public void addPoint(Graphics2D g, Stroke stroke, Paint paint, Point point) {
        g.setStroke(stroke);
        g.setColor(Color.WHITE);
        g.draw(new Line2D.Float(lastPoint, point));

        lastPoint = point;
    }
}
