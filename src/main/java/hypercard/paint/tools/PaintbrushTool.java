package hypercard.paint.tools;

import java.awt.*;
import java.awt.geom.Line2D;

public class PaintbrushTool extends AbstractBrushTool {

    public PaintbrushTool() {
        super(PaintToolType.PAINTBRUSH);
    }

    @Override
    public void drawSegment(Graphics g, int x1, int y1, int x2, int y2) {
        Graphics2D g2 = (Graphics2D) g;
        g2.draw(new Line2D.Float(x1,y1,x2,y2));
    }
}
