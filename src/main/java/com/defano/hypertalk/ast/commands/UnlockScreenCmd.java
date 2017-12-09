package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.fx.CurtainManager;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.VisualEffectExp;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class UnlockScreenCmd extends Command {

    private final Expression effectExp;

    public UnlockScreenCmd(ParserRuleContext context, Expression effectExp) {
        super(context, "unlock");
        this.effectExp = effectExp;
    }

    @Override
    public void onExecute() throws HtException {
        CurtainManager.getInstance().unlockScreenWithEffect(effectExp.factor(VisualEffectExp.class, new HtSemanticException("Not a visual effect.")).effectSpecifier);

        try {
            CurtainManager.getInstance().waitForEffectToFinish();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
