package hypercard.paint.tools;

import hypercard.paint.canvas.Canvas;
import hypercard.paint.observers.Provider;

import java.awt.*;

public class PaintToolBuilder {

    private final PaintToolType type;

    private Canvas canvas;
    private Provider<Stroke> strokeProvider;
    private Provider<Paint> strokePaintProvider;
    private Provider<Paint> fillPaintProvider;
    private Provider<Integer> shapeSidesProvider;
    private Provider<Font> fontProvider;

    private static Canvas defaultCanvas;

    private PaintToolBuilder(PaintToolType toolType) {
        this.type = toolType;
    }

    public static PaintToolBuilder create(PaintToolType toolType) {
        return new PaintToolBuilder(toolType);
    }

    public PaintToolBuilder makeActive() {
        this.canvas = defaultCanvas;
        return this;
    }

    public PaintToolBuilder makeActiveOnCanvas(Canvas canvas) {
        this.canvas = canvas;
        return this;
    }

    public PaintToolBuilder withFont(Font font) {
        this.fontProvider = new Provider<>(font);
        return this;
    }

    public PaintToolBuilder withFontProvider(Provider<Font> fontProvider) {
        this.fontProvider = fontProvider;
        return this;
    }

    public PaintToolBuilder withShapeSides(int sides) {
        this.shapeSidesProvider = new Provider<>(sides);
        return this;
    }

    public PaintToolBuilder withShapeSidesProvider(Provider<Integer> shapeSidesProvider) {
        this.shapeSidesProvider = shapeSidesProvider;
        return this;
    }

    public PaintToolBuilder withStroke(Stroke stroke) {
        this.strokeProvider = new Provider<>(stroke);
        return this;
    }

    public PaintToolBuilder withStrokeProvider(Provider<Stroke> strokeProvider) {
        this.strokeProvider = strokeProvider;
        return this;
    }

    public PaintToolBuilder withStrokePaint(Paint strokePaint) {
        this.strokePaintProvider = new Provider<Paint>(strokePaint);
        return this;
    }

    public PaintToolBuilder withStrokePaintProvider(Provider<Paint> strokePaintProvider) {
        this.strokePaintProvider = strokePaintProvider;
        return this;
    }

    public PaintToolBuilder withFillPaint(Paint paint) {
        this.fillPaintProvider = new Provider<>(paint);
        return this;
    }

    public PaintToolBuilder withFillPaintProvider(Provider<Paint> paintProvider) {
        this.fillPaintProvider = paintProvider;
        return this;
    }

    public AbstractPaintTool build() {

        AbstractPaintTool selectedTool;

        switch (type) {
            case PENCIL:
                selectedTool = new PencilTool();
                break;
            case ARROW:
                selectedTool = new ArrowTool();
                break;
            case RECTANGLE:
                selectedTool = new RectangleTool();
                break;
            case ROUND_RECTANGLE:
                selectedTool = new RoundRectangleTool();
                break;
            case OVAL:
                selectedTool = new OvalTool();
                break;
            case PAINTBRUSH:
                selectedTool = new PaintbrushTool();
                break;
            case ERASER:
                selectedTool = new EraserTool();
                break;
            case LINE:
                selectedTool = new LineTool();
                break;
            case POLYGON:
                selectedTool = new PolygonTool();
                break;
            case SHAPE:
                selectedTool = new ShapeTool();
                break;
            case SELECTION:
                selectedTool = new SelectionTool();
                break;
            case TEXT:
                selectedTool = new TextTool();
                break;
            case FILL:
                selectedTool = new FillTool();
                break;

            default:
                throw new RuntimeException("Bug! Unimplemented builder for tool " + type);
        }

        if (strokeProvider != null) {
            selectedTool.setStrokeProvider(strokeProvider);
        }

        if (strokePaintProvider != null) {
            selectedTool.setStrokePaintProvider(strokePaintProvider);
        }

        if (shapeSidesProvider != null) {
            selectedTool.setShapeSidesProvider(shapeSidesProvider);
        }

        if (fontProvider != null) {
            selectedTool.setFontProvider(fontProvider);
        }

        if (fillPaintProvider != null) {
            selectedTool.setFillPaintProvider(fillPaintProvider);
        }

        if (canvas != null) {
            selectedTool.activate(canvas);
        }

        return selectedTool;
    }

    public static void setDefaultCanvas(Canvas canvas) {
        defaultCanvas = canvas;
    }
}
