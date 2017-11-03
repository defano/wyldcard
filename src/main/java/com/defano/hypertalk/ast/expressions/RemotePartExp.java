package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.specifiers.RemotePartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class RemotePartExp extends PartExp {

    private final PartExp partExp;
    private final PartExp ofPartExp;

    public RemotePartExp(ParserRuleContext context, PartExp partExp, PartExp ofPartExp) {
        super(context);

        this.partExp = partExp;
        this.ofPartExp = ofPartExp;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier() throws HtException {
        return new RemotePartSpecifier(partExp.evaluateAsSpecifier(), ofPartExp);
    }

    @Override
    protected Value onEvaluate() throws HtException {
        try {
            return ExecutionContext.getContext().getPart(evaluateAsSpecifier()).getValue();
        } catch (Exception e) {
            throw new HtSemanticException("Can't get that part.");
        }
    }
}
