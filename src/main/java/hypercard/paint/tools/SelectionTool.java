package hypercard.paint.tools;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SelectionTool extends AbstractSelectionTool {

    public SelectionTool() {
        super(ToolType.SELECTION);
    }

    @Override
    protected void drawSelectionBounds(Graphics g, Rectangle bounds) {
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    protected void drawSelectedImage(Graphics g, BufferedImage image, int x, int y) {
        g.drawImage(image, x, y, null);
    }
}
