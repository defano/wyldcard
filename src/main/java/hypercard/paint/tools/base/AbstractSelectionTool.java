package hypercard.paint.tools.base;

import hypercard.paint.utils.Transform;
import hypercard.paint.canvas.Canvas;
import hypercard.paint.model.ImmutableProvider;
import hypercard.paint.model.PaintToolType;
import hypercard.paint.model.Provider;
import hypercard.paint.tools.RotateTool;
import hypercard.paint.utils.MarchingAnts;
import hypercard.paint.utils.MarchingAntsObserver;
import hypercard.paint.utils.Geometry;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public abstract class AbstractSelectionTool extends AbstractPaintTool implements MarchingAntsObserver {

    private Provider<BufferedImage> selectedImage = new Provider<>();
    private Point initialPoint, lastPoint;
    private boolean isMovingSelection = false;
    private boolean dirty = false;

    /**
     * Reset the selection boundary to its initial, no-selection state. {@link #getSelectionOutline()} should return
     * null following a selection reset, but prior to defining a new selection via {@link #defineSelectionBounds(Point, Point, boolean)}
     */
    public abstract void resetSelection();

    public abstract void setSelectionBounds(Rectangle bounds);

    public abstract void defineSelectionBounds(Point initialPoint, Point currentPoint, boolean constrain);

    public abstract void completeSelectionBounds(Point finalPoint);

    public abstract Shape getSelectionOutline();

    public abstract void adjustSelectionBounds(int xDelta, int yDelta);

    public AbstractSelectionTool(PaintToolType type) {
        super(type);
    }

    @Override
    public void mouseMoved(MouseEvent e, int scaleX, int scaleY) {
        if (hasSelectionBounds() && getSelectionOutline().contains(new Point(scaleX, scaleY))) {
            setToolCursor(Cursor.getDefaultCursor());
        } else {
            setToolCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        }
    }

    @Override
    public void mousePressed(MouseEvent e, int scaleX, int scaleY) {
        isMovingSelection = getSelectionOutline() != null && getSelectionOutline().contains(new Point(scaleX, scaleY));

        // User clicked inside selection bounds; start moving selection
        if (isMovingSelection) {
            lastPoint = new Point(scaleX, scaleY);
        }

        // User clicked outside current selection bounds
        else {
            if (isDirty()) {
                finishSelection();
            } else {
                clearSelection();
            }

            initialPoint = new Point(scaleX, scaleY);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e, int scaleX, int scaleY) {

        // User is moving an existing selection
        if (isMovingSelection) {
            setDirty();
            adjustSelectionBounds(scaleX - lastPoint.x, scaleY - lastPoint.y);
            drawSelection();
            lastPoint = new Point(scaleX, scaleY);
        }

        // User is defining a new selection rectangle
        else {
            defineSelectionBounds(initialPoint, Geometry.pointWithinBounds(new Point(scaleX, scaleY), getCanvas().getBounds()), e.isShiftDown());

            getCanvas().clearScratch();
            drawSelectionOutline();
            getCanvas().invalidateCanvas();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e, int scaleX, int scaleY) {
        // User released mouse after defining a selection
        if (!hasSelection() && hasSelectionBounds()) {
            completeSelectionBounds(new Point(scaleX, scaleY));
            getSelectionFromCanvas();
        }
    }

    @Override
    public void activate(Canvas canvas) {
        super.activate(canvas);

        getCanvas().addObserver(this);
        MarchingAnts.getInstance().addObserver(this);
    }

    @Override
    public void deactivate() {
        super.deactivate();

        // Need to remove selection frame when tool is no longer active
        finishSelection();

        getCanvas().removeObserver(this);
        MarchingAnts.getInstance().removeObserver(this);
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

    public void flipVertical() {
        transformSelection(Transform.flipVerticalTransform(selectedImage.get().getHeight()));
    }

    public void transformSelection(AffineTransform transform) {
        if (hasSelection()) {
            setDirty();

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

    /**
     * Clears the current selection frame (removes any "marching ants" from the canvas), but does not "erase" the
     * selected image, nor does it commit the selected image.
     */
    public void clearSelection() {
        selectedImage.set(null);
        dirty = false;
        resetSelection();

        getCanvas().clearScratch();
        getCanvas().invalidateCanvas();
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
        return getSelectionOutline() != null && getSelectionOutline().getBounds().width > 0 && getSelectionOutline().getBounds().height > 0;
    }

    /**
     * Make the canvas image bounded by the given selection rectangle the current selected image.
     */
    private void getSelectionFromCanvas() {
        getCanvas().clearScratch();

        Shape selectionBounds = getSelectionOutline();
        BufferedImage maskedSelection = maskSelection(getCanvas().getCanvasImage(), selectionBounds);
        BufferedImage trimmedSelection = maskedSelection.getSubimage(selectionBounds.getBounds().x, selectionBounds.getBounds().y, selectionBounds.getBounds().width, selectionBounds.getBounds().height);

        selectedImage.set(trimmedSelection);
        drawSelection();
    }

    /**
     * Determines the location (top-left x,y coordinate) of the selection outline.
     * @return
     */
    private Point getSelectionLocation() {
        if (!hasSelection()) {
            return null;
        }

        return getSelectionOutline().getBounds().getLocation();
    }

    /**
     * Removes the image bounded by the selection outline from the canvas by filling bounded pixels with
     * fully transparent pixels.
     */
    private void eraseSelectionFromCanvas() {
        getCanvas().clearScratch();

        // Clear image underneath selection
        Graphics2D scratch = (Graphics2D) getCanvas().getScratchImage().getGraphics();
        scratch.setColor(Color.WHITE);
        scratch.fill(getSelectionOutline());
        scratch.dispose();

        getCanvas().commit(AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1.0f));
        drawSelection();
    }

    /**
     * Drops the selected image onto the canvas (committing the change) and clears the selection outline. This has the
     * effect of completing a select-and-move operation.
     */
    protected void finishSelection() {

        if (hasSelection()) {
            getCanvas().clearScratch();

            Graphics2D g2d = (Graphics2D) getCanvas().getScratchImage().getGraphics();
            g2d.drawImage(selectedImage.get(), getSelectedImageLocation().x, getSelectedImageLocation().y, null);
            g2d.dispose();

            getCanvas().commit();
            clearSelection();
        }
    }

    /**
     * Draws the provided image and selection frame ("marching ants") onto the scratch buffer at the given location.
     */
    protected void drawSelection() {
        getCanvas().clearScratch();

        Graphics2D g = (Graphics2D) getCanvas().getScratchImage().getGraphics();
        g.drawImage(selectedImage.get(), getSelectedImageLocation().x, getSelectedImageLocation().y, null);
        g.dispose();

        drawSelectionOutline();

        getCanvas().invalidateCanvas();
    }

    /**
     * Returns the location (top-left x,y coordinates) on the canvas where the selected image should be drawn.
     * Typically, this is the location of the selection shape.
     *
     * However, for tools that mutate the selection shape (i.e., {@link RotateTool}), this location may need to be
     * adjusted to account for changes to the selection shape's bounds.
     *
     * @return The x,y coordinate where the selected image should be drawn on the canvas.
     */
    protected Point getSelectedImageLocation() {
        return getSelectionOutline().getBounds().getLocation();
    }

    /**
     * Renders the selection outline (marching ants) on the canvas.
     */
    protected void drawSelectionOutline() {
        Graphics2D g = (Graphics2D) getCanvas().getScratchImage().getGraphics();

        g.setStroke(MarchingAnts.getInstance().getMarchingAnts());
        g.setColor(Color.BLACK);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.XOR));
        g.draw(getSelectionOutline());
        g.dispose();
    }

    /**
     * Marks the selection as having been mutated (either by transformation or movement).
     */
    protected void setDirty() {

        // First time we attempt to modify the selection, clear it from the canvas (so that we don't duplicate it)
        if (!dirty) {
            eraseSelectionFromCanvas();
        }

        dirty = true;
    }

    /**
     * Determines if the current selection has been changed or moved in any way since the selection outline was
     * defined.
     *
     * @return True if the selection was changed, false otherwise.
     */
    protected boolean isDirty() {
        return dirty;
    }

    /**
     * Creates a new image in which every pixel not within the given shape has been changed to fully transparent.
     *
     * @param image The image to mask
     * @param mask The shape bounding the subimage to keep
     * @return
     */
    private BufferedImage maskSelection(BufferedImage image, Shape mask) {
        BufferedImage subimage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        int clearPixel = new Color(0, 0, 0, 0).getRGB();

        for (int y = 0; y < image.getRaster().getHeight(); y++) {
            for (int x = 0; x < image.getRaster().getWidth(); x++) {
                if (x > image.getWidth() || y > image.getHeight()) continue;

                if (mask.contains(x, y)) {
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
                    setDirty();
                    clearSelection();
                    break;

                case KeyEvent.VK_ESCAPE:
                    finishSelection();
                    break;

                case KeyEvent.VK_LEFT:
                    setDirty();
                    adjustSelectionBounds(-1, 0);
                    drawSelection();
                    break;

                case KeyEvent.VK_RIGHT:
                    setDirty();
                    adjustSelectionBounds(1, 0);
                    drawSelection();
                    break;

                case KeyEvent.VK_UP:
                    setDirty();
                    adjustSelectionBounds(0, -1);
                    drawSelection();
                    break;

                case KeyEvent.VK_DOWN:
                    setDirty();
                    adjustSelectionBounds(0, 1);
                    drawSelection();
                    break;
            }
        }
    }

    @Override
    public void onAntsMoved() {
        if (hasSelection()) {
            drawSelection();
        }
    }
}
