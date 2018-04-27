package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.specifiers.CompositePartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.StackPartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.WindowManager;
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
