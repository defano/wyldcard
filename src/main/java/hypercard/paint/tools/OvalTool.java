package hypercard.paint.tools;

import java.awt.*;

public class OvalTool extends AbstractShapeTool {

    public OvalTool() {
        super(PaintToolType.OVAL);
    }

    @Override
    public void drawBounds(Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawOval(x, y, width, height);

        if (getFill() != null) {
            g2d.setPaint(getFill());
            g2d.fillOval(x, y, width, height);
        }
    }
}
