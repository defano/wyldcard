package hypercard.paint.canvas.surface;

import hypercard.paint.model.Provider;

public interface GridSurface {
    void setGridSpacing(int grid);
    Provider<Integer> getGridSpacingProvider();
}
