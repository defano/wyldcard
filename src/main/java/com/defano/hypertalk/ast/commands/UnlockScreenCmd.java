package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.fx.CurtainManager;
import com.defano.hypertalk.ast.expressions.VisualEffectExp;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class UnlockScreenCmd extends Command {

    private final VisualEffectExp effectExp;

    public UnlockScreenCmd(ParserRuleContext context, VisualEffectExp effectExp) {
        super(context, "unlock");
        this.effectExp = effectExp;
    }

    @Override
    public void onExecute() throws HtException {
        CurtainManager.getInstance().unlockScreenWithEffect(effectExp.evaluateAsVisualEffect());

        try {
            CurtainManager.getInstance().waitForEffectToFinish();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
