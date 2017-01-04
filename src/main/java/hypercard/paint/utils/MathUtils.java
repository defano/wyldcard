package hypercard.paint.utils;

import org.w3c.dom.css.Rect;

import java.awt.*;

public class MathUtils {

    /**
     * Rounds a given value to the nearest provided integer multiple. For example rounding 24.3 to the nearest 10
     * yields 20.
     *
     * @param value The value to round
     * @param toNearest The nearest integer multiple.
     * @return
     */
    public static int round(Double value, int toNearest) {
        return (int) (toNearest * Math.round(value / toNearest));
    }

    /**
     * Returns the angle (in degrees) of the line represented by (x1, y1), (x2, y2).
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double getLineAngle(int x1, int y1, int x2, int y2) {
        return Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
    }

    /**
     * Given an origin point and a length, calculates the second point of a line whose length and angle match the
     * provided values.
     *
     * @param origin Origin/location of the line
     * @param length Length of the line
     * @param angle Desired angle (in degrees) of the resulting line
     * @return
     */
    public static Point lineAtAngle(Point origin, int length, double angle) {
        double radians = Math.toRadians(angle);
        return new Point((int)(origin.x + length * Math.cos(radians)), (int)(origin.y + length * Math.sin(radians)));
    }

    public static double getLineLength(Point p1, Point p2) {
        return Math.sqrt( ((p1.x - p2.x) * (p1.x - p2.x)) + ((p1.y - p2.y) * (p1.y - p2.y)));
    }

    public static Point snapLineToNearestAngle(Point p1, Point p2, int toNearestAngle) {
        double length = getLineLength(p1, p2);
        double nearestAngle = round(MathUtils.getLineAngle(p1.x, p1.y, p2.x, p2.y), toNearestAngle);
        return lineAtAngle(p1, (int) length, nearestAngle);
    }

    /**
     * Returns a point closest to the given point but which remains inside the provided boundary.
     *
     * @param p
     * @param bounds
     * @return
     */
    public static Point pointWithinBounds(Point p, Rectangle bounds) {

        if (bounds.contains(p)) {
            return p;
        }

        int newX = p.x;
        int newY = p.y;

        if (p.x < bounds.x) {
            newX = bounds.x;
        } else if (p.x > bounds.x + bounds.width) {
            newX = bounds.x + bounds.width;
        }

        if (p.y < bounds.y) {
            newY = bounds.y;
        } else if (p.y > bounds.y + bounds.height) {
            newY = bounds.y + bounds.height;
        }

        return new Point(newX, newY);
    }
}
