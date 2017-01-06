package hypercard.parts.model;

import hypercard.parts.Part;
import hypercard.parts.PartException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CardModel {

    private int backgroundId = 0;
    private Collection<ButtonModel> buttonModels;
    private Collection<FieldModel> fieldModels;
    private byte[] cardImage;

    private CardModel () {
        buttonModels = new ArrayList<>();
        fieldModels = new ArrayList<>();
    }

    public static CardModel emptyCardModel () {
        return new CardModel();
    }

    public Collection<AbstractPartModel> getPartModels() {
        List<AbstractPartModel> partModels = new ArrayList<>();
        partModels.addAll(buttonModels);
        partModels.addAll(fieldModels);
        return partModels;
    }

    public void removePart (Part part) {
        switch (part.getType()) {
            case BUTTON:
                buttonModels.remove(part.getPartModel());
                break;
            case FIELD:
                fieldModels.remove(part.getPartModel());
                break;
        }
    }

    public void addPart (Part part) throws PartException {
        switch (part.getType()) {
            case BUTTON:
                buttonModels.add((ButtonModel) part.getPartModel());
                break;
            case FIELD:
                fieldModels.add((FieldModel) part.getPartModel());
                break;
            default:
                throw new PartException("Unsupported part type: " + part.getType());
        }
    }

    public int getBackgroundId() {
        return backgroundId;
    }

    public void setBackgroundId(int backgroundId) {
        this.backgroundId = backgroundId;
    }

    public void setCardImage(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            baos.flush();
            this.cardImage = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while trying to save the card image.", e);
        }
    }

    public BufferedImage getCardImage() {
        if (cardImage == null || cardImage.length == 0) {
            return new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        } else {
            try {
                ByteArrayInputStream stream = new ByteArrayInputStream(cardImage);
                return ImageIO.read(stream);
            } catch (IOException e) {
                throw new RuntimeException("An error occurred while reading the card image. The stack may be corrupted.", e);
            }
        }
    }
}
