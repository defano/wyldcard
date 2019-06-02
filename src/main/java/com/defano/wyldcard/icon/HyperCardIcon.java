package com.defano.wyldcard.icon;

import com.defano.jmonet.tools.attributes.BoundaryFunction;
import com.defano.jmonet.tools.attributes.FillFunction;
import com.defano.jmonet.transform.image.FloodFillTransform;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Base64;

public class HyperCardIcon implements ButtonIcon {

    private final int resourceId;
    private final String resourceName;
    private final int resourceFlags;
    private final String resourceData;
    private BufferedImage image;
    private BufferedImage mask;

    public HyperCardIcon(int resourceId, String resourceName, int resourceFlags, String resourceData) {
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.resourceFlags = resourceFlags;
        this.resourceData = resourceData;
    }

    @SuppressWarnings("unused")
    public int getResourceFlags() {
        return resourceFlags;
    }

    @Override
    public int getId() {
        return resourceId;
    }

    @Override
    public String getName() {
        if (resourceName.isEmpty()) {
            return String.valueOf(resourceId);
        }

        return resourceName;
    }

    @Override
    public Image getImage() {

        // Image has already been decoded
        if (image != null) {
            return image;
        }

        // Build raster by decoding image data
        else {

            image = decodeImage();
            mask = inferMask(image);

            // Apply mask to image
            for (int col = 0; col < 32; col++) {
                for (int row = 0; row < 32; row++) {
                    if ((mask.getRGB(row + 1, col + 1) & 0xff000000) == 0) {
                        image.setRGB(row, col, image.getRGB(row, col) | 0xff000000);
                    }
                }
            }

            return image;
        }
    }

    /**
     * Convert the base-64 encoded bitmap into a 32x32 image in which each cleared bit in the data is represented
     * by a fully transparent pixel, and each set bit in the data results in a fully-opaque black pixel.
     *
     * @return The icon image.
     */
    private BufferedImage decodeImage() {
        BufferedImage image = new BufferedImage(ICON_WIDTH, ICON_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
        byte[] data = Base64.getDecoder().decode(resourceData.replaceAll("\\s", ""));
        for (int col = 0; col < 32; col++) {
            for (int row = 0; row < 32; row++) {
                int byteIdx = (col * 32 + row) / 8;
                int bitIdx = 7 - ((col * 32 + row) % 8);

                image.setRGB(row, col, ((0xff & data[byteIdx]) & (0x01 << bitIdx)) == 0 ? 0x00ffffff : 0xff000000);
            }
        }

        return image;
    }

    /**
     * HyperCard applies an inferred mask to system icons so that clear pixels fully encircled by opaque
     * black pixels appear opaque white, while un-encircled pixels remain transparent.
     *
     * This method performs a flood-fill seeded at the edge of the icon image to create a mask.
     *
     * @param image The 32x32 icon image.
     * @return A mask image in which every opaque pixel in the mask should be made opaque in the source.
     */
    private BufferedImage inferMask(BufferedImage image) {

        // Create a copy of the icon image 1 pixel larger than the source on each edge
        BufferedImage maskImage = new BufferedImage(ICON_WIDTH + 2, ICON_HEIGHT + 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) maskImage.getGraphics();
        g2d.drawImage(image, 1, 1, null);
        g2d.dispose();

        FloodFillTransform transform = new FloodFillTransform();
        transform.setBoundaryFunction(new BoundaryFunction() {});
        transform.setFill(new FillFunction() {
            @Override
            public void fill(BufferedImage image, int x, int y, Paint fillPaint) {
                image.setRGB(x, y, 0xff000000);
            }
        });
        transform.setOrigin(new Point(0, 0));
        return transform.apply(maskImage);
    }

}
