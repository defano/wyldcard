package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

public class LassoTool extends AbstractSelectionTool {

    private Path2D selectionBounds;

    public LassoTool() {
        super(PaintToolType.LASSO);
    }

    @Override
    protected void resetSelection() {
        selectionBounds = null;
    }

    @Override
    protected void defineSelectionBounds(Point initialPoint, Point currentPoint) {
        if (selectionBounds == null) {
            selectionBounds = new Path2D.Double();
            selectionBounds.moveTo(initialPoint.getX(), initialPoint.getY());
        }

        selectionBounds.lineTo(currentPoint.getX(), currentPoint.getY());
    }

    @Override
    protected void completeSelectionBounds(Point finalPoint) {
        selectionBounds.closePath();
    }

    @Override
    protected void drawSelectionBounds(Graphics2D g, boolean constrainToSquare) {
        g.setStroke(MARCHING_ANTS);
        g.setColor(Color.BLACK);
        g.draw(selectionBounds);
    }

    @Override
    protected Shape getSelectionBounds() {
        return selectionBounds;
    }

    @Override
    protected void adjustSelectionBounds(int xDelta, int yDelta) {
        selectionBounds.transform(AffineTransform.getTranslateInstance(xDelta, yDelta));

    }

    @Override
    protected BufferedImage getSelectedImage(Graphics2D scratch, BufferedImage canvasImage) {
        // Limit the selection bounds to the raster bounds (i.e., can't select image outside of canvas boundary)
        Rectangle rasterBounds = canvasImage.getRaster().getBounds();
        int maxX = (selectionBounds.getBounds().x + selectionBounds.getBounds().width > rasterBounds.x + rasterBounds.width) ? rasterBounds.x + rasterBounds.width : selectionBounds.getBounds().x + selectionBounds.getBounds().width;
        int maxY = (selectionBounds.getBounds().y + selectionBounds.getBounds().height > rasterBounds.y + rasterBounds.height) ? rasterBounds.y + rasterBounds.height : selectionBounds.getBounds().y + selectionBounds.getBounds().height;

        Rectangle subimageBounds = new Rectangle(selectionBounds.getBounds().getLocation());
        subimageBounds.add(new Point(maxX, maxY));

        BufferedImage selection = getSelection(getCanvas().getCanvasImage(), selectionBounds);
        BufferedImage trimmedSelection = selection.getSubimage(subimageBounds.x, subimageBounds.y, subimageBounds.width, subimageBounds.height);

        // Clear image underneath selection
        Graphics2D g2 = (Graphics2D) getCanvas().getScratchGraphics();
        g2.setColor(Color.WHITE);
        g2.fill(selectionBounds);
        getCanvas().commit(AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1.0f));

        return trimmedSelection;
    }

    private BufferedImage getSelection(BufferedImage image, Shape shape) {
        BufferedImage subimage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        int clearPixel = new Color(0,0,0,0).getRGB();

        for (int y = 0; y < image.getRaster().getHeight(); y++) {
            for (int x = 0; x < image.getRaster().getWidth(); x++) {
                if (x > image.getWidth() || y > image.getHeight()) continue;

                if (shape.contains(x, y)) {
                    subimage.setRGB(x, y, image.getRGB(x, y));
                } else {
                    subimage.setRGB(x, y, clearPixel);
                }
            }
        }

        return subimage;
    }
}
