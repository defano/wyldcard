package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class ParamsFunc extends Expression {

    public ParamsFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    public Value onEvaluate(ExecutionContext context) {
        List<Value> params = context.getStackFrame().getParams();
        StringBuilder paramList = new StringBuilder();

        for (int index = 0; index < params.size(); index++) {
            paramList.append(params.get(index).toString());
            if (index != params.size() - 1) {
                paramList.append(", ");
            }
        }

        return new Value(paramList.toString());
    }
}
