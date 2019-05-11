package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.enums.Owner;

public class ButtonFamilySpecifier {

    private final Owner layer;
    private final Expression familyExpr;

    public ButtonFamilySpecifier(Owner layer, Expression familyExpr) {
        this.layer = layer;
        this.familyExpr = familyExpr;
    }

    public Owner getLayer() {
        return layer;
    }

    public Expression getFamilyExpr() {
        return familyExpr;
    }
}
