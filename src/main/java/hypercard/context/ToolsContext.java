package hypercard.context;

import hypercard.paint.PaintToolSelectionObserver;
import hypercard.paint.tools.AbstractPaintTool;
import hypercard.paint.tools.PaintToolBuilder;
import hypercard.paint.tools.PaintToolType;
import hypercard.runtime.RuntimeEnv;

import java.util.HashSet;
import java.util.Set;

public class ToolsContext {

    private final static ToolsContext instance = new ToolsContext();

    private AbstractPaintTool selectedTool = PaintToolBuilder.createTool(PaintToolType.ARROW).build();
    private Set<PaintToolSelectionObserver> observers = new HashSet<>();

    private ToolsContext() {}

    public static ToolsContext getInstance() {
        return instance;
    }

    public PaintToolType getSelectedToolType() {
        return selectedTool.getToolType();
    }

    public AbstractPaintTool getSelectedTool() {
        return selectedTool;
    }

    public void setSelectedToolType(PaintToolType selectedToolType) {
        AbstractPaintTool oldTool = selectedTool;

        selectedTool.deactivate();
        selectedTool = PaintToolBuilder.createTool(selectedToolType)
                .makeActiveOnCanvas(RuntimeEnv.getRuntimeEnv().getCard().getCanvas())
                .build();

        fireSelectionChanged(oldTool, selectedTool);
    }

    public boolean addObserver(PaintToolSelectionObserver observer) {
        return observers.add(observer);
    }

    public boolean removeObserver(PaintToolSelectionObserver observer) {
        return observers.remove(observer);
    }

    private void fireSelectionChanged(AbstractPaintTool oldTool, AbstractPaintTool newTool) {
        for (PaintToolSelectionObserver thisObserver : observers) {
            thisObserver.onPaintToolSelected(oldTool, newTool);
        }
    }

}
