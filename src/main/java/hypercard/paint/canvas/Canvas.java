package hypercard.paint.canvas;

import hypercard.paint.canvas.surface.PaintableSurface;

import java.awt.*;

public interface Canvas extends PaintableSurface {
    boolean isVisible();

    void setCursor(Cursor cursor);

    void addObserver(CanvasCommitObserver observer);
    boolean removeObserver(CanvasCommitObserver observer);

    void invalidateCanvas();

    void commit();
    void commit(AlphaComposite composite);
}
