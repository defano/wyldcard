package hypercard.paint.tools;

import hypercard.paint.Transform;
import hypercard.paint.model.PaintToolType;
import hypercard.paint.tools.base.AbstractSelectionTool;
import hypercard.paint.utils.Geometry;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class RotateTool extends AbstractSelectionTool {

    private Point centerpoint;
    private Point dragLocation;

    private BufferedImage originalImage;

    private Shape selectionBounds;
    private Shape originalSelectionBounds;
    private Shape dragHandle;
    private Shape originalDragHandle;

    private boolean rotating = false;

    public RotateTool() {
        super(PaintToolType.ROTATE);
    }

    @Override
    public void mousePressed(MouseEvent e, int scaleX, int scaleY) {

        // User clicked outside the selection after making a rotation change
        if (isDirty() && !selectionBounds.contains(e.getPoint())) {
            rotating = false;
            finishSelection();
        }

        // User clicked inside drag handle
        else if (hasSelection() && dragHandle.contains(new Point(scaleX, scaleY))) {
            rotating = true;

            if (centerpoint == null) {
                originalImage = square(getSelectedImage());
                originalSelectionBounds = getSelectionOutline();

                Rectangle selectionBounds = getSelectionOutline().getBounds();
                centerpoint = new Point(selectionBounds.x + selectionBounds.width / 2, selectionBounds.y + selectionBounds.height / 2);
            }
        }

        // None of the above; delegate to superclass
        else {
            rotating = false;
            super.mousePressed(e, scaleX, scaleY);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e, int scaleX, int scaleY) {

        if (rotating) {
            setDirty();     // Mutating the selected image

            // Calculate the rotation angle
            dragLocation = new Point(scaleX, scaleY);
            double angle = Math.toRadians(Geometry.getLineAngle(centerpoint.x, centerpoint.y, dragLocation.x, dragLocation.y));

            // Rotate the marching ants and drag handle
            selectionBounds = Transform.rotateTransform(angle, originalSelectionBounds.getBounds().x + originalSelectionBounds.getBounds().width / 2, originalSelectionBounds.getBounds().y + originalSelectionBounds.getBounds().height / 2).createTransformedShape(originalSelectionBounds);
            dragHandle = Transform.rotateTransform(angle, originalSelectionBounds.getBounds().x + originalSelectionBounds.getBounds().width / 2, originalSelectionBounds.getBounds().y + originalSelectionBounds.getBounds().height / 2).createTransformedShape(originalDragHandle);

            // Rotate the selected canvas image
            setSelectedImage(Transform.rotate(originalImage, angle, originalImage.getWidth() / 2, originalImage.getHeight() / 2));
        }

        else {
            super.mouseDragged(e, scaleX, scaleY);
        }
    }

    @Override
    public void resetSelection() {
        selectionBounds = null;
        originalSelectionBounds = null;
        centerpoint = null;
        dragLocation = null;
        originalImage = null;
    }

    @Override
    public void setSelectionBounds(Rectangle bounds) {
        selectionBounds = bounds;
    }

    @Override
    public void defineSelectionBounds(Point initialPoint, Point currentPoint, boolean constrain) {
        int handleSize = 8;

        Rectangle selectionRectangle = new Rectangle(initialPoint);
        selectionRectangle.add(currentPoint);

        selectionBounds = selectionRectangle;
        originalDragHandle = dragHandle = new Rectangle(selectionRectangle.x + selectionRectangle.width - handleSize, selectionRectangle.y + selectionRectangle.height / 2 - handleSize / 2, handleSize, handleSize);
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
        // Nothing to do; user can't move selection
    }

    @Override
    protected Point getSelectedImageLocation() {

        if (dragLocation == null) {
            return getSelectionOutline().getBounds().getLocation();
        }

        else {
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

    @Override
    protected void drawSelection() {
        super.drawSelection();

        // Draw the drag handle on the selection
        Graphics2D g = (Graphics2D) getCanvas().getScratchGraphics();
        g.setColor(Color.black);
        g.fill(dragHandle);
        g.dispose();
    }

}
