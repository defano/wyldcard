package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;
import hypercard.paint.utils.MathUtils;

import java.awt.*;

/**
 * Tool for drawing regular polygons ("shapes") based on a configurable number of sides. For example, triangles,
 * squares, pentagons, hexagons, etc.
 */
public class ShapeTool extends AbstractBoundsTool {

    public ShapeTool() {
        super(PaintToolType.SHAPE);
    }

    @Override
    public void drawBounds(Graphics2D g, Stroke stroke, Paint paint, int x, int y, int width, int height) {
        g.setStroke(stroke);
        g.setPaint(paint);
        g.drawPolygon(getRegularPolygon(getShapeSides()));
    }

    @Override
    public void drawFill(Graphics2D g, Paint fill, int x, int y, int width, int height) {
        g.setPaint(fill);
        g.fill(getRegularPolygon(getShapeSides()));
    }

    private Polygon getRegularPolygon(int sides) {
        double length = MathUtils.getLineLength(initialPoint, currentPoint);
        double rotation = MathUtils.getLineAngle(initialPoint.x, initialPoint.y, currentPoint.x, currentPoint.y);

        double angle = (2 * Math.PI) / sides;
        double radius = (length / 2) / Math.sin(angle / 2);

        double xPoint = (Math.cos(rotation) * radius) + initialPoint.x;
        double yPoint = (Math.sin(rotation) * radius) + initialPoint.y;

        Polygon polygon = new Polygon();
        polygon.addPoint((int)xPoint, (int)yPoint);

        for (int i = 1; i <= sides; i++) {
            xPoint = (Math.cos((angle * i) + rotation) * radius) + initialPoint.x;
            yPoint = (Math.sin((angle * i) + rotation) * radius) + initialPoint.y;
            polygon.addPoint((int) xPoint, (int) yPoint);
        }

        return polygon;
    }
}
