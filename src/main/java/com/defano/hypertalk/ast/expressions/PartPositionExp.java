package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Position;
import com.defano.hypertalk.ast.containers.PartContainerExp;
import com.defano.hypertalk.ast.specifiers.PartPositionSpecifier;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartPositionExp extends PartContainerExp {

    private final PartType type;
    private final Position position;

    public PartPositionExp(ParserRuleContext context, PartType type, Position position) {
        super(context);

        if (type != PartType.BACKGROUND && type != PartType.CARD) {
            throw new IllegalArgumentException("Cannot specify this type by position: " + type);
        }

        this.type = type;
        this.position = position;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier() {
        return new PartPositionSpecifier(Owner.STACK, type, position);
    }
}
