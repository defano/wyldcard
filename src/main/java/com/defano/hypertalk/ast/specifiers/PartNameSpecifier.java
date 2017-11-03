package com.defano.hypertalk.ast.specifiers;

import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;

/**
 * Specifies a button, field, card or background by name. For example, 'card "Nifty Card"' or 'card button 'Press me!"'
 * Note that names are not unique; multiple parts can have the same name. HyperCard assumes the user is referring to
 * the part with the lowest number when ambiguously referring to parts by name.
 */
public class PartNameSpecifier implements PartSpecifier {

    private final Owner layer;
    private final PartType type;
    private final String name;

    public PartNameSpecifier (Owner layer, PartType type, String name) {
        this.layer = layer;
        this.type = type;
        this.name = name;
    }

    @Override
    public Owner getOwner() {
        return layer;
    }

    @Override
    public PartType getType() {
        return type;
    }

    @Override
    public String getValue() {
        return name;
    }

    @Override
    public String getHyperTalkIdentifier() {
        return getOwner().name().toLowerCase() + " " + type.toString().toLowerCase() + " " + name;
    }
}
