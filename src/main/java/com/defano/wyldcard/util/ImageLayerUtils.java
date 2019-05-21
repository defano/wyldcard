package com.defano.wyldcard.util;

import com.defano.jmonet.canvas.layer.ImageLayer;
import com.defano.jmonet.tools.util.ImageUtils;

import java.awt.*;

public class ImageLayerUtils {

    private ImageLayerUtils() {
    }

    public static boolean layersRemovesPaint(ImageLayer... layers) {
        for (ImageLayer layer : layers) {
            if (layerRemovesPaint(layer)) {
                return true;
            }
        }

        return false;
    }

    public static boolean layerRemovesPaint(ImageLayer layer) {
        return ((AlphaComposite)layer.getComposite()).getRule() == AlphaComposite.DST_OUT && !ImageUtils.getMinimumBounds(layer.getImage()).isEmpty();
    }
}
