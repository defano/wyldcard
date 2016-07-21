package hypercard;

import com.google.gson.*;
import hypertalk.ast.common.Value;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class Serializer {

    private final static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Value.class, new ValueSerializer())
            .setPrettyPrinting()
            .create();

    public static String serialize (Object object) {
        return gson.toJson(object);
    }

    public static void serialize (File file, Object object) throws IOException {
        Files.write(file.toPath(), serialize(object).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
    }

    public static <T> T deserialize (String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static <T> T deserialize (File file, Class<T> clazz) throws IOException {
        return deserialize(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8), clazz);
    }

    /**
     * Used to serialize/deserialize a Value object as a primitive. Replaces JSON like "visible":{"value":"true"} with
     * "visible":true.
     */
    public static class ValueSerializer implements JsonSerializer<Value>, JsonDeserializer<Value> {
        @Override
        public Value deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new Value(json.getAsString());
        }

        @Override
        public JsonElement serialize(Value src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.stringValue());
        }
    }
}
