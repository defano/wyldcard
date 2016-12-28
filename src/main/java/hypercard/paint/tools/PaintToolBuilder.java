package hypercard.paint.tools;

import hypercard.paint.canvas.Canvas;
import hypercard.paint.observers.ObservableAttribute;

import java.awt.*;

public class PaintToolBuilder {

    private final ToolType type;

    private Canvas canvas;
    private ObservableAttribute<Stroke> strokeProvider;
    private ObservableAttribute<Paint> paintProvider;

    private static ObservableAttribute<Stroke> defaultShapeStrokeProvider = new ObservableAttribute<>(new BasicStroke(2));
    private static ObservableAttribute<Stroke> defaultBrushStrokeProvider = new ObservableAttribute<>(new BasicStroke(10));
    private static ObservableAttribute<Paint> defaultPaintProvider = new ObservableAttribute<>(Color.BLACK);
    private static Canvas defaultCanvas;

    private PaintToolBuilder(ToolType type) {
        this.type = type;
    }

    public static PaintToolBuilder createTool(ToolType ofType) {
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
        this.strokeProvider = new ObservableAttribute<>(stroke);
        return this;
    }

    public PaintToolBuilder withStrokeProvider(ObservableAttribute<Stroke> strokeProvider) {
        this.strokeProvider = strokeProvider;
        return this;
    }

    public PaintToolBuilder withPaint(Paint paint) {
        this.paintProvider = new ObservableAttribute<>(paint);
        return this;
    }

    public PaintToolBuilder withPaintProvider(ObservableAttribute<Paint> paintProvider) {
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
            case PAINTBRUSH:
                selectedTool = new PaintbrushTool();
                break;
            case ERASER:
                selectedTool = new EraserTool();
                break;
            case LINE:
                selectedTool = new LineTool();
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

    public static void setDefaultBrushStrokeProvider(ObservableAttribute<Stroke> strokeProvider) {
        if (strokeProvider != null) {
            defaultBrushStrokeProvider = strokeProvider;
        }
    }

    public static void setDefaultShapeStrokeProvider(ObservableAttribute<Stroke> strokeProvider) {
        if (strokeProvider != null) {
            defaultShapeStrokeProvider = strokeProvider;
        }
    }

    public static void setDefaultPaintProvider(ObservableAttribute<Paint> paintProvider) {
        if (paintProvider != null) {
            defaultPaintProvider = paintProvider;
        }
    }

    public static void setDefaultCanvas(Canvas canvas) {
        defaultCanvas = canvas;
    }

    private ObservableAttribute<Stroke> getDefaultStrokeProviderForTool() {
        switch (type) {
            case PAINTBRUSH:
                return defaultBrushStrokeProvider;

            default:
                return defaultShapeStrokeProvider;
        }
    }
}
