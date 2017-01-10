package hypercard.paint.tools;

import hypercard.paint.model.ImmutableProvider;
import hypercard.paint.model.PaintToolType;
import hypercard.paint.utils.Geometry;

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

        Rectangle bounds = e.isShiftDown() ?
                Geometry.squareAtAnchor(initialPoint, e.getPoint()) :
                Geometry.rectangleFromPoints(initialPoint, e.getPoint());

        Graphics2D g2d = (Graphics2D) getCanvas().getScratchGraphics();

        if (getFillPaint() != null) {
            drawFill(g2d, getFillPaint(), bounds.x, bounds.y, bounds.width, bounds.height);
        }

        drawBounds(g2d, getStroke(), getStrokePaint(), bounds.x, bounds.y, bounds.width, bounds.height);

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
