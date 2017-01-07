package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;
import hypercard.paint.utils.FlexQuadrilateral;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public abstract class AbstractTransformTool extends AbstractSelectionTool {

    private final static int HANDLE_SIZE = 8;

    private boolean selectionComplete = false;

    protected BufferedImage originalImage;
    private Rectangle selectionBounds;
    private FlexQuadrilateral transformBounds;

    private Rectangle topLeftHandle, topRightHandle, bottomRightHandle, bottomLeftHandle;
    private boolean dragTopLeft, dragTopRight, dragBottomRight, dragBottomLeft;

    public abstract void moveTopLeft(FlexQuadrilateral quadrilateral, Point newPosition);
    public abstract void moveTopRight(FlexQuadrilateral quadrilateral, Point newPosition);
    public abstract void moveBottomLeft(FlexQuadrilateral quadrilateral, Point newPosition);
    public abstract void moveBottomRight(FlexQuadrilateral quadrilateral, Point newPosition);

    public AbstractTransformTool(PaintToolType type) {
        super(type);
    }

    public AbstractTransformTool(PaintToolType type, Rectangle selectionBounds) {
        super(type);
        this.transformBounds = new FlexQuadrilateral(selectionBounds);
        selectionComplete = true;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!selectionComplete) {
            super.mousePressed(e);
        }

        else {
            dragTopLeft = topLeftHandle.contains(e.getPoint());
            dragTopRight = topRightHandle.contains(e.getPoint());
            dragBottomLeft = bottomLeftHandle.contains(e.getPoint());
            dragBottomRight = bottomRightHandle.contains(e.getPoint());
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!selectionComplete) {
            super.mouseDragged(e);
        }

        else {
            if (dragTopLeft) {
                moveTopLeft(transformBounds, e.getPoint());
            } else if (dragTopRight) {
                moveTopRight(transformBounds, e.getPoint());
            } else if (dragBottomLeft) {
                moveBottomLeft(transformBounds, e.getPoint());
            } else if (dragBottomRight) {
                moveBottomRight(transformBounds, e.getPoint());
            }

            drawSelection();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!selectionComplete) {
            super.mouseReleased(e);
            originalImage = getSelectedImage();
        }
    }

    @Override
    protected void defineSelectionBounds(Point initialPoint, Point currentPoint, boolean constrain) {
        selectionBounds = new Rectangle(initialPoint);
        selectionBounds.add(currentPoint);

        int width = selectionBounds.width;
        int height = selectionBounds.height;

        if (constrain) {
            width = height = Math.max(width, height);
        }

        selectionBounds = new Rectangle(selectionBounds.x, selectionBounds.y, width, height);
    }

    @Override
    protected void completeSelectionBounds(Point finalPoint) {
        selectionComplete = true;
        transformBounds = new FlexQuadrilateral(selectionBounds);
    }

    @Override
    public void resetSelection() {
        selectionBounds = null;
        transformBounds = null;

        topLeftHandle = topRightHandle = bottomLeftHandle = bottomRightHandle = null;
    }

    @Override
    protected Shape getSelectionBounds() {
        return transformBounds != null ? transformBounds.getShape() : selectionBounds;
    }

    @Override
    protected void adjustSelectionBounds(int xDelta, int yDelta) {
        selectionBounds.setLocation(selectionBounds.x + xDelta, selectionBounds.y + yDelta);
    }

    protected void drawSelectionBounds() {
        super.drawSelectionBounds();

        if (hasSelection()) {
            drawDragHandles();
        }
    }

    private void drawDragHandles() {
        Graphics2D g = (Graphics2D) getCanvas().getScratchGraphics();
        g.setPaint(Color.BLACK);

        topLeftHandle = new Rectangle(transformBounds.getTopLeft().x, transformBounds.getTopLeft().y, HANDLE_SIZE, HANDLE_SIZE);
        topRightHandle = new Rectangle(transformBounds.getTopRight().x - HANDLE_SIZE, transformBounds.getTopRight().y, HANDLE_SIZE, HANDLE_SIZE);
        bottomRightHandle = new Rectangle(transformBounds.getBottomRight().x - HANDLE_SIZE, transformBounds.getBottomRight().y - HANDLE_SIZE, HANDLE_SIZE, HANDLE_SIZE);
        bottomLeftHandle = new Rectangle(transformBounds.getBottomLeft().x, transformBounds.getBottomLeft().y - HANDLE_SIZE, HANDLE_SIZE, HANDLE_SIZE);

        g.fill(topLeftHandle);
        g.fill(topRightHandle);
        g.fill(bottomRightHandle);
        g.fill(bottomLeftHandle);

        g.dispose();
    }

}
