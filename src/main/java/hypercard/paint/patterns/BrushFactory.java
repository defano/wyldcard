package hypercard.paint.patterns;

import javafx.scene.shape.Circle;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class BrushFactory {

    public enum BasicBrush {
        SQUARE_16X16,
        SQUARE_12X12,
        SQUARE_8X8,
        SQUARE_4X4
    }

//    public static Stroke createBrush (BasicBrush basicBrush) {
//
//    }

    public static Stroke createSquareBrush(int size) {
        return new SquareStroke(16);
    }

    public static Stroke createRoundBrush(int size) {
        return new RoundStroke(16);
    }

    public static class SquareStroke implements Stroke {

        private final int size;

        public SquareStroke(int size) {
            this.size = size;
        }

        @Override
        public Shape createStrokedShape(Shape p) {
            return new Rectangle(p.getBounds().x - size / 2, p.getBounds().y - size / 2 , size, size);
        }
    }

    public static class RoundStroke implements Stroke {

        private final int size;

        public RoundStroke(int size) {
            this.size = size;
        }

        @Override
        public Shape createStrokedShape(Shape p) {
            return new Ellipse2D.Double(p.getBounds().x - size / 2, p.getBounds().y - size / 2, size, size);
        }
    }


}
