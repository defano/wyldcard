package hypercard.parts.model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BackgroundModel {
    private byte[] backgroundImage;

    private BackgroundModel() {}

    public static BackgroundModel emptyBackground() {
        return new BackgroundModel();
    }

    public void setBackgroundImage(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            baos.flush();
            this.backgroundImage = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while trying to save the card image.", e);
        }
    }

    public BufferedImage getBackgroundImage() {
        if (backgroundImage == null || backgroundImage.length == 0) {
            return new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        } else {
            try {
                ByteArrayInputStream stream = new ByteArrayInputStream(backgroundImage);
                return ImageIO.read(stream);
            } catch (IOException e) {
                throw new RuntimeException("An error occurred while reading the card image. The stack may be corrupted.", e);
            }
        }
    }

}
