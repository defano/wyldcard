package hypercard.paint.tools;

import hypercard.paint.canvas.Canvas;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;

public abstract class AbstractSelectionTool extends AbstractShapeTool implements KeyListener {

    private Rectangle selectionRectangle;
    private BufferedImage selectedImage;

    private Point lastPoint;
    private boolean clickInsideSelection = false;

    /**
     * Draws a selection frame of the given bounds onto the Graphics context. For example, renders a rectangle of
     * "marching ants".
     *
     * @param g      The graphics context to draw onto.
     * @param bounds The bounds of the selection frame.
     */
    protected abstract void drawSelectionBounds(Graphics g, Rectangle bounds);

    /**
     * Draws the selected image at the given coordinates.
     *
     * @param g     The graphics context to draw onto
     * @param image The image to draw
     * @param x     The x coordinate of where the image should be drawn
     * @param y     The y coordinate of where the image should be drawn
     */
    protected abstract void drawSelectedImage(Graphics g, BufferedImage image, int x, int y);

    public AbstractSelectionTool(PaintToolType type) {
        super(type);
    }

    @Override
    public void drawBounds(Graphics g, int x, int y, int width, int height) {

        BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, new float[]{5.0f}, 0.0f);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(dashed);
        g2d.setColor(Color.BLACK);
        drawSelectionBounds(g2d, new Rectangle(x, y, width, height));

        selectionRectangle = new Rectangle(x, y, width, height);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        clickInsideSelection = selectionRectangle != null && selectionRectangle.contains(e.getPoint());

        // User clicked outside current selection bounds
        if (!clickInsideSelection) {
            putDownSelection();
            super.mousePressed(e);
        }

        // User clicked inside selection bounds; start moving selection
        else {
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
            moveSelection(e.getX() - lastPoint.x, e.getY() - lastPoint.y);
            lastPoint = e.getPoint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        // User released mouse after defining a selection
        if (!hasSelection() && hasSelectionBounds()) {
            pickupSelection(selectionRectangle);
        }
    }

    @Override
    public void activate(Canvas canvas) {
        super.activate(canvas);
        getCanvas().addKeyListener(this);
    }

    @Override
    public void deactivate() {
        super.deactivate();

        // Need to remove selection frame when tool is no longer active
        clearSelection();
        getCanvas().removeKeyListener(this);
    }

    /**
     * Clears the current selection frame (removes any "marching ants" from the canvas), but does not "erase" the image
     * selected.
     */
    public void clearSelection() {
        selectionRectangle = null;
        selectedImage = null;

        getCanvas().clearScratch();
        getCanvas().repaintCanvas();
    }

    /**
     * Determines if the user has an active selection.
     * <p>
     * Differs from {@link #hasSelectionBounds()} in that when a user is dragging the selection rectangle, a selection
     * boundary will exist but a selection will not. The selection is not made until the user releases the mouse.
     *
     * @return True is a selection exists, false otherwise.
     */
    public boolean hasSelection() {
        return hasSelectionBounds() && selectedImage != null;
    }

    /**
     * Determines if the user has an active selection boundary (i.e., a rectangle of marching ants)
     * <p>
     * Differs from {@link #hasSelectionBounds()} in that when a user is dragging the selection rectangle, a selection
     * boundary will exist but a selection will not. The selection is not made until the user releases the mouse.
     *
     * @return True if a selection boundary exists, false otherwise.
     */
    public boolean hasSelectionBounds() {
        return selectionRectangle != null && selectionRectangle.width > 0 && selectionRectangle.height > 0;
    }

    /**
     * Make the canvas image bounded by the given selection rectangle the current selected image.
     *
     * @param selectionRectangle The selection bounds to capture.
     */
    protected void pickupSelection(Rectangle selectionRectangle) {
        try {
            BufferedImage selection = getCanvas().getCanvasImage().getSubimage(selectionRectangle.x, selectionRectangle.y, selectionRectangle.width, selectionRectangle.height);
            eraseCanvasRectangle(selectionRectangle);
            drawSelection(selection, selectionRectangle);
            selectedImage = selection;
        } catch (RasterFormatException ex) {
        }
    }

    /**
     * Moves the selected image (including the selection frame) by some delta in the x/y direction.
     *
     * @param xDelta Number of pixels +/- to move in the x-direction
     * @param yDelta Number of pixels +/- to move in the y-direction
     */
    protected void moveSelection(int xDelta, int yDelta) {
        selectionRectangle.setLocation(selectionRectangle.x + xDelta, selectionRectangle.y + yDelta);
        drawSelection(selectedImage, selectionRectangle);
    }

    /**
     * Drops the selected image onto the canvas and clears the selection. This has the effect of completing a select-
     * and-move operation.
     */
    protected void putDownSelection() {

        if (hasSelection()) {
            getCanvas().clearScratch();

            Graphics2D g2d = (Graphics2D) getCanvas().getScratchGraphics();
            drawSelectedImage(g2d, selectedImage, selectionRectangle.x, selectionRectangle.y);
            g2d.dispose();

            getCanvas().commit();

            g2d = (Graphics2D) getCanvas().getScratchGraphics();
            drawBounds(g2d, selectionRectangle.x, selectionRectangle.y, selectionRectangle.width, selectionRectangle.height);
            g2d.dispose();

            clearSelection();
        }
    }

    /**
     * Draws the provided image and selection frame ("marching ants") onto the scratch buffer at the given location.
     *
     * @param selectedImage The selected image to draw
     * @param location      The bounding box of the selection (i.e., rectangle of ants)
     */
    private void drawSelection(BufferedImage selectedImage, Rectangle location) {
        getCanvas().clearScratch();

        drawBounds(getCanvas().getScratchGraphics(), location.x, location.y, location.width, location.height);

        Graphics2D g2d = (Graphics2D) getCanvas().getScratchGraphics();
        drawSelectedImage(g2d, selectedImage, location.x, location.y);
        g2d.dispose();

        getCanvas().repaintCanvas();
    }

    /**
     * Erases the contents of the canvas bounded by the given rectangle.
     *
     * @param bounds
     */
    private void eraseCanvasRectangle(Rectangle bounds) {
        Graphics2D g2 = (Graphics2D) getCanvas().getScratchGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        getCanvas().commit(AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1.0f));
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (hasSelection()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_DELETE:
                case KeyEvent.VK_BACK_SPACE:
                    clearSelection();
                    getCanvas().clearScratch();
                    getCanvas().repaintCanvas();
                    break;

                case KeyEvent.VK_LEFT:
                    moveSelection(-1, 0);
                    break;

                case KeyEvent.VK_RIGHT:
                    moveSelection(1, 0);
                    break;

                case KeyEvent.VK_UP:
                    moveSelection(0, -1);
                    break;

                case KeyEvent.VK_DOWN:
                    moveSelection(0, 1);
                    break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Nothing to do
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Nothing to do
    }
}
