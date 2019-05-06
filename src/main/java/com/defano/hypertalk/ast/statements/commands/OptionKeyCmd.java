package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.awt.keyboard.ModifierKey;
import com.defano.wyldcard.awt.keyboard.RoboticTypist;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class OptionKeyCmd extends Command {

    private final Expression keyExpr;

    public OptionKeyCmd(ParserRuleContext context, Expression keyExpr) {
        super(context, "optionkey");
        this.keyExpr = keyExpr;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException, Preemption {
        RoboticTypist.getInstance().type(keyExpr.evaluate(context).toString(), ModifierKey.OPTION);
    }
}
