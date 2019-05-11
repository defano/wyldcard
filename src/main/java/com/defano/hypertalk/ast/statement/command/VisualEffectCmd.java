package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.expression.VisualEffectExp;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class VisualEffectCmd extends Command {

    private final Expression visualEffectExp;

    public VisualEffectCmd(ParserRuleContext context, Expression visualEffectExp) {
        super(context, "visual");
        this.visualEffectExp = visualEffectExp;
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException {
        context.getCurrentStack().getCurtainManager().lockScreen(context);
        context.setVisualEffect(visualEffectExp.factor(context, VisualEffectExp.class, new HtSemanticException("Not a visual effect.")).evaluateAsSpecifier(context));
    }
}