package hypercard.paint.canvas;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Canvas extends JPanel {

    private BufferedImage image;
    private BufferedImage scratch;

    private List<CanvasObserver> observers = new ArrayList<>();

    public Canvas() {
        this(null);
    }

    public Canvas(BufferedImage initialImage) {

        if (initialImage == null) {
            image = newTransparentImage(1,1);
        } else {
            image = initialImage;
        }

        scratch = newTransparentImage(image.getWidth(), image.getHeight());

        setOpaque(false);
        setEnabled(false);
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

    protected void overlayImage(BufferedImage source, BufferedImage destination, AlphaComposite composite) {

        Graphics2D g2d = (Graphics2D) destination.getGraphics();
        g2d.setComposite(composite);
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();
    }

    private BufferedImage newTransparentImage(int width, int height) {
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f);
        Graphics2D g2d = (Graphics2D) newImage.getGraphics();
        g2d.setComposite(composite);
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, width, height);

        return newImage;
    }

    public BufferedImage getCanvasImage() {
        return image;
    }

    protected BufferedImage getScratchImage() {
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

        overlayImage(scratchImage, canvasImage, composite);
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

    /**
     * Causes the Swing framework to redraw/refresh the Canvas by calling repaint on the Canvas's parent component.
     */
    public void repaintCanvas() {
        if (getParent() != null) {
            getParent().repaint();
        }
    }

    /**
     * Adds an observer to be notified of scratch buffer commits.
     * @param observer The observer to be registered.
     */
    public void addObserver(CanvasObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes an existing observer.
     * @param observer The observer to be removed.
     * @return True if the given observer was successfully unregistered; false otherwise.
     */
    public boolean removeObserver(CanvasObserver observer) {
        return observers.remove(observer);
    }

    /**
     * Invoked by the Swing framework to render this component (i.e., draw the Canvas).
     * @param g The shared Graphics context in which the component should be rendered.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(getCanvasImage(), 0, 0, null);
        g.drawImage(getScratchImage(), 0, 0, null);
    }

    protected void fireObservers(BufferedImage committedImage, BufferedImage canvasImage) {
        for (CanvasObserver thisObserver : observers) {
            thisObserver.onCommit(this, committedImage, canvasImage);
        }
    }
}
