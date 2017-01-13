package hypercard.paint.tools;


import hypercard.paint.canvas.Canvas;
import hypercard.paint.model.PaintToolType;

import java.awt.*;
import java.awt.event.MouseEvent;

public class MagnifierTool extends AbstractPaintTool {

    private double scale = 1.0;
    private double magnificationStep = 2;

    public MagnifierTool() {
        super(PaintToolType.MAGNIFIER);
    }

    @Override
    public void mousePressed(MouseEvent e, int scaleX, int scaleY) {

        if (e.isControlDown() || e.isAltDown() || e.isMetaDown()) {
            reset();
        }

        else if (e.isShiftDown()) {
            zoomOut(e.getX(), e.getY());
            recenter(e.getX(), e.getY(), scaleX, scaleY);
        }

        else {
            zoomIn();
            recenter(e.getX(), e.getY(), scaleX, scaleY);
        }
    }

    @Override
    public void activate(Canvas canvas) {
        super.activate(canvas);
        this.scale = canvas.getScale();
    }

    private void recenter(int canvasX, int canvasY, int imageX, int imageY) {
        getCanvas().setImageLocation(new Point((int)(imageX * scale) - canvasX, (int)(imageY * scale) - canvasY));
    }

    private void reset() {
        scale = 1.0;
        getCanvas().setScale(scale);
        getCanvas().setImageLocation(new Point(0,0));
    }

    private void zoomIn() {
        scale += magnificationStep;
        getCanvas().setScale(scale);
    }

    private void zoomOut(int x, int y) {
        scale -= magnificationStep;

        if (scale <= 1.0) {
            reset();
            return;
        }

        getCanvas().setScale(scale);
    }
}
