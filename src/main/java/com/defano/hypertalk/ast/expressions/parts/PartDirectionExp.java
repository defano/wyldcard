package com.defano.hypertalk.ast.expressions.parts;

import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.enums.Direction;
import com.defano.hypertalk.ast.model.specifiers.PartDirectionSpecifier;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartDirectionExp extends PartExp {

    private final Direction direction;

    public PartDirectionExp(ParserRuleContext context, Direction direction) {
        super(context);
        this.direction = direction;
    }

    @Override
    public PartDirectionSpecifier evaluateAsSpecifier(ExecutionContext context) {
        return new PartDirectionSpecifier(direction);
    }
}
