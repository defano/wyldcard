package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.VisualEffectExp;
import com.defano.hypertalk.ast.model.specifiers.VisualEffectSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class UnlockScreenCmd extends Command {

    private final Expression effectExp;

    public UnlockScreenCmd(ParserRuleContext context) {
        this(context, null);
    }

    public UnlockScreenCmd(ParserRuleContext context, Expression effectExp) {
        super(context, "unlock");
        this.effectExp = effectExp;
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException {

        // User unlocked screen with form 'unlock screen with visual effect...'
        if (effectExp != null) {
            context.getCurrentStack().getCurtainManager().unlockScreenWithEffect(context, effectExp.factor(context, VisualEffectExp.class, new HtSemanticException("Not a visual effect.")).effectSpecifier);
            context.getCurrentStack().getCurtainManager().waitForEffectToFinish();
        }

        else {
            // User unlocked screen without visual effect arg ('unlock screen') or specified the effect earlier in the
            // script (via the visual command... 'visual effect dissolve')
            VisualEffectSpecifier ves = context.getStackFrame().getVisualEffect();
            context.getCurrentStack().getCurtainManager().unlockScreenWithEffect(context, ves);
            context.getCurrentStack().getCurtainManager().waitForEffectToFinish();
        }
    }
}
