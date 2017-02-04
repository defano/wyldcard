package hypercard.patterns;

import hypercard.context.ToolsContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class HyperCardPatternFactory {

    private final static int SPRITE_MATRIX_WIDTH = 17;
    private final static int SPRITE_MATRIX_HEIGHT = 12;

    private final static int PATTERN_WIDTH = 8;
    private final static int PATTERN_HEIGHT = 8;

    public static TexturePaint create (int id) {

        if (id > 39) {
            throw new IllegalArgumentException("No such pattern. Patterns are numbered 0 to 39.");
        }

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
