package hypercard.paint;

import hypercard.paint.tools.AbstractPaintTool;

public interface PaintToolSelectionObserver {
    void onPaintToolSelected(AbstractPaintTool oldTool, AbstractPaintTool newTool);
}
