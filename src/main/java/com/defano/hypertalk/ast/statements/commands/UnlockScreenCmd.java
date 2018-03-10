package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.fx.CurtainManager;
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
    public void onExecute() throws HtException {
        if (effectExp != null) {
            CurtainManager.getInstance().unlockScreenWithEffect(effectExp.factor(VisualEffectExp.class, new HtSemanticException("Not a visual effect.")).effectSpecifier);
            CurtainManager.getInstance().waitForEffectToFinish();
            return;
        }

        VisualEffectSpecifier ves = ExecutionContext.getContext().getVisualEffect();
        CurtainManager.getInstance().unlockScreenWithEffect(ves);
        CurtainManager.getInstance().waitForEffectToFinish();
    }
}
