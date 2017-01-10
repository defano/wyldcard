package hypercard.paint.tools;

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
    public void mousePressed(MouseEvent e) {
        initialPoint = new Point(e.getX(), e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        getCanvas().clearScratch();

        Point currentLoc = e.getPoint();

        if (e.isShiftDown()) {
            currentLoc = Geometry.snapLineToNearestAngle(initialPoint, currentLoc, snapToDegrees);
        }

        Graphics2D g2d = (Graphics2D) getCanvas().getScratchGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawLine(g2d, getStroke(), getStrokePaint(), initialPoint.x, initialPoint.y, currentLoc.x, currentLoc.y);
        g2d.dispose();

        getCanvas().repaintCanvas();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        getCanvas().commit();
    }

    public int getSnapToDegrees() {
        return snapToDegrees;
    }

    public void setSnapToDegrees(int snapToDegrees) {
        this.snapToDegrees = snapToDegrees;
    }
}
