package hypercard.paint.tools.base;

import hypercard.paint.model.ImmutableProvider;
import hypercard.paint.model.PaintToolType;
import hypercard.paint.model.Provider;
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

    private Provider<Boolean> drawMultiple = new Provider<>(false);
    private Provider<Boolean> drawCentered = new Provider<>(false);

    protected Point initialPoint;
    protected Point currentPoint;

    public AbstractBoundsTool(PaintToolType type) {
        super(type);
    }

    public abstract void drawBounds(Graphics2D g, Stroke stroke, Paint paint, int x, int y, int width, int height);
    public abstract void drawFill(Graphics2D g, Paint fill, int x, int y, int width, int height);

    @Override
    public void mousePressed(MouseEvent e, int scaleX, int scaleY) {
        initialPoint = new Point(scaleX, scaleY);
    }

    @Override
    public void mouseDragged(MouseEvent e, int scaleX, int scaleY) {
        currentPoint = new Point(scaleX, scaleY);

        if (!drawMultiple.get()) {
            getCanvas().clearScratch();
        }

        Point originPoint = new Point(initialPoint);

        if (drawCentered.get()) {
            int height = currentPoint.y - initialPoint.y;
            int width = currentPoint.x - initialPoint.x;

            originPoint.x = initialPoint.x - width / 2;
            originPoint.y = initialPoint.y - height / 2;
        }

        Rectangle bounds = e.isShiftDown() ?
                Geometry.squareAtAnchor(originPoint, currentPoint) :
                Geometry.rectangleFromPoints(originPoint, currentPoint);

        Graphics2D g2d = (Graphics2D) getCanvas().getScratchImage().getGraphics();

        if (getFillPaint() != null) {
            drawFill(g2d, getFillPaint(), bounds.x, bounds.y, bounds.width, bounds.height);
        }

        drawBounds(g2d, getStroke(), getStrokePaint(), bounds.x, bounds.y, bounds.width, bounds.height);

        g2d.dispose();
        getCanvas().invalidateCanvas();
    }

    @Override
    public void mouseReleased(MouseEvent e, int scaleX, int scaleY) {
        getCanvas().commit();
    }

    public ImmutableProvider<Boolean> getDrawMultiple() {
        return drawMultiple;
    }

    public void setDrawMultiple(Provider<Boolean> drawMultiple) {
        this.drawMultiple = drawMultiple;
    }

    public Provider<Boolean> getDrawCentered() {
        return drawCentered;
    }

    public void setDrawCentered(Provider<Boolean> drawCentered) {
        this.drawCentered = drawCentered;
    }
}
