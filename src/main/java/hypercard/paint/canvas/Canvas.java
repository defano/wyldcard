package hypercard.paint.canvas;

import hypercard.paint.model.Provider;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

public interface Canvas {
    Rectangle getBounds();
    int getHeight();
    int getWidth();
    boolean isVisible();

    void setCursor(Cursor cursor);

    void addObserver(CanvasObserver observer);
    boolean removeObserver(CanvasObserver observer);

    void addCanvasInteractionListener(CanvasInteractionListener listener);
    boolean removeCanvasInteractionListener(CanvasInteractionListener listener);

    void addComponent(Component component);
    void removeComponent(Component component);

    void repaintCanvas();

    void commit();
    void commit(AlphaComposite composite);

    void setImageLocation(Point location);
    Point getImageLocation();
    void setScale(double scale);
    Provider<Double> getScaleProvider();

    void setGridSpacing(int grid);
    Provider<Integer> getGridSpacingProvider();

    BufferedImage getCanvasImage();
    BufferedImage getScratchImage();
    Graphics getScratchGraphics();
    void clearScratch();
}
