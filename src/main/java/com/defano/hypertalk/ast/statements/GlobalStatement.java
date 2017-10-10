package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class GlobalStatement extends Statement {

    public final String symbol;
    
    public GlobalStatement(ParserRuleContext context, String symbol) {
        super(context);
        this.symbol = symbol;
    }

    public void onExecute() {
        ExecutionContext.getContext().defineGlobal(symbol);
    }
}
