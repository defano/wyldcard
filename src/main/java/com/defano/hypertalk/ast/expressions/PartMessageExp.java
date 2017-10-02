package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartMessageSpecifier;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;

public class PartMessageExp extends PartExp {

    @Override
    public PartSpecifier evaluateAsSpecifier() throws HtSemanticException {
        return new PartMessageSpecifier();
    }

    @Override
    public Value evaluate() throws HtSemanticException {
        return new Value(WindowManager.getMessageWindow().getMsgBoxText());
    }
}
