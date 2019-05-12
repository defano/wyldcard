package com.defano.wyldcard.part.card;

import com.defano.wyldcard.part.Part;
import com.defano.wyldcard.part.model.PartModel;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class PartTable<PartType extends Part> {

    private final ConcurrentHashMap<PartModel, PartType> parts = new ConcurrentHashMap<>();

    public void remove(PartType p) {
        parts.remove(p.getPartModel());
    }

    public void add(PartType p) {
        parts.put(p.getPartModel(), p);
    }

    public Collection<PartType> getAll() {
        return parts.values();
    }

    public PartType get(PartModel model) {
        return parts.get(model);
    }

}
