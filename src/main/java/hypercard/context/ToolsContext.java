package hypercard.context;

import hypercard.paint.observers.Provider;
import hypercard.paint.tools.AbstractPaintTool;
import hypercard.paint.tools.PaintToolBuilder;
import hypercard.paint.tools.PaintToolType;
import hypercard.HyperCard;
import hypercard.parts.CardPart;
import hypercard.parts.model.StackModelObserver;

import java.awt.*;

public class ToolsContext implements StackModelObserver {

    private final static ToolsContext instance = new ToolsContext();

    private Provider<Stroke> strokeProvider = new Provider<>(new BasicStroke(5));
    private Provider<Paint> paintProvider = new Provider<>(Color.black);
    private Provider<Paint> fillProvider = new Provider<>(null);
    private Provider<Integer> shapeSidesProvider = new Provider<>(5);
    private Provider<Font> fontProvider = new Provider<>(new Font("Courier", Font.PLAIN, 12));

    private Provider<AbstractPaintTool> toolProvider = new Provider<>(PaintToolBuilder.create(PaintToolType.ARROW).build());

    private ToolsContext() {
        PaintToolBuilder.setDefaultBrushStrokeProvider(strokeProvider);
        PaintToolBuilder.setDefaultPaintProvider(paintProvider);
        PaintToolBuilder.setDefaultShapeSidesProvider(shapeSidesProvider);
        PaintToolBuilder.setDefaultFontProvider(fontProvider);

        HyperCard.getRuntimeEnv().getStack().addObserver(this);
    }

    public static ToolsContext getInstance() {
        return instance;
    }

    public Provider<AbstractPaintTool> getPaintToolProvider() {
        return toolProvider;
    }

    public AbstractPaintTool getToolProvider() {
        return toolProvider.get();
    }

    public void setSelectedToolType(PaintToolType selectedToolType) {
        AbstractPaintTool oldTool = toolProvider.get();

        toolProvider.get().deactivate();
        toolProvider.set(PaintToolBuilder.create(selectedToolType)
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

    @Override
    public void onCurrentCardChanged(CardPart newCard) {
        toolProvider.get().deactivate();
        toolProvider.get().activate(newCard.getCanvas());
    }
}
