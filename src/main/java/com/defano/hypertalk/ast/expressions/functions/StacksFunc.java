package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;

public class StacksFunc extends Expression {

    @Inject
    private WyldCard wyldCard;

    public StacksFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        ArrayList<Value> stacks = new ArrayList<>();

        for (StackPart thisStack : wyldCard.getOpenStacks()) {
            stacks.add(new Value(thisStack.getStackModel().getStackPath(context)));
        }

        return Value.ofLines(stacks);
    }
}
