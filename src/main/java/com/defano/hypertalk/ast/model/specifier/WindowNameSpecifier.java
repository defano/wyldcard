package com.defano.hypertalk.ast.model.specifier;

import com.defano.hypertalk.exception.HtNoSuchPartException;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.window.WyldCardFrame;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WindowNameSpecifier extends WindowSpecifier {

    private final String windowName;

    public WindowNameSpecifier(String windowName) {
        this.windowName = windowName;
    }

    @Override
    public WyldCardFrame find(ExecutionContext context, List<WyldCardFrame> frames) throws HtNoSuchPartException {
        Optional<WyldCardFrame> foundWindow = frames.stream()
                .filter(p -> p.getTitle().equalsIgnoreCase(windowName))
                .findFirst();

        if (foundWindow.isPresent()) {
            return foundWindow.get();
        } else {
            throw new HtNoSuchPartException("No such window.");
        }
    }

    @Override
    public Object getValue() {
        return windowName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WindowNameSpecifier that = (WindowNameSpecifier) o;
        return windowName.equals(that.windowName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(windowName);
    }
}
