package com.defano.wyldcard.pattern;

import com.defano.wyldcard.WyldCard;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class WyldCardPatternFactory {

    public static final int PATTERN_WIDTH = 8;
    public static final int PATTERN_HEIGHT = 8;

    // Pattern bitmaps, base 64 encoded
    private static final String PATTERN_DATA =
                    "ACgAAAAAAAAAAIAAAAAIAAAAiAAiAIgAIgCIiCIiiIgiIoiq" +
                    "IqqIqiKqzKozqsyqM6ruqruq7qq7qu67u+7uu7vu/7v/7v+7" +
                    "/+7/u////7v//4AQAiABCEAE//////////+IIogiiCKIIhEi" +
                    "RIgRIkSIxIAMaEMCMCaxMAMb2MAMjaoAqgCqAKoAiCJVIogi" +
                    "VSKIVSJViFUiVXfdd9133XfdgAAAAAAAAACqVapVqlWqVQOE" +
                    "SDAMAgEBgkQ5RIIBAQGIFCJBiEEiFICAQT4ICBTjIgSMdCIX" +
                    "mBC+gIgI6wiIgCXIMolkJEySopxBvirJFOtAoAAABAoAAIBA" +
                    "IAACBAgAqgCAAIgAgAD/gICAgICAgAgcIsGAAQIE/4CAgP8I" +
                    "CAj4dCJHjxcicb8Av7+wsLCw/3++XaJBgAD69fr1oFCgUA==";

    private static final WyldCardPatternFactory instance = new WyldCardPatternFactory();
    byte[] patternBytes = Base64.getDecoder().decode(PATTERN_DATA);
    private List<PatternInvalidatonObserver> observers = new ArrayList<>();
    private HashMap<Integer, TexturePaint> patternCache = new HashMap<>();

    private WyldCardPatternFactory() {}

    public static WyldCardPatternFactory getInstance() {
        return instance;
    }

    public synchronized void invalidatePatternCache() {
        patternCache.clear();

        for (int index = 0; index < 40; index++) {
            patternCache.put(index, create(index));
        }

        fireObservers();
    }

    public synchronized TexturePaint getPattern(int id) {
        if (patternCache.isEmpty()) {
            invalidatePatternCache();
        }

        if (id > 39) {
            throw new IllegalArgumentException("No such pattern. Patterns are numbered 0 to 39.");
        }

        TexturePaint paint = patternCache.get(id);

        if (paint == null) {
            throw new IllegalStateException("Bug! Pattern is null for id: " + id);
        }

        return paint;
    }

    public void addPatternInvalidationObserver(PatternInvalidatonObserver observer) {
        this.observers.add(observer);
    }

    private TexturePaint create(int id) {

        BufferedImage pattern = getPatternImage(id);
        for (int x = 0; x < pattern.getWidth(); x++) {
            for (int y = 0; y < pattern.getHeight(); y++) {
                if (pattern.getRGB(x, y) == 0xffffffff) {
                    pattern.setRGB(x, y, WyldCard.getInstance().getPaintManager().getBackgroundColor().getRGB());
                } else if (pattern.getRGB(x, y) == 0xff000000) {
                    pattern.setRGB(x, y, WyldCard.getInstance().getPaintManager().getForegroundColor().getRGB());
                }
            }
        }

        return new TexturePaint(pattern, new Rectangle(0, 0, pattern.getWidth(), pattern.getHeight()));
    }

    private BufferedImage getPatternImage(int id) {

        // See if user has edited pattern, if so, return their copy
        if (WyldCard.getInstance().getStackManager().getFocusedStack() != null) {
            BufferedImage userPattern = WyldCard.getInstance().getStackManager().getFocusedStack().getStackModel().getUserPattern(id);
            if (userPattern != null) {
                return userPattern;
            }
        }

        // Get system pattern image; note that first two bytes of pattern data should be ignored
        long pattern = ((long) (patternBytes[(id * 8) + 9]) & 0xff) |
                ((long) (patternBytes[(id * 8) + 8] & 0xff) << 8) |
                ((long) (patternBytes[(id * 8) + 7] & 0xff) << 16) |
                ((long) (patternBytes[(id * 8) + 6] & 0xff) << 24) |
                ((long) (patternBytes[(id * 8) + 5] & 0xff) << 32) |
                ((long) (patternBytes[(id * 8) + 4] & 0xff) << 40) |
                ((long) (patternBytes[(id * 8) + 3] & 0xff) << 48) |
                ((long) (patternBytes[(id * 8) + 2] & 0xff) << 56);

        return decodePattern(pattern);
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private BufferedImage decodePattern(long pattern) {
        int[] pixels = new int[64];

        for (int row = 0; row < 8; row++) {
            byte rowBits = (byte) ((pattern & (0xffL << (row * 8))) >> (row * 8));

            pixels[row * 8 + 7] = ((rowBits & 0x80) > 0) ? 0xFF000000 : 0xFFFFFFFF;
            pixels[row * 8 + 6] = ((rowBits & 0x40) > 0) ? 0xFF000000 : 0xFFFFFFFF;
            pixels[row * 8 + 5] = ((rowBits & 0x20) > 0) ? 0xFF000000 : 0xFFFFFFFF;
            pixels[row * 8 + 4] = ((rowBits & 0x10) > 0) ? 0xFF000000 : 0xFFFFFFFF;
            pixels[row * 8 + 3] = ((rowBits & 0x08) > 0) ? 0xFF000000 : 0xFFFFFFFF;
            pixels[row * 8 + 2] = ((rowBits & 0x04) > 0) ? 0xFF000000 : 0xFFFFFFFF;
            pixels[row * 8 + 1] = ((rowBits & 0x02) > 0) ? 0xFF000000 : 0xFFFFFFFF;
            pixels[row * 8 + 0] = ((rowBits & 0x01) > 0) ? 0xFF000000 : 0xFFFFFFFF;
        }

        BufferedImage image = new BufferedImage(PATTERN_WIDTH, PATTERN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, PATTERN_WIDTH, PATTERN_HEIGHT, pixels, 0, 8);

        return image;
    }

    private void fireObservers() {
        SwingUtilities.invokeLater(() -> {
            for (PatternInvalidatonObserver observer : observers) {
                observer.patternsInvalidated();
            }
        });
    }

}
