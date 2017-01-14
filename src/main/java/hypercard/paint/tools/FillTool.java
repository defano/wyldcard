package hypercard.paint.tools;

import hypercard.paint.model.PaintToolType;
import hypercard.paint.tools.base.AbstractPaintTool;
import hypercard.paint.utils.FloodFill;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class FillTool extends AbstractPaintTool {

    public FillTool() {
        super(PaintToolType.FILL);
    }

    @Override
    public void mouseClicked(MouseEvent e, int scaleX, int scaleY) {

        BufferedImage canvasImage = getCanvas().getCanvasImage();
        Rectangle canvasBounds = getCanvas().getBounds();
        Paint fillPaint = getFillPaint();

        // Nothing to do if no fill is specified
        if (fillPaint == null) {
            return;
        }

        getCanvas().clearScratch();

        FloodFill.floodFill(scaleX, scaleY, canvasBounds, p -> {
            int rgb = getFillPixel(p.x, p.y, fillPaint);
            getCanvas().getScratchImage().setRGB(p.x, p.y, rgb);
        }, (Point point) -> {
            Color canvasPixel = new Color(canvasImage.getRGB(point.x, point.y), true);
            Color scratchPixel = new Color(getCanvas().getScratchImage().getRGB(point.x, point.y), true);
            return canvasPixel.getAlpha() == 0 && scratchPixel.getAlpha() == 0;
        });

        getCanvas().commit();
        getCanvas().repaintCanvas();
    }

    private int getFillPixel(int x, int y, Paint paint) {

        if (paint instanceof Color) {
            return ((Color) paint).getRGB();
        } else if (paint instanceof TexturePaint) {
            BufferedImage texture = ((TexturePaint) paint).getImage();
            return texture.getRGB(x % texture.getWidth(), y % texture.getHeight());
        }

        throw new IllegalArgumentException("Don't know how to fill with paint " + paint);
    }
}
