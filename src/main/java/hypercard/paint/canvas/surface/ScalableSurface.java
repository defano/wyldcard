package hypercard.paint.canvas.surface;

import hypercard.paint.model.Provider;

import java.awt.*;

public interface ScalableSurface {
    void setImageLocation(Point location);
    Point getImageLocation();

    void setScale(double scale);
    Provider<Double> getScaleProvider();
}
