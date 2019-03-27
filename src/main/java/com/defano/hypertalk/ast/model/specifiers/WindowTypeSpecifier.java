package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.SingletonWindowType;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.WyldCardFrame;

import java.util.List;

public class WindowTypeSpecifier extends WindowSpecifier {

    private final SingletonWindowType windowType;
    private final ExecutionContext context;

    public WindowTypeSpecifier(ExecutionContext context, SingletonWindowType type) {
        this.windowType = type;
        this.context = context;
    }

    @Override
    public WyldCardFrame find(ExecutionContext context, List<WyldCardFrame> windows) throws PartException {
        return getWindowType().getWindow(context);
    }

    @Override
    public Object getValue() {
        return windowType.getWindow(context).getTitle();
    }

    public SingletonWindowType getWindowType() {
        return windowType;
    }
}
