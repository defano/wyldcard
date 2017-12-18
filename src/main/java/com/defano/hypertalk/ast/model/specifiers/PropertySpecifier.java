package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.Chunk;
import com.defano.hypertalk.ast.expressions.containers.MenuItemExp;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
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
            PartExp factor = partExp.factor(PartExp.class);
            return factor == null ? null : factor.getChunk();
        }
    }

    public PartExp getPartExp() throws HtException {
        if (partExp == null) {
            return null;
        } else {
            return partExp.factor(PartExp.class, new HtSemanticException("Expected a part here."));
        }
    }

    public MenuItemSpecifier getMenuItem() {
        if (partExp == null) {
            return null;
        } else {
            MenuItemExp factor = partExp.factor(MenuItemExp.class);
            return factor == null ? null : factor.item;
        }
    }
}
