package hypercard.context;

import hypercard.HyperCard;
import hypercard.paint.observers.Provider;
import hypercard.paint.patterns.StandardPatternFactory;
import hypercard.paint.tools.AbstractPaintTool;
import hypercard.paint.tools.PaintToolBuilder;
import hypercard.paint.tools.PaintToolType;
import hypercard.parts.CardPart;
import hypercard.parts.model.StackModelObserver;

import java.awt.*;

public class ToolsContext implements StackModelObserver {

    private final static ToolsContext instance = new ToolsContext();

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
                .withFillPaintProvider(new Provider<>(fillPatternProvider, t -> (int) t == 0 ? null : StandardPatternFactory.create((int) t)))
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
