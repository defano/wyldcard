package hypercard.paint.tools;

import hypercard.paint.canvas.Canvas;
import hypercard.paint.observers.Provider;

import java.awt.*;

public class PaintToolBuilder {

    private final PaintToolType type;

    private Canvas canvas;
    private Provider<Stroke> strokeProvider;
    private Provider<Paint> paintProvider;

    private static Provider<Stroke> defaultShapeStrokeProvider = new Provider<>(new BasicStroke(2));
    private static Provider<Stroke> defaultBrushStrokeProvider = new Provider<>(new BasicStroke(10));
    private static Provider<Paint> defaultPaintProvider = new Provider<>(Color.BLACK);
    private static Canvas defaultCanvas;

    private PaintToolBuilder(PaintToolType type) {
        this.type = type;
    }

    public static PaintToolBuilder createTool(PaintToolType ofType) {
        return new PaintToolBuilder(ofType);
    }

    public PaintToolBuilder makeActive() {
        this.canvas = defaultCanvas;
        return this;
    }

    public PaintToolBuilder makeActiveOnCanvas(Canvas canvas) {
        this.canvas = canvas;
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

        if (canvas != null) {
            selectedTool.activate(canvas);
        }

        if (strokeProvider != null) {
            selectedTool.setStrokeProvider(strokeProvider);
        } else {
            selectedTool.setStrokeProvider(getDefaultStrokeProviderForTool());
        }

        if (paintProvider != null) {
            selectedTool.setPaintProvider(paintProvider);
        } else {
            selectedTool.setPaintProvider(defaultPaintProvider);
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

    public static void setDefaultPaintProvider(Provider<Paint> paintProvider) {
        if (paintProvider != null) {
            defaultPaintProvider = paintProvider;
        }
    }

    public static void setDefaultCanvas(Canvas canvas) {
        defaultCanvas = canvas;
    }

    private Provider<Stroke> getDefaultStrokeProviderForTool() {
        switch (type) {
            case PAINTBRUSH:
                return defaultBrushStrokeProvider;

            default:
                return defaultShapeStrokeProvider;
        }
    }
}
