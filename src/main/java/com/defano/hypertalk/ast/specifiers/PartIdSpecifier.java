/*
 * PartIdSpecifier
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * PartIdSpecifier.java
 * @author matt.defano@gmail.com
 * 
 * ID-based specification of a part, for example "field id 22"
 */

package com.defano.hypertalk.ast.specifiers;

import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;


public class PartIdSpecifier implements PartSpecifier {

    public final Owner layer;
    public final PartType type;
    public final int id;

    public PartIdSpecifier(Owner layer, PartType type, int id) {
        this.layer = layer;
        this.type = type;
        this.id = id;
    }

    @Override
    public Owner owner() {
        return layer;
    }

    @Override
    public PartType type () {
        return type;
    }

    @Override
    public Integer value () {
        return id;
    }

    @Override
    public String toString () {
        if (owner() == null) {
            return type + " id " + id;
        } else {
            return owner().name() + " " + type + " id " + id;
        }
    }
}
