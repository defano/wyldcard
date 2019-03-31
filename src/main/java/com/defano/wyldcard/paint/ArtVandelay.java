package com.defano.wyldcard.paint;

import com.defano.jmonet.tools.MarqueeTool;
import com.defano.wyldcard.WyldCard;
import com.defano.hypertalk.ast.model.ToolType;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

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

    /**
     * Prompts the user to choose a file from which to import paint from; graphics from selected file are "pasted" onto
     * the card as the active selection.
     */
    public static void importPaint() {
        FileDialog fd = new FileDialog(WyldCard.getInstance().getWindowManager().getFocusedStackWindow().getWindow(), "Import Paint", FileDialog.LOAD);
        fd.setMultipleMode(false);
        fd.setFilenameFilter((dir, name) -> isFileSupportedForImporting(name));
        fd.setVisible(true);

        if (fd.getFiles().length > 0) {
            importPaint(fd.getFiles());
        }
    }

    /**
     * Exports an image of the currently displayed card, or the graphic currently selected (if an active paint selection
     * exists) to a file of the user's choosing. Displays a syntax error dialog if the export fails for any reason.
     */
    public static void exportPaint() {
        FileDialog fd = new FileDialog(WyldCard.getInstance().getWindowManager().getFocusedStackWindow().getWindow(), "Export Paint", FileDialog.SAVE);
        fd.setFile("Untitled.png");
        fd.setVisible(true);

        if (fd.getFiles().length > 0) {
            try {
                exportPaint(fd.getFiles()[0]);
            } catch (HtException e) {
                WyldCard.getInstance().showErrorDialogAndAbort(e);
            }
        }
    }

    /**
     * Exports an image of the currently displayed card, or the graphic currently selected (if an active paint selection
     * exists) to the given file.
     *
     * @param file The file where the paint should be exported
     * @throws HtException Thrown if an error occurs exporting paint.
     */
    public static void exportPaint(File file) throws HtException {
        try {
            BufferedImage exportImage = WyldCard.getInstance().getToolsManager().getSelectedImage() == null ?
                    WyldCard.getInstance().getStackManager().getFocusedStack().getDisplayedCard().getScreenshot() :
                    WyldCard.getInstance().getToolsManager().getSelectedImage();
            exportPaint(file, exportImage);
        } catch (IOException e) {
            throw new HtSemanticException("Couldn't export paint to that file.");
        }
    }

    /**
     * Exports the given image to a file. The format of the resulting image is based on the extension of the given file.
     *
     * @param file  The file where to write the image
     * @param image The image to be written
     * @throws IOException Thrown if an error occurs writing the file.
     */
    private static void exportPaint(File file, BufferedImage image) throws IOException, HtException {
        if (isFileSupportedForExporting(file.getName())) {
            ImageIO.write(image, getFileSuffix(file.getName(), "png"), file);
        } else {
            throw new HtException("Can't export paint in that format.");
        }
    }

    /**
     * Makes a "best effort" attempt to import paint from the first file provided. Has no effect (and no error is
     * reported) if paint cannot be imported from the first file in the list.
     *
     * @param files Zero or more files; only the first file in the list is utilized.
     */
    public static void importPaint(File[] files) {
        if (files != null && files.length > 0) {
            try {
                importPaint(files[0]);
            } catch (HtSemanticException e) {
                // Nothing to do
            }
        }
    }

    /**
     * Attempts to import paint from the given file.
     *
     * @param file The file to import
     * @throws HtSemanticException Thrown if an error occurs importing paint (i.e., file doesn't exist or is in an
     *                             unknown format.
     */
    public static void importPaint(File file) throws HtSemanticException {

        try {
            BufferedImage importedImage = ImageIO.read(file);

            if (importedImage != null) {
                int cardHeight = WyldCard.getInstance().getStackManager().getFocusedCard().getHeight();
                int cardWidth = WyldCard.getInstance().getStackManager().getFocusedCard().getWidth();
                int cardCenterX = cardWidth / 2;
                int cardCenterY = cardHeight / 2;

                if (importedImage.getWidth() > cardHeight || importedImage.getHeight() > cardHeight) {
                    importedImage = scaleImageToDimension(importedImage, cardWidth, cardHeight);
                }

                MarqueeTool tool = (MarqueeTool) WyldCard.getInstance().getToolsManager().forceToolSelection(ToolType.SELECT, false);
                tool.createSelection(importedImage, new Point(cardCenterX - importedImage.getWidth() / 2, cardCenterY - importedImage.getHeight() / 2));
            }
        } catch (IOException e) {
            throw new HtSemanticException("Couldn't import paint.");
        }
    }

    /**
     * Given an image dimension and a boundary dimension in which to constrain the image, this method returns the
     * largest, aspect-ratio preserved size that fits inside the boundary dimension.
     *
     * @param imgSize The size of the image
     * @param boundary The size of the canvas on which to constrain it
     * @return The largest aspect-preserved size fitting within boundary.
     */
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

    /**
     * Scales a given image while preserving its aspect ratio such that the larger of its dimensions do not exceed
     * the given max width or height.
     *
     * @param image The image to scale
     * @param maxWidth The max width allowed for the scaled image
     * @param maxHeight The max height allowed for the scaled image
     * @return The scaled image
     */
    private static BufferedImage scaleImageToDimension(BufferedImage image, int maxWidth, int maxHeight) {
        Dimension scaledDimension = getScaledDimension(new Dimension(image.getWidth(), image.getHeight()), new Dimension(maxWidth, maxHeight));
        BufferedImage resized = new BufferedImage(scaledDimension.width, scaledDimension.height, image.getType());
        Graphics g = resized.getGraphics();
        g.drawImage(image, 0, 0, scaledDimension.width, scaledDimension.height, null);
        g.dispose();

        return resized;
    }

    /**
     * Determines if the extension present on the given filename refers to a writable paint format (i.e., 'png' or
     * 'jpg'). Assumes 'png' if the filename does not have an extension.
     *
     * @param fileName The filename
     * @return True if the filename appears to be a supported image format; false otherwise
     */
    private static boolean isFileSupportedForExporting(String fileName) {
        String suffix = getFileSuffix(fileName, "png");

        for (String thisSuffix : ImageIO.getWriterFileSuffixes()) {
            if (thisSuffix.equalsIgnoreCase(suffix)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the extension present on the given filename refers to a readable paint format (i.e., 'png' or
     * 'jpg').
     *
     * @param fileName The filename
     * @return True if the filename appears to be a supported image format; false otherwise
     */
    private static boolean isFileSupportedForImporting(String fileName) {
        String suffix = getFileSuffix(fileName, null);

        for (String thisSuffix : ImageIO.getReaderFileSuffixes()) {
            if (thisSuffix.equalsIgnoreCase(suffix)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the suffix (i.e., all characters after the last '.') of the given filename.
     * @param fileName The filename whose suffix should be returned
     * @param dflt The value to return if the filename has no suffix; may be null
     * @return The filename's suffix or the dlft value if the file has no suffix
     */
    private static String getFileSuffix(String fileName, String dflt) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i + 1);
        }

        return dflt;
    }
}
