/*
 * PropertySpecifier
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.containers;

import com.defano.hypertalk.ast.expressions.PartExp;

public class PropertySpecifier {

    public final String property;
    public final PartExp partExp;

    public PropertySpecifier (String globalProperty) {
        this.property = globalProperty;
        this.partExp = null;
    }

    public PropertySpecifier (String property, PartExp partSpecifier) {
        this.property = property;
        this.partExp = partSpecifier;
    }

    public boolean isGlobalPropertySpecifier() {
        return partExp == null;
    }

}
