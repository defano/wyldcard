package hypercard.paint.canvas;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;

public class JFXCanvas extends SwingNode {

    public JFXCanvas (AbstractSwingCanvas canvas) {
        Platform.runLater(() -> JFXCanvas.super.setContent(canvas));
    }

}
