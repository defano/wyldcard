/*
 * PropertySpecifier
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.specifiers;

import com.defano.hypertalk.ast.expressions.PartExp;

public class PropertySpecifier {

    public final String property;
    public final PartExp partExp;
    public final MenuItemSpecifier menuItem;

    public PropertySpecifier (String globalProperty) {
        this.property = globalProperty;
        this.partExp = null;
        this.menuItem = null;
    }

    public PropertySpecifier (String property, PartExp partSpecifier) {
        this.property = property;
        this.partExp = partSpecifier;
        this.menuItem = null;
    }

    public PropertySpecifier (String property, MenuItemSpecifier menuItem) {
        this.property = property;
        this.menuItem = menuItem;
        this.partExp = null;
    }

    public boolean isGlobalPropertySpecifier() {
        return partExp == null && menuItem == null;
    }

    public boolean isMenuItemPropertySpecifier() {
        return menuItem != null;
    }
}
