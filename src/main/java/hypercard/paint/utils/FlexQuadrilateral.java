package hypercard.paint.utils;

import java.awt.*;
import java.awt.geom.Path2D;

public class FlexQuadrilateral {

    private Point topLeft;
    private Point topRight;
    private Point bottomLeft;
    private Point bottomRight;

    public FlexQuadrilateral(Shape fromShape) {
        Rectangle bounds = fromShape.getBounds();
        topLeft = new Point(bounds.x, bounds.y);
        topRight = new Point(bounds.x + bounds.width, bounds.y);
        bottomLeft = new Point(bounds.x, bounds.y + bounds.height);
        bottomRight = new Point(bounds.x + bounds.width, bounds.y + bounds.height);
    }

    public Point getTopLeft() {
        return topLeft;
    }

    public void setTopLeft(Point topLeft) {
        this.topLeft = topLeft;
    }

    public Point getTopRight() {
        return topRight;
    }

    public void setTopRight(Point topRight) {
        this.topRight = topRight;
    }

    public Point getBottomLeft() {
        return bottomLeft;
    }

    public void setBottomLeft(Point bottomLeft) {
        this.bottomLeft = bottomLeft;
    }

    public Point getBottomRight() {
        return bottomRight;
    }

    public void setBottomRight(Point bottomRight) {
        this.bottomRight = bottomRight;
    }

    public Point[] getCorners() {
        return new Point[] {getTopLeft(), getTopRight(), getBottomRight(), getBottomLeft()};
    }

    public Shape getShape() {
        Path2D path = new Path2D.Double();
        path.moveTo(topLeft.getX(), topLeft.getY());
        path.lineTo(topRight.getX(), topRight.getY());
        path.lineTo(bottomRight.getX(), bottomRight.getY());
        path.lineTo(bottomLeft.getX(), bottomLeft.getY());
        path.closePath();

        return path;
    }
}
