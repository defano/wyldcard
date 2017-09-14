package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.fx.CurtainManager;
import com.defano.hypertalk.ast.common.VisualEffectSpecifier;
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
            Thread.interrupted();
        }
    }
}
