package hypercard.paint.tools;

import java.awt.*;

public class RoundRectangleTool extends AbstractShapeTool {

    private int cornerRadius = 10;

    public RoundRectangleTool() {
        super(PaintToolType.ROUND_RECTANGLE);
    }

    @Override
    public void drawBounds(Graphics2D g, Stroke stroke, Paint paint, int x, int y, int width, int height) {
        g.setPaint(paint);
        g.setStroke(stroke);
        g.drawRoundRect(x, y, width, height, cornerRadius, cornerRadius);
    }

    @Override
    public void drawFill(Graphics2D g, Paint fill, int x, int y, int width, int height) {
        g.setPaint(fill);
        g.fillRoundRect(x, y, width, height, cornerRadius, cornerRadius);
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
    }
}
