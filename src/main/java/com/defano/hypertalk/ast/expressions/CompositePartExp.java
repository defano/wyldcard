package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.containers.PartContainerExp;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.specifiers.CompositePartSpecifier;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * An expression referring to a button or field not on the presently active card. For example, 'the first card
 * button of the next card).
 */
public class CompositePartExp extends PartContainerExp {

    private final PartContainerExp partExp;
    private final PartContainerExp ofPartExp;

    public CompositePartExp(ParserRuleContext context, PartContainerExp partExp, PartContainerExp ofPartExp) {
        super(context);

        this.partExp = partExp;
        this.ofPartExp = ofPartExp;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier() throws HtException {
        return new CompositePartSpecifier(partExp.evaluateAsSpecifier(), ofPartExp);
    }
}
