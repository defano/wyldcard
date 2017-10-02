package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.fx.CurtainManager;
import com.defano.hypertalk.ast.specifiers.VisualEffectSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;

public class UnlockScreenCmd extends Command {

    private final VisualEffectSpecifier effect;

    public UnlockScreenCmd(VisualEffectSpecifier effect) {
        super("unlock");
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
