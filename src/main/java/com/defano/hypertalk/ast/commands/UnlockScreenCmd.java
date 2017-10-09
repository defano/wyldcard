package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.fx.CurtainManager;
import com.defano.hypertalk.ast.specifiers.VisualEffectSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class UnlockScreenCmd extends Command {

    private final VisualEffectSpecifier effect;

    public UnlockScreenCmd(ParserRuleContext context, VisualEffectSpecifier effect) {
        super(context, "unlock");
        this.effect = effect;
    }

    @Override
    public void onExecute() throws HtException {
        CurtainManager.getInstance().unlockScreenWithEffect(effect);

        try {
            CurtainManager.getInstance().waitForEffectToFinish();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
