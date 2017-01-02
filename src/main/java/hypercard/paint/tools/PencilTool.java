package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;

import java.awt.*;
import java.awt.geom.Line2D;

public class PencilTool extends AbstractBrushTool {

    public PencilTool() {
        super(PaintToolType.PENCIL);
    }

    @Override
    public void drawSegment(Graphics2D g, Stroke stroke, Paint paint, int x1, int y1, int x2, int y2) {
        g.setStroke(stroke);
        g.setPaint(Color.BLACK);
        g.draw(new Line2D.Float(x1,y1,x2,y2));
    }
}
