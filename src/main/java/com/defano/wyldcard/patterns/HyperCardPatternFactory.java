package com.defano.wyldcard.patterns;

import com.defano.wyldcard.runtime.context.ToolsContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

public class HyperCardPatternFactory {

    private final static HyperCardPatternFactory instance = new HyperCardPatternFactory();

    private final static int SPRITE_MATRIX_WIDTH = 17;
    private final static int SPRITE_MATRIX_HEIGHT = 12;

    private final static int PATTERN_WIDTH = 8;
    private final static int PATTERN_HEIGHT = 8;

    private HashMap<Integer, TexturePaint> patternCache = new HashMap<>();

    private HyperCardPatternFactory() {
        invalidatePatternCache();
    }

    public static HyperCardPatternFactory getInstance() {
        return instance;
    }

    public void invalidatePatternCache() {
        patternCache.clear();

        for (int index = 0; index < 40; index++) {
            patternCache.put(index, create(index));
        }
    }

    public TexturePaint getPattern(int id) {
        if (id > 39) {
            throw new IllegalArgumentException("No such pattern. Patterns are numbered 0 to 39.");
        }

        return patternCache.get(id);
    }

    private static TexturePaint create(int id) {
        int row = (id / 4);
        int column = (id % 4);

        int xOffset = 1 + (column * SPRITE_MATRIX_WIDTH) + column;
        int yOffset = 1 + (row * SPRITE_MATRIX_HEIGHT) + row;

        try {
            BufferedImage spriteSheet = ImageIO.read(HyperCardPatternFactory.class.getResource("/patterns/patterns.png"));
            BufferedImage sprite = spriteSheet.getSubimage(xOffset, yOffset, PATTERN_WIDTH, PATTERN_HEIGHT);

            for (int x = 0; x < sprite.getWidth(); x++) {
                for (int y = 0; y < sprite.getHeight(); y++) {
                    if (sprite.getRGB(x, y) == 0xffffffff) {
                        sprite.setRGB(x, y, ToolsContext.getInstance().getBackgroundColor().getRGB());
                    } else if (sprite.getRGB(x, y) == 0xff000000) {
                        sprite.setRGB(x, y, ToolsContext.getInstance().getForegroundColor().getRGB());
                    }
                }
            }

            return new TexturePaint(sprite, new Rectangle(0,0,sprite.getWidth(),sprite.getHeight()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read patterns.", e);
        }
    }

}
