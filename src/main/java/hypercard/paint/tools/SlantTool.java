package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;
import hypercard.paint.utils.FlexQuadrilateral;
import hypercard.paint.utils.MathUtils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class SlantTool extends AbstractTransformTool {

    public SlantTool() {
        super(PaintToolType.SLANT);
    }

    @Override
    public void moveTopLeft(FlexQuadrilateral quadrilateral, Point newPosition) {
        int delta = newPosition.x - quadrilateral.getTopLeft().x;
        quadrilateral.getTopRight().x += delta;
        quadrilateral.getTopLeft().x = newPosition.x;

        Point p = new Point(quadrilateral.getBottomLeft().x, quadrilateral.getTopLeft().y);
        double theta = MathUtils.angleBetweenTwoPoints(quadrilateral.getBottomLeft(), p, quadrilateral.getTopLeft());
        AffineTransform transform = AffineTransform.getShearInstance(Math.tan(theta), 0);
        transform.translate(quadrilateral.getBottomLeft().x + delta, 0);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        setSelectedImage(op.filter(originalImage,null));
    }

    @Override
    public void moveTopRight(FlexQuadrilateral quadrilateral, Point newPosition) {
        quadrilateral.getTopLeft().x += newPosition.x - quadrilateral.getTopRight().x;
        quadrilateral.getTopRight().x = newPosition.x;
    }

    @Override
    public void moveBottomLeft(FlexQuadrilateral quadrilateral, Point newPosition) {
        quadrilateral.getBottomRight().x += newPosition.x - quadrilateral.getBottomLeft().x;
        quadrilateral.getBottomLeft().x = newPosition.x;
    }

    @Override
    public void moveBottomRight(FlexQuadrilateral quadrilateral, Point newPosition) {
        quadrilateral.getBottomLeft().x += newPosition.x - quadrilateral.getBottomRight().x;
        quadrilateral.getBottomRight().x = newPosition.x;
    }
}
