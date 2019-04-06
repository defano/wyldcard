package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class ShowAtCmd extends Command {

    private final Expression partExp;
    private final Expression locationExp;

    public ShowAtCmd(ParserRuleContext context, Expression partExp, Expression locationExp) {
        super(context, "show");
        this.partExp = partExp;
        this.locationExp = locationExp;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException, Preemption {
        PartModel part = partExp.partFactor(context, PartModel.class, new HtSemanticException("Cannot show that."));
        Value location = locationExp.evaluate(context);

        if (!location.isPoint()) {
            throw new HtSemanticException("Expected a location, but got " + location.toString());
        }

        part.setKnownProperty(context, PartModel.PROP_LOC, location);
        part.setKnownProperty(context, PartModel.PROP_VISIBLE, new Value(true));
    }
}
