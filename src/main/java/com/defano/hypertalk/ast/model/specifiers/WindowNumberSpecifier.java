package com.defano.hypertalk.ast.model.specifiers;

import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.WyldCardFrame;

import java.util.List;

public class WindowNumberSpecifier extends WindowSpecifier {

    private final int windowNumber;

    public WindowNumberSpecifier(int windowNumber) {
        this.windowNumber = windowNumber;
    }

    @Override
    public WyldCardFrame find(ExecutionContext context, List<WyldCardFrame> windows) throws PartException {
        if (windowNumber < 1 || windowNumber >= windows.size()) {
            throw new PartException("No such window.");
        } else {
            return windows.get(windowNumber - 1);
        }
    }

    @Override
    public Object getValue() {
        return windowNumber;
    }
}
