package hypercard.paint.tools.base;

import hypercard.paint.model.PaintToolType;
import hypercard.paint.utils.Geometry;

import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class AbstractLineTool extends AbstractPaintTool {

    private int snapToDegrees = 15;
    private Point initialPoint;

    public AbstractLineTool(PaintToolType type) {
        super(type);
    }

    public abstract void drawLine(Graphics2D g, Stroke stroke, Paint paint, int x1, int y1, int x2, int y2);

    @Override
    public void mousePressed(MouseEvent e, int scaleX, int scaleY) {
        initialPoint = new Point(scaleX, scaleY);
    }

    @Override
    public void mouseDragged(MouseEvent e, int scaleX, int scaleY) {
        getCanvas().clearScratch();

        Point currentLoc = new Point(scaleX, scaleY);

        if (e.isShiftDown()) {
            currentLoc = Geometry.snapLineToNearestAngle(initialPoint, currentLoc, snapToDegrees);
        }

        Graphics2D g2d = (Graphics2D) getCanvas().getScratchImage().getGraphics();
        drawLine(g2d, getStroke(), getStrokePaint(), initialPoint.x, initialPoint.y, currentLoc.x, currentLoc.y);
        g2d.dispose();

        getCanvas().invalidateCanvas();
    }

    @Override
    public void mouseReleased(MouseEvent e, int scaleX, int scaleY) {
        getCanvas().commit();
    }

    public int getSnapToDegrees() {
        return snapToDegrees;
    }

    public void setSnapToDegrees(int snapToDegrees) {
        this.snapToDegrees = snapToDegrees;
    }
}
