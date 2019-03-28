package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.compiler.Compiler;
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
    
    public void onExecute(ExecutionContext context) throws HtException {
        PartSpecifier target = context.getCurrentStack().getDisplayedCard().getPartModel().getPartSpecifier(context);
        Compiler.asyncExecuteString(context, target, script.evaluate(context).toString()).checkedGet();
    }
}
