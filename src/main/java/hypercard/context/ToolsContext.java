package hypercard.context;

import hypercard.paint.observers.Provider;
import hypercard.paint.tools.AbstractPaintTool;
import hypercard.paint.tools.PaintToolBuilder;
import hypercard.paint.tools.PaintToolType;
import hypercard.runtime.RuntimeEnv;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class ToolsContext {

    private final static ToolsContext instance = new ToolsContext();

    private Provider<Stroke> strokeProvider = new Provider<>(new BasicStroke(5));
    private Provider<Paint> paintProvider = new Provider<>(Color.black);
    private Provider<Paint> fillProvider = new Provider<>(null);
    private Provider<Integer> shapeSidesProvider = new Provider<>(5);
    private Provider<Font> fontProvider = new Provider<>(new Font("Courier", Font.PLAIN, 12));

    private Provider<AbstractPaintTool> selectedToolProvider = new Provider<>(PaintToolBuilder.create(PaintToolType.ARROW).build());

    private Set<PaintToolSelectionObserver> paintToolSelectionObservers = new HashSet<>();
    private Set<ShapeSelectionObserver> shapeSelectionObservers = new HashSet<>();

    public interface PaintToolSelectionObserver {
        void onPaintToolSelected(AbstractPaintTool oldTool, AbstractPaintTool newTool);
    }

    public interface ShapeSelectionObserver {
        void onShapeSelected(int sides);
    }

    private ToolsContext() {
        PaintToolBuilder.setDefaultBrushStrokeProvider(strokeProvider);
        PaintToolBuilder.setDefaultPaintProvider(paintProvider);
        PaintToolBuilder.setDefaultShapeSidesProvider(shapeSidesProvider);
        PaintToolBuilder.setDefaultFontProvider(fontProvider);
    }

    public static ToolsContext getInstance() {
        return instance;
    }

    public Provider<AbstractPaintTool> getPaintToolProvider() {
        return selectedToolProvider;
    }

    public AbstractPaintTool getSelectedToolProvider() {
        return selectedToolProvider.get();
    }

    public void setSelectedToolType(PaintToolType selectedToolType) {
        AbstractPaintTool oldTool = selectedToolProvider.get();

        selectedToolProvider.get().deactivate();
        selectedToolProvider.set(PaintToolBuilder.create(selectedToolType)
                .makeActiveOnCanvas(RuntimeEnv.getRuntimeEnv().getCard().getCanvas())
                .build());

        firePaintToolSelectionChanged(oldTool, selectedToolProvider.get());
    }

    public void setShapeSides(int shapeSides) {
        shapeSidesProvider.set(shapeSides);
        fireShapeSelectionChanged(shapeSides);
    }

    public int getShapeSides() {
        return shapeSidesProvider.get();
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

    public boolean addPaintToolSelectionObserver(PaintToolSelectionObserver observer) {
        return paintToolSelectionObservers.add(observer);
    }

    public boolean removePaintToolSelectionObserver(PaintToolSelectionObserver observer) {
        return paintToolSelectionObservers.remove(observer);
    }

    public boolean addShapeSelectionObserver(ShapeSelectionObserver observer) {
        return shapeSelectionObservers.add(observer);
    }

    public boolean removeShapeSelectionObserver(ShapeSelectionObserver observer) {
        return shapeSelectionObservers.remove(observer);
    }

    private void fireShapeSelectionChanged(int sides) {
        for (ShapeSelectionObserver thisObserver : shapeSelectionObservers) {
            thisObserver.onShapeSelected(sides);
        }
    }

    private void firePaintToolSelectionChanged(AbstractPaintTool oldTool, AbstractPaintTool newTool) {
        for (PaintToolSelectionObserver thisObserver : paintToolSelectionObservers) {
            thisObserver.onPaintToolSelected(oldTool, newTool);
        }
    }

}
