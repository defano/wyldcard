package com.defano.hypercard.paint;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.runtime.context.ToolsContext;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.ast.model.ToolType;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.jmonet.tools.SelectionTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * A utility class providing import and export routines for card graphics.
 * For the Seinfeld-impaired: http://seinfeld.wikia.com/wiki/Art_Vandelay
 */
public class ArtVandelay {

    public static void importPaint() {
        FileDialog fd = new FileDialog(WindowManager.getStackWindow().getWindow(), "Import Paint", FileDialog.LOAD);
        fd.setMultipleMode(false);
        fd.setFilenameFilter((dir, name) -> isFileSupportedForImporting(name));
        fd.setVisible(true);

        if (fd.getFiles().length > 0) {
            importPaint(fd.getFiles());
        }
    }

    public static void exportPaint() {
        FileDialog fd = new FileDialog(WindowManager.getStackWindow().getWindow(), "Export Paint", FileDialog.SAVE);
        fd.setFile("Untitled.png");
        fd.setVisible(true);

        if (fd.getFiles().length > 0) {
            try {
                exportPaint(fd.getFiles()[0], ToolsContext.getInstance().getSelectedImage());
            } catch (IOException e) {
                HyperCard.getInstance().showErrorDialog(new HtSemanticException("Can't export paint in that format."));
            }
        }
    }

    private static void exportPaint(File file, BufferedImage image) throws IOException {
        if (isFileSupportedForExporting(file.getName())) {
            ImageIO.write(image, getFileSuffix(file.getName(), null), file);
        } else {
            throw new IOException("Image format not supported.");
        }
    }

    public static void importPaint(File[] files) {
        if (files != null && files.length > 0) {
            importPaint(files[0]);
        }
    }

    private static void importPaint(File file) {

        try {
            BufferedImage importedImage = ImageIO.read(file);

            if (importedImage != null) {
                int cardHeight = HyperCard.getInstance().getDisplayedCard().getHeight();
                int cardWidth = HyperCard.getInstance().getDisplayedCard().getWidth();
                int cardCenterX = cardWidth / 2;
                int cardCenterY = cardHeight / 2;

                if (importedImage.getWidth() > cardHeight || importedImage.getHeight() > cardHeight) {
                    importedImage = scaleImageToDimension(importedImage, cardWidth, cardHeight);
                }

                SelectionTool tool = (SelectionTool) ToolsContext.getInstance().forceToolSelection(ToolType.SELECT, false);
                tool.createSelection(importedImage, new Point(cardCenterX - importedImage.getWidth() / 2, cardCenterY - importedImage.getHeight() / 2));
            }
        } catch (IOException e) {
            // Nothing to do
        }
    }

    private static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

        int originalWidth = imgSize.width;
        int originalHeight = imgSize.height;
        int maxWidth = boundary.width;
        int maxHeight = boundary.height;
        int targetWidth = originalWidth;
        int targetHeight = originalHeight;

        if (originalWidth > maxWidth) {
            targetWidth = maxWidth;
            targetHeight = (targetWidth * originalHeight) / originalWidth;
        }

        if (targetHeight > maxHeight) {
            targetHeight = maxHeight;
            targetWidth = (targetHeight * originalWidth) / originalHeight;
        }

        return new Dimension(targetWidth, targetHeight);
    }

    private static BufferedImage scaleImageToDimension(BufferedImage image, int maxWidth, int maxHeight) {
        Dimension scaledDimension = getScaledDimension(new Dimension(image.getWidth(), image.getHeight()), new Dimension(maxWidth, maxHeight));
        BufferedImage resized = new BufferedImage(scaledDimension.width, scaledDimension.height, image.getType());
        Graphics g = resized.getGraphics();
        g.drawImage(image, 0, 0, scaledDimension.width, scaledDimension.height, null);
        g.dispose();

        return resized;
    }

    private static boolean isFileSupportedForExporting(String fileName) {
        String suffix = getFileSuffix(fileName, null);

        for (String thisSuffix : ImageIO.getWriterFileSuffixes()) {
            if (thisSuffix.equalsIgnoreCase(suffix)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isFileSupportedForImporting(String fileName) {
        String suffix = getFileSuffix(fileName, null);

        for (String thisSuffix : ImageIO.getReaderFileSuffixes()) {
            if (thisSuffix.equalsIgnoreCase(suffix)) {
                return true;
            }
        }

        return false;
    }

    private static String getFileSuffix(String fileName, String dflt) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i + 1);
        }

        return dflt;
    }
}
