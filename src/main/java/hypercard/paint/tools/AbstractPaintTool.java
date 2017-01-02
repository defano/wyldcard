package hypercard.paint.tools;

import hypercard.paint.canvas.Canvas;
import hypercard.paint.model.PaintToolType;
import hypercard.paint.model.Provider;

import java.awt.*;
import java.awt.event.MouseAdapter;

public abstract class AbstractPaintTool extends MouseAdapter {

    private Canvas canvas;
    private final PaintToolType type;
    private AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);

    private Stroke defaultStroke = new BasicStroke(2);
    private Paint defaultStrokePaint = Color.BLACK;
    private Paint defaultFillPaint = null;
    private int defaultShapeSides = 5;
    private Font defaultFont = new Font("Courier", Font.PLAIN, 14);

    private Provider<Stroke> strokeProvider = new Provider<>(defaultStroke);
    private Provider<Paint> strokePaintProvider = new Provider<>(defaultStrokePaint);
    private Provider<Paint> fillPaintProvider = new Provider<>(defaultFillPaint);
    private Provider<Integer> shapeSidesProvider = new Provider<>(defaultShapeSides);
    private Provider<Font> fontProvider = new Provider<>(defaultFont);

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

    public void setStrokePaintProvider(Provider<Paint> strokePaintProvider) {
        if (strokePaintProvider != null) {
            this.strokePaintProvider = strokePaintProvider;
        }
    }

    public void setStrokeProvider(Provider<Stroke> strokeProvider) {
        if (strokeProvider != null) {
            this.strokeProvider = strokeProvider;
        }
    }

    public void setShapeSidesProvider(Provider<Integer> shapeSidesProvider) {
        if (shapeSidesProvider != null) {
            this.shapeSidesProvider = shapeSidesProvider;
        }
    }

    public void setFontProvider(Provider<Font> fontProvider) {
        if (fontProvider != null) {
            this.fontProvider = fontProvider;
        }
    }

    public void setFillPaintProvider(Provider<Paint> fillPaintProvider) {
        if (fillPaintProvider != null) {
            this.fillPaintProvider = fillPaintProvider;
        }
    }

    public Stroke getStroke() {
        return strokeProvider.get();
    }

    public Paint getFillPaint() {
        return fillPaintProvider.get();
    }

    public Font getFont() {
        return fontProvider.get();
    }

    public int getShapeSides() {
        return shapeSidesProvider.get();
    }

    public Paint getStrokePaint() {
        return strokePaintProvider.get();
    }

    public Provider<Paint> getFillPaintProvider() {
        return fillPaintProvider;
    }

    public Provider<Stroke> getStrokeProvider() {
        return strokeProvider;
    }

    public Provider<Paint> getStrokePaintProvider() {
        return strokePaintProvider;
    }

    public Provider<Integer> getShapeSidesProvider() {
        return shapeSidesProvider;
    }

    public Provider<Font> getFontProvider() {
        return fontProvider;
    }
}
