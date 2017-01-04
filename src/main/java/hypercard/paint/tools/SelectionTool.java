package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;
import hypercard.paint.patterns.HyperCardPatternFactory;
import hypercard.paint.utils.MathUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SelectionTool extends AbstractSelectionTool {

    private Rectangle selectionBounds;

    public SelectionTool() {
        super(PaintToolType.SELECTION);
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
        // Nothing to do
    }

    @Override
    public void resetSelection() {
        selectionBounds = null;
    }

    @Override
    protected Shape getSelectionBounds() {
        return selectionBounds;
    }

    @Override
    protected void adjustSelectionBounds(int xDelta, int yDelta) {
        selectionBounds.setLocation(selectionBounds.x + xDelta, selectionBounds.y + yDelta);
    }

}
