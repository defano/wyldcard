package com.defano.wyldcard.runtime.serializer;

import com.google.gson.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;

public class BufferedImageSerializer implements JsonSerializer<BufferedImage>, JsonDeserializer<BufferedImage> {

    @Override
    public BufferedImage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        byte[] imageData = Base64.getDecoder().decode(json.getAsString());

        if (imageData == null || imageData.length == 0) {
            throw new IllegalStateException("Bogus image size");
        } else {
            try {
                ByteArrayInputStream stream = new ByteArrayInputStream(imageData);
                return ImageIO.read(stream);
            } catch (IOException e) {
                throw new RuntimeException("An error occurred reading the image. This stack may be corrupted.", e);
            }
        }
    }

    @Override
    public JsonElement serialize(BufferedImage src, Type typeOfSrc, JsonSerializationContext context) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(src, "png", baos);
            baos.flush();
            byte[] serialized = baos.toByteArray();
            baos.close();
            return new JsonPrimitive(Base64.getEncoder().encodeToString(serialized));
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while trying to save the image.", e);
        }
    }

    public static BufferedImage emptyImage(Dimension dimension) {
        return new BufferedImage(dimension.width,dimension.height, BufferedImage.TYPE_INT_ARGB);
    }

}
