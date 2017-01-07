package hypercard.paint.tools;

import hypercard.paint.canvas.Canvas;
import hypercard.paint.model.PaintToolType;
import hypercard.paint.utils.MathUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractSelectionTool extends AbstractPaintTool implements KeyListener {

    private BufferedImage selectedImage;
    private Point initialPoint, lastPoint;
    private boolean isMovingSelection = false;

    private int antsPhase;
    private ScheduledExecutorService antsAnimator = Executors.newSingleThreadScheduledExecutor();
    private Future antsAnimation;

    protected abstract void resetSelection();
    protected abstract void defineSelectionBounds(Point initialPoint, Point currentPoint, boolean constrain);
    protected abstract void completeSelectionBounds(Point finalPoint);
    protected abstract Shape getSelectionBounds();
    protected abstract void adjustSelectionBounds(int xDelta, int yDelta);

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
            drawSelection();
            lastPoint = e.getPoint();
        }

        // User is defining a new selection rectangle
        else {
            defineSelectionBounds(initialPoint, MathUtils.pointWithinBounds(e.getPoint(), getCanvas().getBounds()), e.isShiftDown());

            getCanvas().clearScratch();
            drawSelectionBounds();
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

        antsAnimation = antsAnimator.scheduleAtFixedRate(() -> {
            SwingUtilities.invokeLater(() -> {
                antsPhase = antsPhase + 1 % 5;
                if (hasSelection()) {
                    drawSelection();
                }
            });

        },0, 20, TimeUnit.MILLISECONDS);
    }

    @Override
    public void deactivate() {
        super.deactivate();

        // Need to remove selection frame when tool is no longer active
        putDownSelection();
        getCanvas().removeKeyListener(this);
        getCanvas().removeObserver(this);

        if (antsAnimation != null) {
            antsAnimation.cancel(false);
        }
    }

    public BufferedImage getSelectedImage() {
        return selectedImage;
    }

    protected void setSelectedImage(BufferedImage selectedImage) {
        this.selectedImage = selectedImage;
        drawSelection();
    }

    protected Stroke getMarchingAnts() {
        return new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, new float[]{5.0f}, antsPhase);
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

    public void cancelSelection() {
        if (hasSelection()) {
            putDownSelection();
        }
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

        Shape selectionBounds = getSelectionBounds();
        BufferedImage maskedSelection = maskSelection(getCanvas().getCanvasImage(), selectionBounds);
        BufferedImage trimmedSelection = maskedSelection.getSubimage(selectionBounds.getBounds().x, selectionBounds.getBounds().y, selectionBounds.getBounds().width, selectionBounds.getBounds().height);

        // Clear image underneath selection
        Graphics2D scratch = (Graphics2D) getCanvas().getScratchGraphics();
        scratch.setColor(Color.WHITE);
        scratch.fill(getSelectionBounds());
        scratch.dispose();

        getCanvas().commit(AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1.0f));

        selectedImage = trimmedSelection;
        drawSelection();
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
     * Draws the provided image and selection bounds ("marching ants") onto the scratch buffer at the given location.
     *
     */
    protected void drawSelection() {
        getCanvas().clearScratch();

        Graphics2D g = (Graphics2D) getCanvas().getScratchGraphics();
        g.drawImage(selectedImage, getSelectionBounds().getBounds().x, getSelectionBounds().getBounds().y, null);
        g.dispose();

        drawSelectionBounds();

        getCanvas().repaintCanvas();
    }

    protected void drawSelectionBounds() {
        Graphics2D g = (Graphics2D) getCanvas().getScratchGraphics();

        g.setStroke(getMarchingAnts());
        g.setColor(Color.BLACK);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.XOR));
        g.draw(getSelectionBounds());
        g.dispose();
    }

    /**
     * Creates a new image in which every pixel not within the given shape has been changed to clear.
     *
     * @param image The image to mask
     * @param shape The shape bounding the subimage to keep
     * @return
     */
    private BufferedImage maskSelection(BufferedImage image, Shape shape) {
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
                    break;

                case KeyEvent.VK_ESCAPE:
                    cancelSelection();
                    break;

                case KeyEvent.VK_LEFT:
                    adjustSelectionBounds(-1, 0);
                    drawSelection();
                    break;

                case KeyEvent.VK_RIGHT:
                    adjustSelectionBounds(1, 0);
                    drawSelection();
                    break;

                case KeyEvent.VK_UP:
                    adjustSelectionBounds(0, -1);
                    drawSelection();
                    break;

                case KeyEvent.VK_DOWN:
                    adjustSelectionBounds(0, 1);
                    drawSelection();
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
