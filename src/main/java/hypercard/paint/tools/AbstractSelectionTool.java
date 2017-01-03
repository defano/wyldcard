package hypercard.paint.tools;

import hypercard.paint.canvas.Canvas;
import hypercard.paint.model.PaintToolType;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public abstract class AbstractSelectionTool extends AbstractPaintTool implements KeyListener {

    protected final BasicStroke MARCHING_ANTS = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, new float[]{5.0f}, 0.0f);

    private BufferedImage selectedImage;
    private Point initialPoint, lastPoint;
    private boolean isMovingSelection = false;

    protected abstract void resetSelection();
    protected abstract void defineSelectionBounds(Point initialPoint, Point currentPoint);
    protected abstract void completeSelectionBounds(Point finalPoint);
    protected abstract void drawSelectionBounds(Graphics2D g, boolean constrainToSquare);
    protected abstract Shape getSelectionBounds();
    protected abstract void adjustSelectionBounds(int xDelta, int yDelta);
    protected abstract BufferedImage getSelectedImage(Graphics2D scratch, BufferedImage canvasImage);

    public AbstractSelectionTool(PaintToolType type) {
        super(type);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (hasSelectionBounds() && getSelectionBounds().contains(e.getPoint())) {
            setToolCursor(Cursor.getDefaultCursor());
        } else {
            setToolCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        isMovingSelection = getSelectionBounds() != null && getSelectionBounds().contains(e.getPoint());

        // User clicked inside selection bounds; start moving selection
        if (isMovingSelection) {
            lastPoint = e.getPoint();
        }

        // User clicked outside current selection bounds
        else {
            putDownSelection();
            initialPoint = e.getPoint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        // User is moving an existing selection
        if (isMovingSelection) {
            adjustSelectionBounds(e.getX() - lastPoint.x, e.getY() - lastPoint.y);
            drawSelection(selectedImage);
            lastPoint = e.getPoint();
        }

        // User is defining a new selection rectangle
        else {
            defineSelectionBounds(initialPoint, e.getPoint());

            getCanvas().clearScratch();
            Graphics2D g = (Graphics2D) getCanvas().getScratchGraphics();
            drawSelectionBounds(g, e.isShiftDown());
            g.dispose();

            getCanvas().repaintCanvas();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // User released mouse after defining a selection
        if (!hasSelection() && hasSelectionBounds()) {
            completeSelectionBounds(e.getPoint());
            pickupSelection();
        }
    }

    @Override
    public void activate(Canvas canvas) {
        super.activate(canvas);

        getCanvas().addKeyListener(this);
        getCanvas().addObserver(this);
    }

    @Override
    public void deactivate() {
        super.deactivate();

        // Need to remove selection frame when tool is no longer active
        putDownSelection();
        getCanvas().removeKeyListener(this);
        getCanvas().removeObserver(this);
    }

    /**
     * Clears the current selection frame (removes any "marching ants" from the canvas), but does not "erase" the image
     * selected.
     */
    public void clearSelection() {
        selectedImage = null;
        resetSelection();

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
        return getSelectionBounds() != null && getSelectionBounds().getBounds().width > 0 && getSelectionBounds().getBounds().height > 0;
    }

    /**
     * Make the canvas image bounded by the given selection rectangle the current selected image.
     *
     */
    protected void pickupSelection() {
        getCanvas().clearScratch();

        Graphics2D g = (Graphics2D) getCanvas().getScratchGraphics();
        selectedImage = getSelectedImage(g, getCanvas().getCanvasImage());
        g.dispose();

        drawSelection(selectedImage);
    }

    /**
     * Drops the selected image onto the canvas and clears the selection. This has the effect of completing a select-
     * and-move operation.
     */
    protected void putDownSelection() {

        if (hasSelection()) {
            getCanvas().clearScratch();

            Graphics2D g2d = (Graphics2D) getCanvas().getScratchGraphics();
            g2d.drawImage(selectedImage, getSelectionBounds().getBounds().x, getSelectionBounds().getBounds().y, null);
            g2d.dispose();

            getCanvas().commit();
            clearSelection();
        }
    }

    /**
     * Draws the provided image and selection frame ("marching ants") onto the scratch buffer at the given location.
     *
     * @param selectedImage The selected image to draw
     */
    private void drawSelection(BufferedImage selectedImage) {
        getCanvas().clearScratch();
        Graphics2D g = (Graphics2D) getCanvas().getScratchGraphics();

        drawSelectionBounds(g, false);
        g.drawImage(selectedImage, getSelectionBounds().getBounds().x, getSelectionBounds().getBounds().y, null);
        g.dispose();

        getCanvas().repaintCanvas();
    }

    @Override
    public void onCommit(Canvas canvas, BufferedImage committedElement, BufferedImage canvasImage) {
        if (hasSelection()) {
            clearSelection();
        }
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
                    adjustSelectionBounds(-1, 0);
                    drawSelection(selectedImage);
                    break;

                case KeyEvent.VK_RIGHT:
                    adjustSelectionBounds(1, 0);
                    drawSelection(selectedImage);
                    break;

                case KeyEvent.VK_UP:
                    adjustSelectionBounds(0, -1);
                    drawSelection(selectedImage);
                    break;

                case KeyEvent.VK_DOWN:
                    adjustSelectionBounds(0, 1);
                    drawSelection(selectedImage);
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
