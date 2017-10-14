package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.parts.PartException;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Position;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartPositionSpecifier;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;
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
    public PartSpecifier evaluateAsSpecifier() throws HtSemanticException {
        return new PartPositionSpecifier(Owner.STACK, type, position);
    }

    @Override
    public Value onEvaluate() throws HtSemanticException {
        try {
            return ExecutionContext.getContext().getPart(evaluateAsSpecifier()).getValue();
        } catch (PartException e) {
            throw new HtSemanticException("Can't get that part.");
        }
    }
}
