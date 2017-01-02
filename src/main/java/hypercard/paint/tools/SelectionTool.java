package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SelectionTool extends AbstractSelectionTool {

    private Rectangle selectionBounds;

    public SelectionTool() {
        super(PaintToolType.SELECTION);
    }

    @Override
    protected void defineSelectionBounds(Point initialPoint, Point currentPoint) {
        selectionBounds = new Rectangle(initialPoint);
        selectionBounds.add(currentPoint);
    }

    @Override
    protected void completeSelectionBounds(Point finalPoint) {
        // Nothing to do
    }

    @Override
    public void resetSelection() {
        selectionBounds = null;
    }

    @Override
    protected void drawSelectionBounds(Graphics2D g, boolean constrainToSquare) {
        if (selectionBounds != null) {

            int left = selectionBounds.x;
            int top = selectionBounds.y;
            int right = selectionBounds.x + selectionBounds.width;
            int bottom = selectionBounds.y + selectionBounds.height;

            int width = (right - left);
            int height = (bottom - top);

            if (constrainToSquare) {
                width = height = Math.max(width, height);
            }

            selectionBounds = new Rectangle(left, top, width, height);

            g.setStroke(MARCHING_ANTS);
            g.setColor(Color.BLACK);
            g.draw(selectionBounds);
        }
    }

    @Override
    protected Shape getSelectionBounds() {
        return selectionBounds;
    }

    @Override
    protected BufferedImage getSelectedImage(Graphics2D scratch, BufferedImage canvasImage) {

        // Limit the selection bounds to the raster bounds (i.e., can't select image outside of canvas boundary)
        Rectangle rasterBounds = canvasImage.getRaster().getBounds();
        int maxX = (selectionBounds.x + selectionBounds.width > rasterBounds.x + rasterBounds.width) ? rasterBounds.x + rasterBounds.width : selectionBounds.x + selectionBounds.width;
        int maxY = (selectionBounds.y + selectionBounds.height > rasterBounds.y + rasterBounds.height) ? rasterBounds.y + rasterBounds.height : selectionBounds.y + selectionBounds.height;

        Rectangle subimageBounds = new Rectangle(selectionBounds.getLocation());
        subimageBounds.add(new Point(maxX, maxY));

        BufferedImage selection = canvasImage.getSubimage(subimageBounds.x, subimageBounds.y, subimageBounds.width, subimageBounds.height);

        Graphics2D g2 = (Graphics2D) getCanvas().getScratchGraphics();
        g2.setColor(Color.WHITE);
        g2.fill(getSelectionBounds());
        getCanvas().commit(AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1.0f));

        return selection;
    }

    @Override
    protected void adjustSelectionBounds(int xDelta, int yDelta) {
        selectionBounds.setLocation(selectionBounds.x + xDelta, selectionBounds.y + yDelta);
    }
}
