package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.context.ExecutionContext;
import com.defano.hypertalk.ast.common.VisualEffectSpecifier;
import com.defano.hypertalk.exception.HtException;

public class VisualEffectCmd extends Statement {

    private final VisualEffectSpecifier visualEffect;

    public VisualEffectCmd(VisualEffectSpecifier visualEffect) {
        this.visualEffect = visualEffect;
    }

    @Override
    public void execute() throws HtException {
        ExecutionContext.getContext().setVisualEffect(visualEffect);
    }
}
