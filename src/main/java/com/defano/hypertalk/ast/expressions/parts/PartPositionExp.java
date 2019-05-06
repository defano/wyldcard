package com.defano.hypertalk.ast.expressions.parts;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Position;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.specifiers.CardPositionSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartPositionExp extends PartExp {

    private final PartType type;
    private final Position position;
    private final boolean marked;

    public PartPositionExp(ParserRuleContext context, PartType type, Position position) {
        this(context, type, position, false);
    }

    public PartPositionExp(ParserRuleContext context, PartType type, Position position, boolean marked) {
        super(context);

        if (type != PartType.BACKGROUND && type != PartType.CARD) {
            throw new IllegalArgumentException("Cannot specify this type by position: " + type);
        }

        this.type = type;
        this.position = position;
        this.marked = marked;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier(ExecutionContext context) {
        return new CardPositionSpecifier(Owner.STACK, type, position, marked);
    }
}
