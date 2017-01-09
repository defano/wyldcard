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
        g.drawPolygon(MathUtils.getRegularPolygon(initialPoint, getShapeSides(), getRadius(), getRotationAngle()));
    }

    @Override
    public void drawFill(Graphics2D g, Paint fill, int x, int y, int width, int height) {
        g.setPaint(fill);
        g.fill(MathUtils.getRegularPolygon(initialPoint, getShapeSides(), getRadius(), getRotationAngle()));
    }

    private double getRadius() {
        return MathUtils.getLineLength(initialPoint, currentPoint);
    }

    private double getRotationAngle() {
        return Math.toRadians(MathUtils.getLineAngle(initialPoint.x, initialPoint.y, currentPoint.x, currentPoint.y));
    }
}
