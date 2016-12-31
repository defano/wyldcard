package hypercard.paint.tools;

import hypercard.paint.MathUtils;
import hypercard.paint.canvas.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPolylineTool extends AbstractPaintTool implements KeyListener {

    private List<Point> points = new ArrayList<>();
    private Point currentPoint = null;

    protected abstract void drawPolyline(Graphics g, int[] xPoints, int[] yPoints);
    protected abstract void drawPolygon(Graphics g, int[] xPoints, int[] yPoints);

    public AbstractPolylineTool(PaintToolType type) {
        super(type);
    }

    @Override
    public void activate(hypercard.paint.canvas.Canvas canvas) {
        super.activate(canvas);
        getCanvas().addKeyListener(this);
    }

    @Override
    public void deactivate() {
        super.deactivate();
        getCanvas().removeKeyListener(this);
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
    public void mousePressed(MouseEvent e) {

        // User double-clicked; complete the polygon
        if (e.getClickCount() > 1 && points.size() > 1){
            points.add(currentPoint);
            commitPolygon();
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

    private void commitPolygon() {
        getCanvas().clearScratch();

        int[] xs = points.stream().mapToInt(i->i.x).toArray();
        int[] ys = points.stream().mapToInt(i->i.y).toArray();

        points.clear();
        currentPoint = null;

        Graphics2D g2d = getGraphics2D();
        drawPolygon(g2d, xs, ys);
        g2d.dispose();

        getCanvas().commit();
    }

    private void commitPolyline() {
        getCanvas().clearScratch();

        int[] xs = points.stream().mapToInt(i->i.x).toArray();
        int[] ys = points.stream().mapToInt(i->i.y).toArray();

        points.clear();
        currentPoint = null;

        Graphics2D g2d = getGraphics2D();
        drawPolyline(g2d, xs, ys);
        g2d.dispose();

        getCanvas().commit();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Nothing to do
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            points.add(currentPoint);
            commitPolyline();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Nothing to do
    }
}
