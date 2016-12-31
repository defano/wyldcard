package hypercard.paint.tools;

import hypercard.paint.canvas.Canvas;
import hypercard.paint.observers.Provider;

import java.awt.*;
import java.awt.event.MouseAdapter;

public abstract class AbstractPaintTool extends MouseAdapter {

    private Canvas canvas;
    private final PaintToolType type;
    private AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);

    private Provider<Stroke> strokeProvider = new Provider<>(new BasicStroke(5));
    private Provider<Paint> paintProvider = new Provider<>(Color.black);
    private Provider<Paint> fillProvider = new Provider<>(null);
    private Provider<Integer> shapeSidesProvider = new Provider<>(5);
    private Provider<Font> fontProvider = new Provider<>(new Font("Courier", Font.PLAIN, 14));

    public AbstractPaintTool(PaintToolType type) {
        this.type = type;
    }

    public void activate (Canvas canvas) {
        this.canvas = canvas;
        this.canvas.addMouseListener(this);
        this.canvas.addMouseMotionListener(this);
    }

    public void deactivate() {
        if (canvas != null) {
            canvas.removeMouseListener(this);
            canvas.removeMouseMotionListener(this);
        }
    }

    public AlphaComposite getComposite() {
        return composite;
    }

    public void setComposite(AlphaComposite composite) {
        this.composite = composite;
    }

    public PaintToolType getToolType() {
        return this.type;
    }

    protected Canvas getCanvas() {
        return canvas;
    }

    public void setPaintProvider(Provider<Paint> paintProvider) {
        this.paintProvider = paintProvider;
    }

    public Paint getPaint() {
        return paintProvider.get();
    }

    public void setStrokeProvider(Provider<Stroke> strokeProvider) {
        this.strokeProvider = strokeProvider;
    }

    public Stroke getStroke() {
        return strokeProvider.get();
    }

    public void setFillProvider(Provider<Paint> fillProvider) {
        this.fillProvider = fillProvider;
    }

    public Paint getFill() {
        return fillProvider.get();
    }

    public void setShapeSidesProvider(Provider<Integer> shapeSidesProvider) {
        this.shapeSidesProvider = shapeSidesProvider;
    }

    public void setFontProvider(Provider<Font> fontProvider) {
        this.fontProvider = fontProvider;
    }

    public Provider<Stroke> getStrokeProvider() {
        return strokeProvider;
    }

    public Provider<Paint> getPaintProvider() {
        return paintProvider;
    }

    public Provider<Paint> getFillProvider() {
        return fillProvider;
    }

    public Provider<Integer> getShapeSidesProvider() {
        return shapeSidesProvider;
    }

    public Provider<Font> getFontProvider() {
        return fontProvider;
    }

    public Font getFont() {
        return fontProvider.get();
    }

    public int getShapeSides() {
        return shapeSidesProvider.get();
    }
}
