/*
 * PartSpecifier
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * PartSpecifier.java
 * @author matt.defano@gmail.com
 * 
 * Abstract superclass for part specifiers
 */

package com.defano.hypertalk.ast.containers;

import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;

public interface PartSpecifier {
    Object value();
    Owner layer();
    PartType type();

    default boolean isCardElementSpecifier() {
        return type() == PartType.BUTTON || type() == PartType.FIELD;
    }

    default boolean isStackElementSpecifier() {
        return type() == PartType.CARD || type() == PartType.BACKGROUND;
    }

    default boolean isHyperCardElementSpecifier() {
        return type() == PartType.MESSAGEBOX;
    }
}
