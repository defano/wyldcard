package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;

import java.awt.*;

public class OvalTool extends AbstractBoundsTool {

    public OvalTool() {
        super(PaintToolType.OVAL);
    }

    @Override
    public void drawBounds(Graphics2D g, Stroke stroke, Paint paint, int x, int y, int width, int height) {
        g.setStroke(stroke);
        g.setPaint(paint);
        g.drawOval(x, y, width, height);
    }

    @Override
    public void drawFill(Graphics2D g, Paint fill, int x, int y, int width, int height) {
        g.setPaint(getFillPaint());
        g.fillOval(x, y, width, height);
    }
}
