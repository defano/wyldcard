package hypercard.paint.canvas.surface;

import java.awt.*;

public interface CompositeSurface {
    void addComponent(Component component);
    void removeComponent(Component component);
}
