package hypercard.paint.canvas;

import java.awt.image.BufferedImage;

public interface CanvasCommitObserver {
    /**
     * Fires when an new shape or image is committed from scratch onto the canvas.
     *
     * @param canvas The canvas on which the commit is occurring.
     * @param committedElement An image representing just the change being committed.
     * @param canvasImage The resulting canvas image (including the committed change)
     */
    void onCommit(Canvas canvas, BufferedImage committedElement, BufferedImage canvasImage);
}
