package hypercard.paint.canvas;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class BasicCanvas extends SwingCanvas {

    private BufferedImage image;
    private BufferedImage scratch;

    public BasicCanvas() {
        this(null);
    }

    public BasicCanvas(BufferedImage initialImage) {

        if (initialImage == null) {
            image = newTransparentImage(1,1);
        } else {
            image = initialImage;
        }

        scratch = newTransparentImage(image.getWidth(), image.getHeight());

        setOpaque(false);
        setEnabled(true);
        setFocusable(true);
        setLayout(null);
    }

    public void setSize(int width, int height) {
        super.setSize(width, height);

        // Don't truncate image if canvas shrinks, but do grow it
        if (width < image.getWidth()) {
            width = image.getWidth();
        }

        if (height < image.getHeight()) {
            height = image.getHeight();
        }

        BufferedImage newScratch = newTransparentImage(width, height);
        Graphics newScratchGraphics = newScratch.getGraphics();
        newScratchGraphics.drawImage(getScratchImage(), 0, 0, null);
        scratch = newScratch;

        BufferedImage newImage = newTransparentImage(width, height);
        Graphics newImageGraphics = newImage.getGraphics();
        newImageGraphics.drawImage(getCanvasImage(), 0, 0, null);
        image = newImage;

        newScratchGraphics.dispose();
        newImageGraphics.dispose();

        repaintCanvas();
    }

    public void clearCanvas() {
        Graphics2D g2 = (Graphics2D) getScratchGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());
        commit(AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1.0f));
    }

    public BufferedImage getCanvasImage() {
        return image;
    }

    protected void overlayImage(int x, int y, BufferedImage source, BufferedImage destination, AlphaComposite composite) {

        Graphics2D g2d = (Graphics2D) destination.getGraphics();
        g2d.setComposite(composite);
        g2d.drawImage(source, x, y, null);
        g2d.dispose();
    }

    private BufferedImage newTransparentImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public BufferedImage getScratchImage() {
        return scratch;
    }

    public void commit() {
        commit(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    /**
     * Copies the image contents of the scratch buffer to the Canvas' image.
     */
    public void commit(AlphaComposite composite) {
        BufferedImage scratchImage = getScratchImage();
        BufferedImage canvasImage = getCanvasImage();

        overlayImage(0, 0, scratchImage, canvasImage, composite);
        fireObservers(scratchImage, canvasImage);

        clearScratch();
        repaintCanvas();
    }

    /**
     * Creates a clean scratch buffer (replacing all pixels in the graphics context with transparent pixels).
     */
    public void clearScratch() {
        scratch = newTransparentImage(getWidth(), getHeight());
    }

    /**
     * Gets the Graphics context associated with the scratch buffer. Paint tools should "draw" into this temporary
     * context, then, once the user is ready to accept the change, invoke {@link #commit()} to apply changes to this
     * context to the Canvas.
     *
     * This design allows paint tools to draw on-screen temporarily without modifying the underline graphic. For example,
     * to show "marching ants" as part of a selection tool, or to let the user drag-size a shape and apply only the
     * desired size shape to the Canvas when they release the mouse.
     *
     * @return The scratch Graphics context.
     */
    public Graphics getScratchGraphics() {
        return getScratchImage().getGraphics();
    }

}