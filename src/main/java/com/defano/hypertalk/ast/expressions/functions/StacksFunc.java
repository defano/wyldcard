package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.layouts.StackWindow;
import com.defano.wyldcard.window.WindowManager;
import org.antlr.v4.runtime.ParserRuleContext;

import java.awt.*;
import java.util.ArrayList;

public class StacksFunc extends Expression {

    public StacksFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        ArrayList<Value> stacks = new ArrayList<>();

        for (Window thisWindow : WindowManager.getInstance().getWindows()) {
            if (thisWindow instanceof StackWindow) {
                stacks.add(new Value(((StackWindow) thisWindow).getStack().getStackModel().getStackPath(context)));
            }
        }

        return Value.ofLines(stacks);
    }
}
