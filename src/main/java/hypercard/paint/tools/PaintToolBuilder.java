package hypercard.paint.tools;

import hypercard.paint.canvas.Canvas;
import hypercard.paint.observers.Provider;

import java.awt.*;

public class PaintToolBuilder {

    private final PaintToolType type;

    private Canvas canvas;
    private Provider<Stroke> strokeProvider;
    private Provider<Paint> paintProvider;
    private Provider<Integer> shapeSidesProvider;
    private Provider<Font> fontProvider;

    private static Provider<Stroke> defaultShapeStrokeProvider = new Provider<>(new BasicStroke(2));
    private static Provider<Stroke> defaultBrushStrokeProvider = new Provider<>(new BasicStroke(10));
    private static Provider<Paint> defaultPaintProvider = new Provider<>(Color.BLACK);
    private static Provider<Integer> defaultShapeSidesProvider = new Provider<>(5);
    private static Provider<Font> defaultFontProvider = new Provider<>(new Font("Courier", Font.PLAIN, 14));

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

    public PaintToolBuilder withPaint(Paint paint) {
        this.paintProvider = new Provider<>(paint);
        return this;
    }

    public PaintToolBuilder withPaintProvider(Provider<Paint> paintProvider) {
        this.paintProvider = paintProvider;
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

            default:
                throw new RuntimeException("Bug! Unimplemented builder for tool " + type);
        }

        if (strokeProvider != null) {
            selectedTool.setStrokeProvider(strokeProvider);
        } else {
            selectedTool.setStrokeProvider(getDefaultStrokeProviderForTool(type));
        }

        if (paintProvider != null) {
            selectedTool.setPaintProvider(paintProvider);
        } else {
            selectedTool.setPaintProvider(defaultPaintProvider);
        }

        if (shapeSidesProvider != null) {
            selectedTool.setShapeSidesProvider(shapeSidesProvider);
        } else {
            selectedTool.setShapeSidesProvider(defaultShapeSidesProvider);
        }

        if (fontProvider != null) {
            selectedTool.setFontProvider(fontProvider);
        } else {
            selectedTool.setFontProvider(defaultFontProvider);
        }

        if (canvas != null) {
            selectedTool.activate(canvas);
        }

        return selectedTool;
    }

    public static void setDefaultBrushStrokeProvider(Provider<Stroke> strokeProvider) {
        if (strokeProvider != null) {
            defaultBrushStrokeProvider = strokeProvider;
        }
    }

    public static void setDefaultShapeStrokeProvider(Provider<Stroke> strokeProvider) {
        if (strokeProvider != null) {
            defaultShapeStrokeProvider = strokeProvider;
        }
    }

    public static void setDefaultFontProvider(Provider<Font> fontProvider) {
        if (fontProvider != null) {
            defaultFontProvider = fontProvider;
        }
    }

    public static void setDefaultPaintProvider(Provider<Paint> paintProvider) {
        if (paintProvider != null) {
            defaultPaintProvider = paintProvider;
        }
    }

    public static void setDefaultShapeSidesProvider(Provider<Integer> defaultShapeSidesProvider) {
        PaintToolBuilder.defaultShapeSidesProvider = defaultShapeSidesProvider;
    }

    public static void setDefaultCanvas(Canvas canvas) {
        defaultCanvas = canvas;
    }

    private Provider<Stroke> getDefaultStrokeProviderForTool(PaintToolType type) {
        switch (type) {
            case PAINTBRUSH:
                return defaultBrushStrokeProvider;

            default:
                return defaultShapeStrokeProvider;
        }
    }
}
