package hypercard.paint.tools;

import java.awt.*;

public class EraserTool extends AbstractBrushTool {

    public EraserTool() {
        super(PaintToolType.ERASER);

        // Eraser is basically a paintbrush whose stroke "clears" the pixels underneath it via the DST_OUT composite mode
        setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1.0f));
    }

    @Override
    public void drawSegment(Graphics2D g, Stroke stroke, Paint paint, int x1, int y1, int x2, int y2) {
        g.setColor(Color.WHITE);
        g.drawLine(x1, y1, x2, y2);
    }
}
