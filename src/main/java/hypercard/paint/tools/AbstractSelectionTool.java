package hypercard.paint.tools;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;

public abstract class AbstractSelectionTool extends AbstractShapeTool {

    private Rectangle selectionRectangle;
    private BufferedImage selectedImage;

    private Point lastPoint;
    private boolean clickInsideSelection = false;

    public AbstractSelectionTool(ToolType type) {
        super(type);
    }

    @Override
    public void drawShape(Graphics g, int x1, int y1, int width, int height) {

        float dash1[] = {5.0f};
        BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash1, 0.0f);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(dashed);
        g2d.setColor(Color.BLACK);
        drawSelectionBounds(g2d, new Rectangle(x1, y1, width, height));

        selectionRectangle = new Rectangle(x1, y1, width, height);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        clickInsideSelection = selectionRectangle != null && selectionRectangle.contains(e.getPoint());

        // User clicked outside current selection bounds
        if (!clickInsideSelection) {
            clearSelection();
            super.mousePressed(e);
        }

        // User clicked inside selection bounds; start moving selection
        else {
            eraseCanvasRectangle(selectionRectangle);
            lastPoint = e.getPoint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        // User is defining a new selection rectangle
        if (!clickInsideSelection) {
            super.mouseDragged(e);
        }

        // User is moving an existing selection
        else {
            selectionRectangle = moveSelection(selectedImage, selectionRectangle, e.getX() - lastPoint.x, e.getY() - lastPoint.y);
            lastPoint = e.getPoint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        // User released mouse after clicking/dragging the selection rectangle
        if (clickInsideSelection) {
            commitSelection(selectedImage, selectionRectangle);
        }

        // Capture the image under the selection rectangle
        if (selectionRectangle != null && selectionRectangle.width > 0 && selectionRectangle.height > 0) {
            BufferedImage newSelection = captureSelection(selectionRectangle);
            if (newSelection != null) {
                selectedImage = newSelection;
            }
        }
    }

    @Override
    public void deactivate() {
        super.deactivate();

        // Clear any existing selection rectangle
        getCanvas().clearScratch();
        getCanvas().repaintCanvas();
    }

    protected void clearSelection() {
        getCanvas().clearScratch();
        getCanvas().repaintCanvas();
        selectionRectangle = null;
    }

    protected BufferedImage captureSelection(Rectangle selectionRectangle) {
        try {
            return getCanvas().getCanvasImage().getSubimage(selectionRectangle.x, selectionRectangle.y, selectionRectangle.width, selectionRectangle.height);
        } catch (RasterFormatException ex) {
            return null;
        }
    }

    protected Rectangle moveSelection(BufferedImage selectedImage, Rectangle bounds, int xDelta, int yDelta) {
        bounds.setLocation(bounds.x + xDelta, bounds.y + yDelta);
        redrawSelectionAt(selectedImage, bounds);

        return bounds;
    }

    protected void commitSelection(BufferedImage selectedImage, Rectangle selectionRectangle) {
        getCanvas().clearScratch();

        Graphics2D g2d = (Graphics2D) getCanvas().getScratchGraphics();
        drawSelectedImage(g2d, selectedImage, selectionRectangle.x, selectionRectangle.y);
        g2d.dispose();

        getCanvas().commit();

        g2d = (Graphics2D) getCanvas().getScratchGraphics();
        drawShape(g2d, selectionRectangle.x, selectionRectangle.y, selectionRectangle.width, selectionRectangle.height);
        g2d.dispose();

        getCanvas().repaintCanvas();
    }

    protected void redrawSelectionAt(BufferedImage selectedImage, Rectangle location) {
        getCanvas().clearScratch();

        drawShape(getCanvas().getScratchGraphics(), location.x, location.y, location.width, location.height);

        Graphics2D g2d = (Graphics2D) getCanvas().getScratchGraphics();
        drawSelectedImage(g2d, selectedImage, location.x, location.y);
        g2d.dispose();

        getCanvas().repaintCanvas();
    }

    protected void eraseCanvasRectangle(Rectangle rectangle) {
        Graphics2D g2 = (Graphics2D) getCanvas().getScratchGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        getCanvas().commit(AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1.0f));
    }

    protected abstract void drawSelectionBounds(Graphics g, Rectangle bounds);
    protected abstract void drawSelectedImage(Graphics g, BufferedImage image, int x, int y);
}
