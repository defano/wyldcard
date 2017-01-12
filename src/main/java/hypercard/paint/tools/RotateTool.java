package hypercard.paint.tools;

import hypercard.paint.Transform;
import hypercard.paint.model.PaintToolType;
import hypercard.paint.utils.Geometry;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class RotateTool extends AbstractSelectionTool {

    private Point angleOrigin;
    private Point angleDestination;

    private Shape selectionBounds;
    private Shape originalSelectionBounds;
    private BufferedImage originalImage;

    public RotateTool() {
        super(PaintToolType.ROTATE);
    }

    @Override
    public void mousePressed(MouseEvent e, int scaleX, int scaleY) {
        if (hasSelection() && getSelectionOutline().contains(e.getPoint())) {
            originalImage = square(getSelectedImage());
            originalSelectionBounds = getSelectionOutline();

            Rectangle selectionBounds = getSelectionOutline().getBounds();
            angleOrigin = new Point(selectionBounds.x + selectionBounds.width / 2, selectionBounds.y + selectionBounds.height / 2);
        } else {
            super.mousePressed(e, scaleX, scaleY);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e, int scaleX, int scaleY) {
        if (angleOrigin != null) {
            angleDestination = e.getPoint();
            drawRotation(Math.toRadians(Geometry.getLineAngle(angleOrigin.x, angleOrigin.y, angleDestination.x, angleDestination.y)));
        } else {
            super.mouseDragged(e, scaleX, scaleY);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e, int scaleX, int scaleY) {
        if (angleOrigin != null || angleDestination != null) {
            finishSelection();
        } else {
            super.mouseReleased(e, scaleX, scaleY);
        }
    }

    @Override
    public void resetSelection() {
        selectionBounds = null;
        originalSelectionBounds = null;
        angleOrigin = null;
        angleDestination = null;
        originalImage = null;
    }

    @Override
    public void setSelectionBounds(Rectangle bounds) {
        selectionBounds = bounds;
    }

    @Override
    public void defineSelectionBounds(Point initialPoint, Point currentPoint, boolean constrain) {
        Rectangle selectionRectangle = new Rectangle(initialPoint);
        selectionRectangle.add(currentPoint);

        selectionBounds = selectionRectangle;
    }

    @Override
    public void completeSelectionBounds(Point finalPoint) {
        // Nothing to do
    }

    @Override
    public Shape getSelectionOutline() {
        return selectionBounds;
    }

    @Override
    public void adjustSelectionBounds(int xDelta, int yDelta) {
        // Nothing to do
    }

    @Override
    protected Point getSelectedImageLocation() {
        if (angleDestination == null) {
            return getSelectionOutline().getBounds().getLocation();
        } else {
            Rectangle enlargedBounds = originalImage.getRaster().getBounds();
            Geometry.center(enlargedBounds, originalSelectionBounds.getBounds());
            return enlargedBounds.getLocation();
        }
    }

    /**
     * Square the bounds of a given image so that the resulting image has an equal height and width whose value is
     * equal to the diagonal of the original image. The original image will be drawn centered inside the enlarged bounds
     * of the result.
     * <p>
     * For example, if the provided image is 10x30, the resulting image will be 32x32 with the contents of the original
     * drawn at (16,1) inside of it.
     *
     * @param image
     * @return
     */
    private BufferedImage square(BufferedImage image) {
        int diagonal = (int) Math.ceil(Math.sqrt(image.getHeight() * image.getHeight() + image.getWidth() * image.getWidth()));

        int deltaX = diagonal - image.getWidth();
        int deltaY = diagonal - image.getHeight();

        BufferedImage enlarged = new BufferedImage(diagonal, diagonal, image.getType());

        Graphics2D g = enlarged.createGraphics();
        g.drawImage(image, AffineTransform.getTranslateInstance(deltaX / 2, deltaY / 2), null);
        g.dispose();

        return enlarged;
    }

    private void drawRotation(double angle) {
        setDirty();

        selectionBounds = Transform.rotateTransform(angle, originalSelectionBounds.getBounds().x + originalSelectionBounds.getBounds().width / 2, originalSelectionBounds.getBounds().y + originalSelectionBounds.getBounds().height / 2).createTransformedShape(originalSelectionBounds);
        setSelectedImage(Transform.rotate(originalImage, angle, originalImage.getWidth() / 2, originalImage.getHeight() / 2));
    }
}
