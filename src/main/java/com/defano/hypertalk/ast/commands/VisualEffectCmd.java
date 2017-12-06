package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.VisualEffectExp;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class VisualEffectCmd extends Command {

    private final VisualEffectExp visualEffectExp;

    public VisualEffectCmd(ParserRuleContext context, VisualEffectExp visualEffectExp) {
        super(context, "visual");
        this.visualEffectExp = visualEffectExp;
    }

    @Override
    public void onExecute() throws HtException {
        ExecutionContext.getContext().setVisualEffect(visualEffectExp.evaluateAsVisualEffect());
    }
}
