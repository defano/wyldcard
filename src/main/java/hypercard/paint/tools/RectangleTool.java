package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;

import java.awt.*;

public class RectangleTool extends AbstractBoundsTool {

    public RectangleTool() {
        super(PaintToolType.RECTANGLE);
    }

    @Override
    public void drawBounds(Graphics2D g, Stroke stroke, Paint paint, int x, int y, int width, int height) {
        g.setStroke(stroke);
        g.setPaint(paint);
        g.drawRect(x, y, width, height);
    }

    @Override
    public void drawFill(Graphics2D g, Paint fill, int x, int y, int width, int height) {
        g.setPaint(getFillPaint());
        g.fillRect(x, y, width, height);
    }
}
