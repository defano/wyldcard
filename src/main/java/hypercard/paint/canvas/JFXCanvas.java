package hypercard.paint.canvas;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;

public class JFXCanvas extends SwingNode {

    private final BasicCanvas swingCanvas;

    public JFXCanvas (BasicCanvas canvas) {
        this.swingCanvas = canvas;
        Platform.runLater(() -> JFXCanvas.super.setContent(canvas));
    }

    public BasicCanvas getPaintCanvas() {
        return swingCanvas;
    }

}
