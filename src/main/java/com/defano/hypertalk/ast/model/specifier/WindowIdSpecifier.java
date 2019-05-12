package com.defano.hypertalk.ast.model.specifier;

import com.defano.hypertalk.exception.HtNoSuchPartException;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.window.WyldCardFrame;
import com.defano.wyldcard.window.WyldCardWindow;

import java.util.List;
import java.util.Optional;

public class WindowIdSpecifier extends WindowSpecifier {

    private final int id;

    public WindowIdSpecifier(int id) {
        this.id = id;
    }

    @Override
    public WyldCardFrame find(ExecutionContext context, List<WyldCardFrame> frames) throws HtNoSuchPartException {
        Optional<WyldCardFrame> foundWindow = frames.stream()
                .filter(p -> p instanceof WyldCardWindow)
                .filter(p -> System.identityHashCode(p) == id)
                .findFirst();

        if (foundWindow.isPresent()) {
            return foundWindow.get();
        } else {
            throw new HtNoSuchPartException("No such window.");
        }

    }

    @Override
    public Object getValue() {
        return id;
    }
}
