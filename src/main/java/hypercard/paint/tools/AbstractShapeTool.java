package hypercard.paint.tools;

import hypercard.gui.util.ModifierKeyListener;

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

        int left = Math.min(initialPoint.x, e.getX());
        int top = Math.min(initialPoint.y, e.getY());
        int right = Math.max(initialPoint.x, e.getX());
        int bottom = Math.max(initialPoint.y, e.getY());

        int width = (right - left);
        int height = (bottom - top);

        if (e.isShiftDown()) {
            width = height = Math.max(width, height);
        }

        drawShape(g2d, left, top, width, height);
        g2d.dispose();

        getCanvas().repaintCanvas();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        getCanvas().commit();
    }

    public abstract void drawShape(Graphics g, int x1, int y1, int width, int height);

}
