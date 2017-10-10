/*
 * PropertySpecifier
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.specifiers;

import com.defano.hypertalk.ast.common.Chunk;
import com.defano.hypertalk.ast.expressions.PartExp;

public class PropertySpecifier {

    public final String property;
    public final Chunk chunk;
    public final PartExp partExp;
    public final MenuItemSpecifier menuItem;

    public PropertySpecifier (String globalProperty) {
        this(globalProperty, null, null, null);
    }

    public PropertySpecifier (String property, Chunk chunk, PartExp partSpecifier) {
        this(property, chunk, partSpecifier, null);
    }

    public PropertySpecifier (String property, PartExp partSpecifier) {
        this(property, null, partSpecifier, null);
    }

    public PropertySpecifier (String property, MenuItemSpecifier menuItem) {
        this(property, null, null, menuItem);
    }

    private PropertySpecifier (String property, Chunk chunk, PartExp part, MenuItemSpecifier menuItem) {
        this.property = property;
        this.chunk = chunk;
        this.partExp = part;
        this.menuItem = menuItem;
    }

    public boolean isGlobalPropertySpecifier() {
        return partExp == null && menuItem == null;
    }

    public boolean isMenuItemPropertySpecifier() {
        return menuItem != null;
    }

    public boolean isChunkPropertySpecifier() {
        return chunk != null;
    }
}
