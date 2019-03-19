package com.defano.wyldcard.runtime;

import com.defano.wyldcard.parts.Part;
import com.defano.wyldcard.parts.model.PartModel;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class PartsTable<T extends Part> {

    private final ConcurrentHashMap<PartModel,T> parts = new ConcurrentHashMap<>();

    public void removePart(T p) {
        parts.remove(p.getPartModel());
    }

    public void addPart(T p) {
        parts.put(p.getPartModel(), p);
    }

    public Collection<T> getParts() {
        return parts.values();
    }

    public T getPart(PartModel model) {
        return parts.get(model);
    }

}
