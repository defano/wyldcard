package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.PartContainerExp;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.specifiers.RemotePartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * An expression referring to a button or field not on the presently active card. For example, 'the first card
 * button of the next card).
 */
public class RemotePartExp extends PartContainerExp {

    private final PartContainerExp partExp;
    private final PartContainerExp ofPartExp;

    public RemotePartExp(ParserRuleContext context, PartContainerExp partExp, PartContainerExp ofPartExp) {
        super(context);

        this.partExp = partExp;
        this.ofPartExp = ofPartExp;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier() throws HtException {
        return new RemotePartSpecifier(partExp.evaluateAsSpecifier(), ofPartExp);
    }

    @Override
    public Value onEvaluate() throws HtException {
        try {
            return ExecutionContext.getContext().getPart(evaluateAsSpecifier()).getValue();
        } catch (Exception e) {
            throw new HtSemanticException("Can't get that part.");
        }
    }
}
