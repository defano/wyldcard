package com.defano.hypertalk.ast.statements;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.ParameterList;
import org.antlr.v4.runtime.ParserRuleContext;

public class GlobalStatement extends Statement {

    public final ParameterList symbols;
    
    public GlobalStatement(ParserRuleContext context, ParameterList symbols) {
        super(context);
        this.symbols = symbols;
    }

    public void onExecute(ExecutionContext context) {
        for (String symbol : symbols.list) {
            context.defineGlobal(symbol);
        }
    }
}
