package hypercard.paint.canvas.surface;

import hypercard.paint.canvas.CanvasInteractionObserver;

public interface InteractiveSurface {
    void addCanvasInteractionListener(CanvasInteractionObserver listener);
    boolean removeCanvasInteractionListener(CanvasInteractionObserver listener);

}
