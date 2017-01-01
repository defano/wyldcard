package hypercard.paint.tools;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Stack;
import java.util.function.Predicate;

public class FillTool extends AbstractPaintTool {

    private BufferedImage canvasImage;
    private Rectangle canvasBounds;
    private Paint fillPaint;

    public FillTool() {
        super(PaintToolType.FILL);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        canvasImage = getCanvas().getCanvasImage();
        canvasBounds = getCanvas().getBounds();
        fillPaint = getFillPaint();

        getCanvas().clearScratch();

        floodFill(e.getX(), e.getY(), point -> {
            Color canvasPixel = new Color(canvasImage.getRGB(point.x, point.y), true);
            Color scratchPixel = new Color(getCanvas().getScratchImage().getRGB(point.x, point.y), true);
            return canvasPixel.getAlpha() == 0 && scratchPixel.getAlpha() == 0;
        });

        getCanvas().commit();
        getCanvas().repaintCanvas();
    }

    private void floodFill(int x, int y, Predicate<Point> boundaryFunction) {
        Stack<Point> fillPixels = new Stack<>();
        fillPixels.push(new Point(x, y));

        while (!fillPixels.isEmpty()) {
            Point thisPixel = fillPixels.pop();
            fill(thisPixel);

            Point right = new Point(thisPixel.x + 1, thisPixel.y);
            Point left = new Point(thisPixel.x - 1, thisPixel.y);
            Point down = new Point(thisPixel.x, thisPixel.y + 1);
            Point up = new Point(thisPixel.x, thisPixel.y - 1);

            if (canvasBounds.contains(right) && boundaryFunction.test(right)) {
                fillPixels.push(right);
            }

            if (canvasBounds.contains(left) && boundaryFunction.test(left)) {
                fillPixels.push(left);
            }

            if (canvasBounds.contains(down) && boundaryFunction.test(down)) {
                fillPixels.push(down);
            }

            if (canvasBounds.contains(up) && boundaryFunction.test(up)) {
                fillPixels.push(up);
            }
        }
    }

    private void fill(Point p) {
        int rgb = getFillRgb(p.x, p.y, fillPaint);
        getCanvas().getScratchImage().setRGB(p.x, p.y, rgb);
    }

    private int getFillRgb(int x, int y, Paint paint) {

        if (paint == null) {
            return new Color(0, 0, 0, 0).getRGB();
        }
        else if (paint instanceof Color) {
            return ((Color) paint).getRGB();
        }

        throw new IllegalArgumentException("Don't know how to fill with paint " + paint);

    }
}
