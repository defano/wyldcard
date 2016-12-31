package hypercard.paint.tools;

import hypercard.paint.MathUtils;

import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class AbstractLineTool extends AbstractPaintTool {

    private Point initialPoint;

    public AbstractLineTool(PaintToolType type) {
        super(type);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        initialPoint = new Point(e.getX(), e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        getCanvas().clearScratch();

        Point currentLoc = e.getPoint();

        if (e.isShiftDown()) {
            currentLoc = MathUtils.snapLineToNearestAngle(initialPoint, currentLoc, 15);
        }


        Graphics2D g2d = (Graphics2D) getCanvas().getScratchGraphics();
        g2d.setStroke(getStroke());
        g2d.setPaint(getPaint());

        drawLine(g2d, initialPoint.x, initialPoint.y, currentLoc.x, currentLoc.y);

        g2d.dispose();

        getCanvas().repaintCanvas();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        getCanvas().commit();
    }

    public abstract void drawLine(Graphics g, int x1, int y1, int x2, int y2);

}
