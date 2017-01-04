package hypercard.context;

import hypercard.HyperCard;
import hypercard.paint.model.Provider;
import hypercard.paint.patterns.HyperCardPatternFactory;
import hypercard.paint.tools.AbstractPaintTool;
import hypercard.paint.utils.PaintToolBuilder;
import hypercard.paint.model.PaintToolType;
import hypercard.parts.CardPart;
import hypercard.parts.model.StackModelObserver;
import hypertalk.ast.common.Tool;
import hypertalk.exception.HtSemanticException;

import java.awt.*;

public class ToolsContext implements StackModelObserver {

    private final static ToolsContext instance = new ToolsContext();

    private boolean shapesFilled = false;

    private Provider<Stroke> lineStrokeProvider = new Provider<>(new BasicStroke(2));
    private Provider<Stroke> eraserStrokeProvider = new Provider<>(new BasicStroke(10));
    private Provider<Stroke> brushStrokeProvider = new Provider<>(new BasicStroke(5));
    private Provider<Paint> linePaintProvider = new Provider<>(Color.black);
    private Provider<Integer> fillPatternProvider = new Provider<>(0);
    private Provider<Integer> shapeSidesProvider = new Provider<>(5);
    private Provider<Font> fontProvider = new Provider<>(new Font("Courier", Font.PLAIN, 12));
    private Provider<AbstractPaintTool> toolProvider = new Provider<>(PaintToolBuilder.create(PaintToolType.ARROW).build());

    private ToolsContext() {
        HyperCard.getRuntimeEnv().getStack().addObserver(this);
    }

    public static ToolsContext getInstance() {
        return instance;
    }

    public Provider<Stroke> getLineStrokeProvider() {
        return lineStrokeProvider;
    }

    public Provider<AbstractPaintTool> getPaintToolProvider() {
        return toolProvider;
    }

    public void setSelectedToolType(PaintToolType selectedToolType) {
        toolProvider.get().deactivate();
        toolProvider.set(PaintToolBuilder.create(selectedToolType)
                .withStrokeProvider(getStrokeProviderForTool(selectedToolType))
                .withStrokePaintProvider(linePaintProvider)
                .withFillPaintProvider(new Provider<>(fillPatternProvider, t -> isShapesFilled() ? HyperCardPatternFactory.create((int) t) : null))
                .withFontProvider(fontProvider)
                .withShapeSidesProvider(shapeSidesProvider)
                .makeActiveOnCanvas(HyperCard.getRuntimeEnv().getCard().getCanvas())
                .build());
    }

    public void setShapeSides(int shapeSides) {
        shapeSidesProvider.set(shapeSides);
    }

    public int getShapeSides() {
        return shapeSidesProvider.get();
    }

    public Provider<Integer> getShapeSidesProvider() {
        return shapeSidesProvider;
    }

    public Provider<Integer> getFillPatternProvider() {
        return fillPatternProvider;
    }

    public void setFontSize(int size) {
        String currentFamily = fontProvider.get().getFamily();
        int currentStyle = fontProvider.get().getStyle();

        fontProvider.set(new Font(currentFamily, currentStyle, size));

    }

    public void setFontStyle(int style) {
        String currentFamily = fontProvider.get().getFamily();
        int currentSize = fontProvider.get().getSize();

        fontProvider.set(new Font(currentFamily, style, currentSize));
    }

    public void setFontFamily(String fontName) {
        int currentSize = fontProvider.get().getSize();
        int currentStyle = fontProvider.get().getStyle();

        fontProvider.set(new Font(fontName, currentStyle, currentSize));
    }

    public Provider<Font> getFontProvider() {
        return fontProvider;
    }

    public void setLineWidth(int width) {
        lineStrokeProvider.set(new BasicStroke(width));
    }

    public void setPattern(int patternId) {
            fillPatternProvider.set(patternId);
    }

    public boolean isShapesFilled() {
        return shapesFilled;
    }

    public void toggleShapesFilled() {
        this.shapesFilled = !shapesFilled;
        setSelectedToolType(toolProvider.get().getToolType());
    }

    public void setSelectedTool (Tool tool) {
        switch (tool) {
            case BROWSE:
                setSelectedToolType(PaintToolType.ARROW);
                break;
            case OVAL:
                setSelectedToolType(PaintToolType.OVAL);
                break;
            case BRUSH:
                setSelectedToolType(PaintToolType.PAINTBRUSH);
                break;
            case PENCIL:
                setSelectedToolType(PaintToolType.PENCIL);
                break;
            case BUCKET:
                setSelectedToolType(PaintToolType.FILL);
                break;
            case POLYGON:
                setSelectedToolType(PaintToolType.POLYGON);
                break;
            case BUTTON:
                // TODO: Not implemented
                break;
            case RECTANGLE:
                setSelectedToolType(PaintToolType.RECTANGLE);
                break;
            case CURVE:
                setSelectedToolType(PaintToolType.CURVE);
                break;
            case SHAPE:
                setSelectedToolType(PaintToolType.SHAPE);
                break;
            case ERASER:
                setSelectedToolType(PaintToolType.ERASER);
                break;
            case ROUNDRECT:
                setSelectedToolType(PaintToolType.ROUND_RECTANGLE);
                break;
            case FIELD:
                // TODO: Not implemented
                break;
            case SELECT:
                setSelectedToolType(PaintToolType.SELECTION);
                break;
            case LASSO:
                setSelectedToolType(PaintToolType.LASSO);
                break;
            case SPRAY:
                setSelectedToolType(PaintToolType.SPRAYPAINT);
                break;
            case LINE:
                setSelectedToolType(PaintToolType.LINE);
                break;
            case TEXT:
                setSelectedToolType(PaintToolType.TEXT);
                break;
        }
    }

    public void setSelectedTool (int toolNumber) throws HtSemanticException {
        setSelectedTool(Tool.byNumber(toolNumber));
    }

    @Override
    public void onCurrentCardChanged(CardPart newCard) {
        toolProvider.get().deactivate();
        toolProvider.get().activate(newCard.getCanvas());
    }

    private Provider<Stroke> getStrokeProviderForTool(PaintToolType type) {
        switch (type) {
            case PAINTBRUSH:
            case SPRAYPAINT:
                return brushStrokeProvider;

            case ERASER:
                return eraserStrokeProvider;

            default:
                return lineStrokeProvider;
        }
    }
}
