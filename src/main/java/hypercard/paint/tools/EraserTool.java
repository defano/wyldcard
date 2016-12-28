package hypercard.paint.tools;

import hypercard.paint.observers.ObservableAttribute;

import java.awt.*;
import java.awt.geom.Line2D;

public class EraserTool extends AbstractBrushTool {

    public EraserTool() {
        super(ToolType.ERASER);

        // Eraser is basically a paintbrush whose stroke "clears" the pixels underneath it via the DST_OUT composite mode
        setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1.0f));
    }

    @Override
    public void drawSegment(Graphics g, int x1, int y1, int x2, int y2) {
        Graphics2D g2 = (Graphics2D) g;

        // Special case: Eraser overrides brush stroke and paint settings
        g2.setStroke(new BasicStroke(10));
        g2.setColor(Color.WHITE);

        g2.draw(new Line2D.Float(x1,y1,x2,y2));
    }
}
