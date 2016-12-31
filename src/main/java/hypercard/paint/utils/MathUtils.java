package hypercard.paint.utils;

import java.awt.*;

public class MathUtils {

    public static int round(Double value, int toNearest) {
        return (int) (toNearest * Math.round(value / toNearest));
    }

    public static double getLineAngle(int x1, int y1, int x2, int y2) {
        return Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
    }

    public static Point lineAtAngle(Point origin, int length, double angle) {
        double radians = Math.toRadians(angle);
        return new Point((int)(origin.x + length * Math.cos(radians)), (int)(origin.y + length * Math.sin(radians)));
    }

    public static double getLineLength(Point p1, Point p2) {
        return Math.sqrt( ((p1.x - p2.x) * (p1.x - p2.x)) + ((p1.y - p2.y) * (p1.y - p2.y)));
    }

    public static Point snapLineToNearestAngle(Point p1, Point p2, int toNearestAngle) {
        double length = MathUtils.getLineLength(p1, p2);
        double nearestAngle = MathUtils.round(MathUtils.getLineAngle(p1.x, p1.y, p2.x, p2.y), toNearestAngle);
        return MathUtils.lineAtAngle(p1, (int) length, nearestAngle);
    }
}
