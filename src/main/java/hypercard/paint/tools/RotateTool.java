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
    public void mousePressed(MouseEvent e) {
        if (hasSelection() && getSelectionOutline().contains(e.getPoint())) {
            originalImage = square(getSelectedImage());
            originalSelectionBounds = getSelectionOutline();

            Rectangle selectionBounds = getSelectionOutline().getBounds();
            angleOrigin = new Point(selectionBounds.x + selectionBounds.width / 2, selectionBounds.y + selectionBounds.height / 2);
        } else {
            super.mousePressed(e);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (angleOrigin != null) {
            angleDestination = e.getPoint();
            drawRotation(Math.toRadians(Geometry.getLineAngle(angleOrigin.x, angleOrigin.y, angleDestination.x, angleDestination.y)));
        } else {
            super.mouseDragged(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (angleOrigin != null || angleDestination != null) {
            finishSelection();
        } else {
            super.mouseReleased(e);
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
     * Square the bounds of the given image so that the resulting image has equal height and width (whichever is
     * the larger) and translate the original graphic such that whichever dimension was enlarged, the original image is
     * centered in that dimension.
     *
     * For example, if the provided image is 10x30, the resulting image will be 30x30 with the contents of the original
     * drawn at 15,0 inside of it.
     *
     * @param image
     * @return
     */
    private BufferedImage square(BufferedImage image) {
        int maxDimension = Math.max(image.getHeight(), image.getWidth());
        int deltaX = 0;
        int deltaY = 0;

        if (image.getHeight() > image.getWidth()) {
            deltaX = image.getHeight() - image.getWidth();
        } else {
            deltaY = image.getWidth() - image.getHeight();
        }

        BufferedImage enlarged = new BufferedImage(maxDimension, maxDimension, image.getType());

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
