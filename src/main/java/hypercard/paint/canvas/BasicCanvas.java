package hypercard.paint.canvas;

import hypercard.paint.model.Provider;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BasicCanvas extends AbstractSwingCanvas implements Canvas {

    private Point imageLocation = new Point(0, 0);
    private Provider<Double> scale = new Provider<>(1.0);
    private Provider<Integer> gridSpacing = new Provider<>(1);

    private BufferedImage image;
    private BufferedImage scratch;

    public BasicCanvas() {
        this(null);
    }

    public BasicCanvas(BufferedImage initialImage) {

        if (initialImage == null) {
            image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        } else {
            image = initialImage;
        }

        scratch = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    public void clearCanvas() {
        Graphics2D g2 = (Graphics2D) getScratchImage().getGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());
        commit(AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1.0f));
    }

    protected void overlayImage(BufferedImage source, BufferedImage destination, AlphaComposite composite) {

        Graphics2D g2d = (Graphics2D) destination.getGraphics();
        g2d.setComposite(composite);
        g2d.drawImage(source, 0,0, null);
        g2d.dispose();
    }

    public BufferedImage getCanvasImage() {
        return image;
    }

    public BufferedImage getScratchImage() {
        return scratch;
    }

    @Override
    public void setCanvasImage(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void setScratchImage(BufferedImage image) {
        this.scratch = image;
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

        overlayImage(scratchImage, canvasImage, composite);
        fireObservers(this, scratchImage, canvasImage);

        clearScratch();
        invalidateCanvas();
    }

    /**
     * Creates a clean scratch buffer (replacing all pixels in the graphics context with transparent pixels).
     */
    public void clearScratch() {
        scratch = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public void setImageLocation(Point location) {
        imageLocation = location;
    }

    @Override
    public Point getImageLocation() {
        return imageLocation;
    }

    @Override
    public Provider<Double> getScaleProvider() {
        return scale;
    }

    @Override
    public void setGridSpacing(int grid) {
        this.gridSpacing.set(grid);
    }

    @Override
    public Provider<Integer> getGridSpacingProvider() {
        return gridSpacing;
    }

    @Override
    public void setScale(double scale) {
        this.scale.set(scale);
        invalidateCanvas();
    }

}
