package com.defano.wyldcard.serializer;

import com.defano.hypertalk.ast.model.Value;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Used to serialize/deserialize a Value object as a primitive. Replaces JSON like "visible":{"value":"true"} with
 * "visible":true.
 */
class ValueSerializer implements JsonSerializer<Value>, JsonDeserializer<Value> {
    @Override
    public Value deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return new Value(json.getAsString());
    }

    @Override
    public JsonElement serialize(Value src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}
