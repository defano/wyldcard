package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.containers.MenuItemExp;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.Chunk;
import com.defano.hypertalk.ast.model.Adjective;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

public class PropertySpecifier {

    private final String property;
    private final Expression partExp;
    private final Adjective adjective;

    public PropertySpecifier(String globalProperty) {
        this(globalProperty, null);
    }

    public PropertySpecifier(String property, Expression part) {
        this(Adjective.DEFAULT, property, part);
    }

    public PropertySpecifier(Adjective adjective, String property, Expression part) {
        this.property = property;
        this.partExp = part;
        this.adjective = adjective;
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

    public Adjective getAdjective() {
        return adjective;
    }

    public String getProperty() {
        return property;
    }

    /**
     * Returns the name of the specified property with the specified adjective applied (where applicable). For example,
     * applying {@link Adjective#SHORT} to the property 'name' yields 'short name'
     * <p>
     * Certain properties (like name and id) support length adjectives (like 'long', 'short' or 'abbrev') when
     * applied to certain objects. This method attempts to compute an applied property name given a property
     * and adjective.
     * <p>
     * Note that objects may override the default adjective. For example, when requesting 'the name of' a button
     * or field, 'the abbrev name' is actually returned.
     *
     * @return The adjective-applied name of the specified property.
     */
    public String getAdjectiveAppliedPropertyName() {
        PartModel model = getPartModel();

        // Apply adjective only to properties that support it
        if (model != null && model.isAdjectiveSupportedProperty(property)) {
            if (adjective == Adjective.DEFAULT) {
                return model.getDefaultAdjectiveForProperty(property).apply(property);
            } else {
                return adjective.apply(property);
            }
        }

        // Ignore adjective on properties that don't support it (i.e., 'the long width' is the same as 'the width')
        else {
            return property;
        }
    }

    public Chunk getChunk() {
        if (partExp == null) {
            return null;
        } else {
            PartExp factor = partExp.factor(PartExp.class);
            return factor == null ? null : factor.getChunk();
        }
    }

    /**
     * Gets the model of the part specified, or null if this specifier either refers to a non-existent part or
     * doesn't specify a part type at all.
     *
     * @return The model of the part specified by this object, or null
     */
    public PartModel getPartModel() {
        if (partExp == null) {
            return null;
        } else {
            return partExp.partFactor(PartModel.class);
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
