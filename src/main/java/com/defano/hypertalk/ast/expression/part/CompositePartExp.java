package com.defano.hypertalk.ast.expression.part;

import com.defano.hypertalk.ast.expression.container.PartExp;
import com.defano.hypertalk.ast.model.specifier.CompositePartSpecifier;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * An expression referring to a button or field not on the presently active card. For example, 'the first card
 * button of the next card).
 */
public class CompositePartExp extends PartExp {

    private final PartExp partExp;
    private final PartExp ofPartExp;

    public CompositePartExp(ParserRuleContext context, PartExp partExp, PartExp ofPartExp) {
        super(context);

        this.partExp = partExp;
        this.ofPartExp = ofPartExp;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier(ExecutionContext context) throws HtException {
        return new CompositePartSpecifier(context, partExp.evaluateAsSpecifier(context), ofPartExp);
    }
}
