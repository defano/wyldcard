package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.ParameterList;
import org.antlr.v4.runtime.ParserRuleContext;

public class GlobalStatement extends Statement {

    public final ParameterList symbols;
    
    public GlobalStatement(ParserRuleContext context, ParameterList symbols) {
        super(context);
        this.symbols = symbols;
    }

    public void onExecute() {
        for (String symbol : symbols.list) {
            ExecutionContext.getContext().defineGlobal(symbol);
        }
    }
}
