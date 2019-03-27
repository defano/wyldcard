package com.defano.hypertalk.ast.model.specifiers;

import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.WyldCardFrame;

import java.util.List;
import java.util.Optional;

public class WindowNameSpecifier extends WindowSpecifier {

    private final String windowName;

    public WindowNameSpecifier(String windowName) {
        this.windowName = windowName;
    }

    @Override
    public WyldCardFrame find(ExecutionContext context, List<WyldCardFrame> frames) throws PartException {
        Optional<WyldCardFrame> foundWindow = frames.stream()
                .filter(p -> p.getTitle().equalsIgnoreCase(windowName))
                .findFirst();

        if (foundWindow.isPresent()) {
            return foundWindow.get();
        } else {
            throw new PartException("No such window.");
        }
    }

    @Override
    public Object getValue() {
        return windowName;
    }
}
