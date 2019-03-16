package com.defano.wyldcard.icons;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Base64;

public class HyperCardIcon implements ButtonIcon {

    private final int resourceId;
    private final String resourceName;
    private final int resourceFlags;
    private final String resourceData;
    private BufferedImage image;

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

            image = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
            byte[] data = Base64.getDecoder().decode(resourceData.replaceAll("\\s", ""));

            for (int col = 0; col < 32; col++) {
                for (int row = 0; row < 32; row++) {
                    int byteIdx = (col * 32 + row) / 8;
                    int bitIdx = 7 - ((col * 32 + row) % 8);

                    image.setRGB(row, col, (data[byteIdx] & (0x01 << bitIdx)) == 0 ? 0x00ffffff : 0xff000000);
                }
            }

            return image;
        }
    }
}
