package com.defano.hypertalk.ast.statement.command;

import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.expression.VisualEffectExp;
import com.defano.hypertalk.ast.model.specifier.VisualEffectSpecifier;
import com.defano.hypertalk.ast.statement.Command;
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
            context.getCurrentStack().getCurtainManager().unlockScreen(context, effectExp.factor(context, VisualEffectExp.class, new HtSemanticException("Not a visual effect.")).evaluateAsSpecifier(context));
            context.getCurrentStack().getCurtainManager().waitForEffectToFinish();
        }

        else {
            // User unlocked screen without visual effect arg ('unlock screen') or specified the effect earlier in the
            // script (via the visual command... 'visual effect dissolve')
            VisualEffectSpecifier ves = context.getVisualEffect();
            context.getCurrentStack().getCurtainManager().unlockScreen(context, ves);
            context.getCurrentStack().getCurtainManager().waitForEffectToFinish();
        }
    }
}
