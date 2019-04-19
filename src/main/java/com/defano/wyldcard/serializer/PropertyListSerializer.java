package com.defano.wyldcard.serializer;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.properties.Property;
import com.defano.wyldcard.properties.PropertyList;
import com.defano.wyldcard.properties.value.BasicValue;
import com.defano.wyldcard.properties.value.ConstantValue;
import com.google.gson.*;

import java.lang.reflect.Type;

public class PropertyListSerializer implements JsonSerializer<PropertyList>, JsonDeserializer<PropertyList> {

    @Override
    public PropertyList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        PropertyList list = new PropertyList();
        JsonObject jsonObj = json.getAsJsonObject();

        jsonObj.entrySet().forEach(es -> {
            if (es.getKey().startsWith("_")) {
                list.add(new Property(new ConstantValue(new Value(es.getValue().getAsString())), es.getKey().substring(1)));
            } else {
                list.add(new Property(new BasicValue(new Value(es.getValue().getAsString())), es.getKey()));
            }
        });

        return list;
    }

    @Override
    public JsonElement serialize(PropertyList src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject json = new JsonObject();

        src.forEach(p -> {
            if (p.value() instanceof BasicValue) {
                json.add(p.name(), new JsonPrimitive(((BasicValue) p.value()).rawValue().toString()));
            } else if (p.value() instanceof ConstantValue) {
                json.add("_" + p.name(), new JsonPrimitive(((ConstantValue) p.value()).rawValue().toString()));
            }
        });

        return json;
    }
}

