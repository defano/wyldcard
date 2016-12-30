package hypercard.paint.tools;

import hypercard.gui.util.ModifierKeyListener;
import hypercard.paint.MathUtils;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public abstract class AbstractPolylineTool extends AbstractPaintTool {

    private List<Point> points = new ArrayList<>();
    private Point currentPoint = null;

    public AbstractPolylineTool(ToolType type) {
        super(type);
    }

    @Override
    public void mouseMoved(MouseEvent e) {

        // Nothing to do if initial point is not yet established
        if (points.size() == 0) {
            return;
        }

        if (e.isShiftDown()) {
            Point lastPoint = points.get(points.size() - 1);
            currentPoint = MathUtils.snapLineToNearestAngle(lastPoint, e.getPoint(), 15);
            points.add(currentPoint);
        } else {
            currentPoint = e.getPoint();
            points.add(currentPoint);
        }

        int[] xs = points.stream().mapToInt(i->i.x).toArray();
        int[] ys = points.stream().mapToInt(i->i.y).toArray();

        getCanvas().clearScratch();

        Graphics2D g2d = getGraphics2D();
        drawPolyline(g2d, xs, ys);
        g2d.dispose();

        getCanvas().repaintCanvas();

        points.remove(points.size() - 1);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        // User double-clicked; complete the polygon
        if (e.getClickCount() > 1 && points.size() > 1){
            getCanvas().clearScratch();

            points.add(currentPoint);

            int[] xs = points.stream().mapToInt(i->i.x).toArray();
            int[] ys = points.stream().mapToInt(i->i.y).toArray();

            points.clear();
            currentPoint = null;

            Graphics2D g2d = getGraphics2D();
            drawPolygon(g2d, xs, ys);
            g2d.dispose();

            getCanvas().commit();
        }

        // First click (creating initial point)
        else if (currentPoint == null) {
            points.add(e.getPoint());
        }

        // Single click with initial point established
        else {
            points.add(currentPoint);
        }
    }

    private Graphics2D getGraphics2D() {
        Graphics2D g2d = (Graphics2D) getCanvas().getScratchGraphics();
        g2d.setStroke(getStroke());
        g2d.setPaint(getPaint());

        return g2d;
    }

    protected abstract void drawPolyline(Graphics g, int[] xPoints, int[] yPoints);
    protected abstract void drawPolygon(Graphics g, int[] xPoints, int[] yPoints);
}
