package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;
import hypercard.paint.utils.MathUtils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

public class LassoTool extends AbstractSelectionTool {

    private Path2D selectionBounds;

    public LassoTool() {
        super(PaintToolType.LASSO);
    }

    @Override
    protected void resetSelection() {
        selectionBounds = null;
    }

    @Override
    protected void defineSelectionBounds(Point initialPoint, Point currentPoint, boolean constrain) {
        if (selectionBounds == null) {
            selectionBounds = new Path2D.Double();
            selectionBounds.moveTo(initialPoint.getX(), initialPoint.getY());
        }

        selectionBounds.lineTo(currentPoint.x, currentPoint.y);
    }

    @Override
    protected void completeSelectionBounds(Point finalPoint) {
        selectionBounds.closePath();
    }

    @Override
    protected Shape getSelectionBounds() {
        return selectionBounds;
    }

    @Override
    protected void adjustSelectionBounds(int xDelta, int yDelta) {
        selectionBounds.transform(AffineTransform.getTranslateInstance(xDelta, yDelta));
    }
}
