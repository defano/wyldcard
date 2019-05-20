package com.defano.wyldcard.serializer;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.property.PropertyList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.text.StyledDocument;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * A utility for serializing/de-serializing WyldCard objects.
 */
public class Serializer {

    private Serializer() {
    }

    private final static Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new PostConstructAdapterFactory())
            .registerTypeAdapter(Value.class, new ValueSerializer())
            .registerTypeAdapter(byte[].class, new ByteArraySerializer())
            .registerTypeAdapter(BufferedImage.class, new BufferedImageSerializer())
            .registerTypeAdapter(StyledDocument.class, new StyledDocumentSerializer())
            .registerTypeAdapter(PropertyList.class, new PropertyListSerializer())
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .create();

    @SuppressWarnings("unchecked")
    public static <T> T copy(T t) {
        return (T) deserialize(serialize(t), t.getClass());
    }

    /**
     * Serializes the contents of an Object to a JSON-formatted string.
     *
     * @param object The object graph to serialize.
     * @return The serialized, JSON-formatted string.
     */
    public static String serialize(Object object) {
        return gson.toJson(object);
    }

    /**
     * Serializes the contents of an Object to a file.
     *
     * @param file The file that should be written with the JSON-formatted serialization data.
     * @param object The object graph to be serialized. Object graph cannot contain cycles!
     * @throws IOException Thrown if an error occurs serializing the data or writing it to the file.
     */
    public static void serialize (File file, Object object) throws IOException {
        Files.write(file.toPath(), serialize(object).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Attempts to deserialize a JSON-formatted string into an Object of the requested type.
     *
     * @param json A JSON string to deserialize; typically generated using the {@link #serialize(Object)} method.
     * @param clazz The class of object to deserialize into.
     * @param <T> A type representing the deserialized object class.
     * @return A deserialized representation of the given file.
     */
    private static <T> T deserialize(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    /**
     * Attempts to deserialize the contents of a file into an Object of the requested type.
     *
     * @param file The file to deserialize; should a plain-text, JSON-formatted file generated using the
     *             {@link #serialize(File, Object)} method.
     * @param clazz The class of object to deserialize into.
     * @param <T> A type representing the deserialized object class.
     * @return A deserialized representation of the given file.
     */
    public static <T> T deserialize (File file, Class<T> clazz) {
        try {
            return deserialize(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8), clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the file. The file may be corrupted.", e);
        }
    }

}
