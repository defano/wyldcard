package hypercard.paint.tools;

import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class AbstractShapeTool extends AbstractPaintTool {

    private Point initialPoint;

    public AbstractShapeTool(ToolType type) {
        super(type);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        initialPoint = new Point(e.getX(), e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        getCanvas().clearScratch();

        Graphics2D g2d = (Graphics2D) getCanvas().getScratchGraphics();
        g2d.setStroke(getStroke());
        g2d.setPaint(getPaint());

        int x1 = Math.min(initialPoint.x, e.getX());
        int y1 = Math.min(initialPoint.y, e.getY());
        int x2 = Math.max(initialPoint.x, e.getX());
        int y2 = Math.max(initialPoint.y, e.getY());

        drawShape(g2d, x1, y1, x2, y2);
        g2d.dispose();

        getCanvas().repaintCanvas();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        getCanvas().commit();
    }

    public abstract void drawShape(Graphics g, int x1, int y1, int x2, int y2);

}
