package hypercard.paint.tools;

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
    public void mousePressed(MouseEvent e) {
        Graphics2D g2d = (Graphics2D) getCanvas().getScratchGraphics();
        startPath(g2d, getStroke(), getStrokePaint(), e.getPoint());
        g2d.dispose();

        getCanvas().repaintCanvas();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Graphics2D g2d = (Graphics2D) getCanvas().getScratchGraphics();
        addPoint(g2d, getStroke(), getFillPaint(), e.getPoint());
        g2d.dispose();

        getCanvas().repaintCanvas();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        getCanvas().commit(getComposite());
    }
}
