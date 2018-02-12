package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypercard.runtime.interpreter.Interpreter;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class DoCmd extends Command {

    public final Expression script;
    
    public DoCmd(ParserRuleContext context, Expression script) {
        super(context, "do");
        this.script = script;
    }
    
    public void onExecute () throws HtException {
        Interpreter.executeString(null, script.evaluate().toString()).checkedGet();
    }
}
