package hypercard.paint.tools;

import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class AbstractShapeTool extends AbstractPaintTool {

    protected Point initialPoint;
    protected Point currentPoint;

    public AbstractShapeTool(PaintToolType type) {
        super(type);
    }

    public abstract void drawBounds(Graphics2D g, Stroke stroke, Paint paint, int x, int y, int width, int height);
    public abstract void drawFill(Graphics2D g, Paint fill, int x, int y, int width, int height);

    @Override
    public void mousePressed(MouseEvent e) {
        initialPoint = new Point(e.getX(), e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        currentPoint = e.getPoint();
        getCanvas().clearScratch();

        Graphics2D g2d = (Graphics2D) getCanvas().getScratchGraphics();

        int left = Math.min(initialPoint.x, currentPoint.x);
        int top = Math.min(initialPoint.y, currentPoint.y);
        int right = Math.max(initialPoint.x, currentPoint.x);
        int bottom = Math.max(initialPoint.y, currentPoint.y);

        int width = (right - left);
        int height = (bottom - top);

        if (e.isShiftDown()) {
            width = height = Math.max(width, height);
        }

        drawBounds(g2d, getStroke(), getStrokePaint(), left, top, width, height);

        if (getFillPaint() != null) {
            drawFill(g2d, getFillPaint(), left, top, width, height);
        }

        g2d.dispose();

        getCanvas().repaintCanvas();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        getCanvas().commit();
    }
}
