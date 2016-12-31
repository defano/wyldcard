package hypercard.paint.tools;

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
        g2d.setStroke(getStroke());
        g2d.setPaint(getPaint());
        drawSegment(g2d, lastPoint.x, lastPoint.y, e.getX(), e.getY());
        g2d.dispose();

        lastPoint = new Point(e.getX(), e.getY());
        getCanvas().repaintCanvas();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        getCanvas().commit(getComposite());
    }

    public abstract void drawSegment(Graphics g, int x1, int y1, int x2, int y2);
}
