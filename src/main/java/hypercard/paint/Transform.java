package hypercard.paint;

import hypercard.paint.utils.FlexQuadrilateral;
import hypercard.paint.utils.MathUtils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Transform {

    public static BufferedImage slant(BufferedImage image, FlexQuadrilateral quadrilateral, int xTranslation) {
        Point p = new Point(quadrilateral.getBottomLeft().x, quadrilateral.getTopLeft().y);
        double theta = MathUtils.angleBetweenTwoPoints(quadrilateral.getBottomLeft(), p, quadrilateral.getTopLeft());

        AffineTransform transform = AffineTransform.getTranslateInstance(xTranslation, 0);
        transform.shear(Math.tan(theta), 0);

        transform.translate(xTranslation,0);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(image ,null);
    }

    public static BufferedImage rotate(BufferedImage image, double theta, int anchorX, int anchorY) {
        return new AffineTransformOp(rotateTransform(theta, anchorX, anchorY),AffineTransformOp.TYPE_BILINEAR).filter(image, null);
    }

    public static BufferedImage transform(BufferedImage image, AffineTransform transform) {
        return new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR).filter(image, null);
    }

    public static AffineTransform rotateLeft(int width, int height) {
        AffineTransform transform = AffineTransform.getTranslateInstance(height / 2, width / 2);
        transform.quadrantRotate(3);
        transform.translate(-width / 2,-height / 2);
        return transform;
    }

    public static AffineTransform rotateRight(int width, int height) {
        AffineTransform transform = AffineTransform.getTranslateInstance(height / 2, width / 2);
        transform.quadrantRotate(1);
        transform.translate(-width / 2,-height / 2);
        return transform;
    }

    public static AffineTransform flipHorizontalTransform(int width) {
        AffineTransform transform = AffineTransform.getScaleInstance(-1, 1);
        transform.translate(-width, 0);
        return transform;
    }

    public static AffineTransform flipVerticalTransform(int height) {
        AffineTransform transform = AffineTransform.getScaleInstance(1, -1);
        transform.translate(0, -height);
        return transform;
    }

    public static AffineTransform rotateTransform(double radians, int centerX, int centerY) {
        return AffineTransform.getRotateInstance(radians, centerX, centerY);
    }

}
