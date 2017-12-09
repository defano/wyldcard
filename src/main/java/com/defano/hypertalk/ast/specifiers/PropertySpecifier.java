package com.defano.hypertalk.ast.specifiers;

import com.defano.hypertalk.ast.common.Chunk;
import com.defano.hypertalk.ast.containers.MenuItemContainerExp;
import com.defano.hypertalk.ast.containers.PartContainerExp;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

public class PropertySpecifier {

    private final String property;
    private final Expression partExp;

    public PropertySpecifier (String globalProperty) {
        this(globalProperty, null);
    }

    public PropertySpecifier (String property, Expression part) {
        this.property = property;
        this.partExp = part;
    }

    public boolean isGlobalPropertySpecifier() {
        return partExp == null && getMenuItem() == null;
    }

    public boolean isMenuItemPropertySpecifier() {
        return getMenuItem() != null;
    }

    public boolean isChunkPropertySpecifier() {
        return getChunk() != null;
    }

    public String getProperty() {
        return property;
    }

    public Chunk getChunk() {
        if (partExp == null) {
            return null;
        } else {
            PartContainerExp factor = partExp.factor(PartContainerExp.class);
            return factor == null ? null : factor.getChunk();
        }
    }

    public PartContainerExp getPartExp() throws HtException {
        if (partExp == null) {
            return null;
        } else {
            return partExp.factor(PartContainerExp.class, new HtSemanticException("Expected a part here."));
        }
    }

    public MenuItemSpecifier getMenuItem() {
        if (partExp == null) {
            return null;
        } else {
            MenuItemContainerExp factor = partExp.factor(MenuItemContainerExp.class);
            return factor == null ? null : factor.item;
        }
    }
}
