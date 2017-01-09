package hypercard.paint.utils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

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

    public void transform(AffineTransform t) {
        PathIterator pi = getShape().getPathIterator(t);
        ArrayList<Point> points = new ArrayList<>();

        while (!pi.isDone()) {
            double[] coordinates = new double[6];
            int type = pi.currentSegment(coordinates);
            if (type == PathIterator.SEG_MOVETO || type == PathIterator.SEG_LINETO) {
                points.add(new Point((int) coordinates[0], (int) coordinates[1]));
            }
            pi.next();
        }

        if (points.size() == 4) {
            topLeft = points.get(0);
            topRight = points.get(1);
            bottomRight = points.get(2);
            bottomLeft = points.get(3);
        }

        for (Point thisP : points) {
            System.err.println(thisP);
        }

        System.err.println();
    }

    public Point getCenter() {
        return new Point(getShape().getBounds().width / 2, getShape().getBounds().height / 2);
    }

    public Shape getShape() {
        GeneralPath path = new GeneralPath();
        path.moveTo(topLeft.getX(), topLeft.getY());
        path.lineTo(topRight.getX(), topRight.getY());
        path.lineTo(bottomRight.getX(), bottomRight.getY());
        path.lineTo(bottomLeft.getX(), bottomLeft.getY());
        path.closePath();

        return path;
    }
}
