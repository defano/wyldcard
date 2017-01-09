package hypercard.paint.tools;

import hypercard.paint.Transform;
import hypercard.paint.model.PaintToolType;
import hypercard.paint.utils.MathUtils;

import java.awt.*;
import java.awt.event.MouseEvent;
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
        if (hasSelection() && getSelectionBounds().contains(e.getPoint())) {
            originalImage = getSelectedImage();
            originalSelectionBounds = getSelectionBounds();

            Rectangle selectionBounds = getSelectionBounds().getBounds();
            angleOrigin = new Point(selectionBounds.x + selectionBounds.width / 2, selectionBounds.y + selectionBounds.height / 2);

        } else {
            super.mousePressed(e);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (angleOrigin != null) {
            angleDestination = e.getPoint();
            drawRotation(Math.toRadians(MathUtils.getLineAngle(angleOrigin.x, angleOrigin.y, angleDestination.x, angleDestination.y)));
        } else {
            super.mouseDragged(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (angleOrigin != null || angleDestination != null) {
            putDownSelection();
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
    public Shape getSelectionBounds() {
        return selectionBounds;
    }

    @Override
    public void adjustSelectionBounds(int xDelta, int yDelta) {
        // Nothing to do
    }

    @Override
    protected Point getSelectedImageAnchor() {
        return originalSelectionBounds == null ? getSelectionBounds().getBounds().getLocation() : originalSelectionBounds.getBounds().getLocation();
    }

    private void drawRotation(double angle) {

        setChanged();
        selectionBounds = Transform.rotateTransform(angle, originalSelectionBounds.getBounds().x + originalSelectionBounds.getBounds().width / 2, originalSelectionBounds.getBounds().y + originalSelectionBounds.getBounds().height / 2).createTransformedShape(originalSelectionBounds);
        setSelectedImage(Transform.rotate(originalImage, angle, originalSelectionBounds.getBounds().width / 2, originalSelectionBounds.getBounds().height / 2));
    }
}
