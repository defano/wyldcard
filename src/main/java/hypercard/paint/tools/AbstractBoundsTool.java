package hypercard.paint.tools;

import hypercard.paint.model.ImmutableProvider;
import hypercard.paint.model.PaintToolType;
import hypercard.paint.model.Provider;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Mouse and keyboard event handler for tools that define a bounding box by clicking and dragging
 * the mouse from the top-left point of the bounds to the bottom-right point.
 *
 * When the shift key is held down the bounding box is constrained to a square whose height and width is equal to the
 * larger of the two dimensions defined by the mouse location.
 *
 */
public abstract class AbstractBoundsTool extends AbstractPaintTool {

    private ImmutableProvider<Boolean> drawMultiple = new ImmutableProvider<>();

    protected Point initialPoint;
    protected Point currentPoint;

    public AbstractBoundsTool(PaintToolType type) {
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

        if (!drawMultiple.get()) {
            getCanvas().clearScratch();
        }

        int left = Math.min(initialPoint.x, currentPoint.x);
        int top = Math.min(initialPoint.y, currentPoint.y);
        int right = Math.max(initialPoint.x, currentPoint.x);
        int bottom = Math.max(initialPoint.y, currentPoint.y);

        int width = (right - left);
        int height = (bottom - top);

        if (e.isShiftDown()) {
            width = height = Math.max(width, height);
        }

        Graphics2D g2d = (Graphics2D) getCanvas().getScratchGraphics();
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

    public ImmutableProvider<Boolean> getDrawMultiple() {
        return drawMultiple;
    }

    public void setDrawMultiple(ImmutableProvider<Boolean> drawMultiple) {
        this.drawMultiple = drawMultiple;
    }
}
