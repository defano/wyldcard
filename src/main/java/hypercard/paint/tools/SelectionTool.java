package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;

import java.awt.*;

public class SelectionTool extends AbstractSelectionTool {

    private Rectangle selectionBounds;

    public SelectionTool() {
        super(PaintToolType.SELECTION);
    }

    @Override
    public void defineSelectionBounds(Point initialPoint, Point currentPoint, boolean constrain) {
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
    public void completeSelectionBounds(Point finalPoint) {
        // Nothing to do
    }

    @Override
    public void resetSelection() {
        selectionBounds = null;
    }

    @Override
    public void setSelectionBounds(Rectangle bounds) {
        selectionBounds = bounds;
    }

    @Override
    public Shape getSelectionOutline() {
        return selectionBounds;
    }

    @Override
    public void adjustSelectionBounds(int xDelta, int yDelta) {
        selectionBounds.setLocation(selectionBounds.x + xDelta, selectionBounds.y + yDelta);
    }

}
