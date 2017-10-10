package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public abstract class PartExp extends Expression {

    public PartExp(ParserRuleContext context) {
        super(context);
    }

    public abstract PartSpecifier evaluateAsSpecifier () throws HtException;
}
