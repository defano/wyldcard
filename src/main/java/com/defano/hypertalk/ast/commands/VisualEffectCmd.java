package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.VisualEffectExp;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class VisualEffectCmd extends Command {

    private final Expression visualEffectExp;

    public VisualEffectCmd(ParserRuleContext context, Expression visualEffectExp) {
        super(context, "visual");
        this.visualEffectExp = visualEffectExp;
    }

    @Override
    public void onExecute() throws HtException {
        ExecutionContext.getContext().setVisualEffect(visualEffectExp.factor(VisualEffectExp.class, new HtSemanticException("Not a visual effect.")).effectSpecifier);
    }
}
