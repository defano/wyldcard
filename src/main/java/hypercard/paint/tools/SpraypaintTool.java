package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;

import java.awt.*;
import java.awt.geom.Line2D;

public class SpraypaintTool extends AbstractPathTool {

    public SpraypaintTool() {
        super(PaintToolType.SPRAYPAINT);
    }

    @Override
    public void drawSegment(Graphics2D g, Stroke stroke, Paint paint, int x1, int y1, int x2, int y2) {
        g.setStroke(stroke);
        g.setPaint(paint);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
        g.draw(new Line2D.Float(x1,y1,x2,y2));
    }
}
