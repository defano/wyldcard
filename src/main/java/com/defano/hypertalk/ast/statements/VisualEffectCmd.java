package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.VisualEffectSpecifier;
import com.defano.hypertalk.exception.HtException;

public class VisualEffectCmd extends Command {

    private final VisualEffectSpecifier visualEffect;

    public VisualEffectCmd(VisualEffectSpecifier visualEffect) {
        super("visual");
        this.visualEffect = visualEffect;
    }

    @Override
    public void onExecute() throws HtException {
        ExecutionContext.getContext().setVisualEffect(visualEffect);
    }
}
