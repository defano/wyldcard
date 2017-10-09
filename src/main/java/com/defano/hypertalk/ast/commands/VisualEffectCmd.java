package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.specifiers.VisualEffectSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class VisualEffectCmd extends Command {

    private final VisualEffectSpecifier visualEffect;

    public VisualEffectCmd(ParserRuleContext context, VisualEffectSpecifier visualEffect) {
        super(context, "visual");
        this.visualEffect = visualEffect;
    }

    @Override
    public void onExecute() throws HtException {
        ExecutionContext.getContext().setVisualEffect(visualEffect);
    }
}
