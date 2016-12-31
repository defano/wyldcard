package hypercard.paint.tools;

import java.awt.*;

public class RectangleTool extends AbstractShapeTool {

    public RectangleTool() {
        super(PaintToolType.RECTANGLE);
    }

    @Override
    public void drawBounds(Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawRect(x, y, width, height);

        if (getFill() != null) {
            g2d.setPaint(getFill());
            g2d.fillRect(x, y, width, height);
        }
    }
}
