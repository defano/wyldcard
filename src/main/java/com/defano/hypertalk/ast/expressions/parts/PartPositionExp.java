package com.defano.hypertalk.ast.expressions.parts;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Position;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.specifiers.PartPositionSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartPositionExp extends PartExp {

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
    public PartSpecifier evaluateAsSpecifier(ExecutionContext context) {
        return new PartPositionSpecifier(Owner.STACK, type, position);
    }
}
