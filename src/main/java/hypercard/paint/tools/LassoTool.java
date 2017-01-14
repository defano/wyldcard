package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;
import hypercard.paint.tools.base.AbstractSelectionTool;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

public class LassoTool extends AbstractSelectionTool {

    private Path2D selectionBounds;

    public LassoTool() {
        super(PaintToolType.LASSO);
    }

    @Override
    public void resetSelection() {
        selectionBounds = null;
    }

    @Override
    public void setSelectionBounds(Rectangle bounds) {
        selectionBounds = new Path2D.Double(bounds);
    }

    @Override
    public void defineSelectionBounds(Point initialPoint, Point currentPoint, boolean constrain) {
        if (selectionBounds == null) {
            selectionBounds = new Path2D.Double();
            selectionBounds.moveTo(initialPoint.getX(), initialPoint.getY());
        }

        selectionBounds.lineTo(currentPoint.x, currentPoint.y);
    }

    @Override
    public void completeSelectionBounds(Point finalPoint) {
        selectionBounds.closePath();
    }

    @Override
    public Shape getSelectionOutline() {
        return selectionBounds;
    }

    @Override
    public void adjustSelectionBounds(int xDelta, int yDelta) {
        selectionBounds.transform(AffineTransform.getTranslateInstance(xDelta, yDelta));
    }
}
