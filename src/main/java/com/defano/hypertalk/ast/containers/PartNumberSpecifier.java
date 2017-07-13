package com.defano.hypertalk.ast.containers;

import com.defano.hypertalk.ast.common.PartLayer;
import com.defano.hypertalk.ast.common.PartType;

public class PartNumberSpecifier implements PartSpecifier {

    public PartType type = null;
    public PartLayer layer;
    public int number;

    public PartNumberSpecifier(PartLayer layer, int number) {
        this(layer, null, number);
    }

    public PartNumberSpecifier(PartLayer layer, PartType type, int number) {
        this.layer = layer;
        this.number = number;
        this.type = type;
    }

    @Override
    public Object value() {
        return number;
    }

    @Override
    public PartLayer layer() {
        return layer;
    }

    @Override
    public PartType type() {
        return null;
    }

    @Override
    public String toString() {
        if (type == null) {
            return layer().name() + " part " + number;
        } else {
            return layer().name() + " " + type + " " + number;
        }
    }
}
