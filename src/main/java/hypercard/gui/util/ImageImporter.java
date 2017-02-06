/*
 * ImageImporter
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/5/17 8:25 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package hypercard.gui.util;

import com.defano.jmonet.model.PaintToolType;
import com.defano.jmonet.tools.SelectionTool;
import hypercard.HyperCard;
import hypercard.context.ToolsContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageImporter {

    public static void importAsSelection(File file) {
        try {
            BufferedImage importedImage = importImage(file);

            if (importedImage != null) {
                int cardCenterX = HyperCard.getInstance().getCard().getWidth() / 2;
                int cardCenterY = HyperCard.getInstance().getCard().getHeight() / 2;

                SelectionTool tool = (SelectionTool) ToolsContext.getInstance().selectPaintTool(PaintToolType.SELECTION);
                tool.createSelection(importedImage, new Point(cardCenterX - importedImage.getWidth() / 2, cardCenterY - importedImage.getHeight() / 2));
            }
        } catch (IOException e) {
            // Nothing to do
        }
    }

    public static BufferedImage importImage(File file) throws IOException {
        return ImageIO.read(file);
    }
}
