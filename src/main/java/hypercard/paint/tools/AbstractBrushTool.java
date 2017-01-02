package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;

import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class AbstractBrushTool extends AbstractPaintTool {

    private Point lastPoint;

    public AbstractBrushTool(PaintToolType type) {
        super(type);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        lastPoint = new Point(e.getX(), e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Graphics2D g2d = (Graphics2D) getCanvas().getScratchGraphics();
        drawSegment(g2d, getStroke(), getFillPaint(), lastPoint.x, lastPoint.y, e.getX(), e.getY());
        g2d.dispose();

        lastPoint = new Point(e.getX(), e.getY());
        getCanvas().repaintCanvas();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        getCanvas().commit(getComposite());
    }

    public abstract void drawSegment(Graphics2D g, Stroke stroke, Paint paint, int x1, int y1, int x2, int y2);
}
