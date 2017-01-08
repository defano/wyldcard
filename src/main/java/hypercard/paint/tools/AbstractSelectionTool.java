package hypercard.paint.tools;

import hypercard.paint.Transform;
import hypercard.paint.canvas.Canvas;
import hypercard.paint.model.ImmutableProvider;
import hypercard.paint.model.PaintToolType;
import hypercard.paint.model.Provider;
import hypercard.paint.utils.MathUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractSelectionTool extends AbstractPaintTool implements KeyListener {

    private Provider<BufferedImage> selectedImage = new Provider<>();
    private Point initialPoint, lastPoint;
    private boolean isMovingSelection = false;
    private boolean selectionChanged = false;

    private int antsPhase;
    private ScheduledExecutorService antsAnimator = Executors.newSingleThreadScheduledExecutor();
    private Future antsAnimation;

    public abstract void resetSelection();

    public abstract void setSelectionBounds(Rectangle bounds);

    public abstract void defineSelectionBounds(Point initialPoint, Point currentPoint, boolean constrain);

    public abstract void completeSelectionBounds(Point finalPoint);

    public abstract Shape getSelectionBounds();

    public abstract void adjustSelectionBounds(int xDelta, int yDelta);

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
            if (hasChanged()) {
                putDownSelection();
            } else {
                clearSelection();
            }

            initialPoint = e.getPoint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        // User is moving an existing selection
        if (isMovingSelection) {
            setChanged();
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
            getSelectionFromCanvas();
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

        }, 0, 20, TimeUnit.MILLISECONDS);
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

    public void rotateLeft() {
        transformSelection(Transform.rotateLeft(selectedImage.get().getWidth(), selectedImage.get().getHeight()));
    }

    public void rotateRight() {
        transformSelection(Transform.rotateRight(selectedImage.get().getWidth(), selectedImage.get().getHeight()));
    }

    public void flipHorizontal() {
        transformSelection(Transform.flipHorizontalTransform(selectedImage.get().getWidth()));
    }

    public void flipVerical() {
        transformSelection(Transform.flipVerticalTransform(selectedImage.get().getHeight()));
    }

    public void transformSelection(AffineTransform transform) {
        if (hasSelection()) {
            setChanged();

            // Get the original location of the selection
            Point originalLocation = getSelectionLocation();

            // Transform the selected image
            selectedImage.set(Transform.transform(selectedImage.get(), transform));

            // Relocate the image to its original location
            Rectangle newBounds = selectedImage.get().getRaster().getBounds();
            newBounds.setLocation(originalLocation);
            setSelectionBounds(newBounds);

            drawSelection();
        }
    }

    public ImmutableProvider<BufferedImage> getSelectedImageProvider() {
        return ImmutableProvider.from(selectedImage);
    }

    public BufferedImage getSelectedImage() {
        return selectedImage.get();
    }

    protected void setSelectedImage(BufferedImage selectedImage) {
        this.selectedImage.set(selectedImage);
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
        selectedImage.set(null);
        selectionChanged = false;
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
        return hasSelectionBounds() && selectedImage.get() != null;
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
     */
    private void getSelectionFromCanvas() {
        getCanvas().clearScratch();

        Shape selectionBounds = getSelectionBounds();
        BufferedImage maskedSelection = maskSelection(getCanvas().getCanvasImage(), selectionBounds);
        BufferedImage trimmedSelection = maskedSelection.getSubimage(selectionBounds.getBounds().x, selectionBounds.getBounds().y, selectionBounds.getBounds().width, selectionBounds.getBounds().height);

        selectedImage.set(trimmedSelection);
        drawSelection();
    }

    private Point getSelectionLocation() {
        if (!hasSelection()) {
            return null;
        }

        return getSelectionBounds().getBounds().getLocation();
    }

    public void eraseSelectionFromCanvas() {
        getCanvas().clearScratch();

        // Clear image underneath selection
        Graphics2D scratch = (Graphics2D) getCanvas().getScratchGraphics();
        scratch.setColor(Color.WHITE);
        scratch.fill(getSelectionBounds());
        scratch.dispose();

        getCanvas().commit(AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1.0f));
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
            g2d.drawImage(selectedImage.get(), getSelectionBounds().getBounds().x, getSelectionBounds().getBounds().y, null);
            g2d.dispose();

            getCanvas().commit();
            clearSelection();
        }
    }

    /**
     * Draws the provided image and selection bounds ("marching ants") onto the scratch buffer at the given location.
     */
    protected void drawSelection() {
        getCanvas().clearScratch();

        Graphics2D g = (Graphics2D) getCanvas().getScratchGraphics();
        g.drawImage(selectedImage.get(), getSelectionBounds().getBounds().x, getSelectionBounds().getBounds().y, null);
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

    protected void setChanged() {

        // First time we attempt to modify the selection, clear it from the canvas (so that we don't duplicate it)
        if (!selectionChanged) {
            eraseSelectionFromCanvas();
        }

        selectionChanged = true;
    }

    protected boolean hasChanged() {
        return selectionChanged;
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

        int clearPixel = new Color(0, 0, 0, 0).getRGB();

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
        if (hasSelection() && committedElement == null) {
            clearSelection();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (hasSelection()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_DELETE:
                case KeyEvent.VK_BACK_SPACE:
                    setChanged();
                    clearSelection();
                    break;

                case KeyEvent.VK_ESCAPE:
                    cancelSelection();
                    break;

                case KeyEvent.VK_LEFT:
                    setChanged();
                    adjustSelectionBounds(-1, 0);
                    drawSelection();
                    break;

                case KeyEvent.VK_RIGHT:
                    setChanged();
                    adjustSelectionBounds(1, 0);
                    drawSelection();
                    break;

                case KeyEvent.VK_UP:
                    setChanged();
                    adjustSelectionBounds(0, -1);
                    drawSelection();
                    break;

                case KeyEvent.VK_DOWN:
                    setChanged();
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
