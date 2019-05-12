package com.defano.wyldcard.serializer;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Base64;

/**
 * Used to serialize/deserialize an array of bytes into a Base64-encoded string.
 */
class ByteArraySerializer implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
    public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Base64.getDecoder().decode(json.getAsString());
    }

    public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(Base64.getEncoder().encodeToString(src));
    }
}
