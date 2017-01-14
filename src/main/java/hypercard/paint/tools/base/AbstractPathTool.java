package hypercard.paint.tools.base;

import hypercard.paint.model.PaintToolType;

import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class AbstractPathTool extends AbstractPaintTool {

    public abstract void startPath(Graphics2D g, Stroke stroke, Paint paint, Point initialPoint);
    public abstract void addPoint(Graphics2D g, Stroke stroke, Paint paint, Point point);

    public AbstractPathTool(PaintToolType type) {
        super(type);
    }

    @Override
    public void mousePressed(MouseEvent e, int scaleX, int scaleY) {
        Graphics2D g2d = (Graphics2D) getCanvas().getScratchGraphics();
        startPath(g2d, getStroke(), getStrokePaint(), new Point(scaleX, scaleY));
        g2d.dispose();

        getCanvas().repaintCanvas();
    }

    @Override
    public void mouseDragged(MouseEvent e, int scaleX, int scaleY) {
        Graphics2D g2d = (Graphics2D) getCanvas().getScratchGraphics();
        addPoint(g2d, getStroke(), getFillPaint(), new Point(scaleX, scaleY));
        g2d.dispose();

        getCanvas().repaintCanvas();
    }

    @Override
    public void mouseReleased(MouseEvent e, int scaleX, int scaleY) {
        getCanvas().commit(getComposite());
    }
}
