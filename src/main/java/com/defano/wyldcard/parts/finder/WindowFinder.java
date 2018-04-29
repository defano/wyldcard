package com.defano.wyldcard.parts.finder;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.*;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.WyldCardFrame;
import com.defano.wyldcard.window.WyldCardWindow;
import com.defano.wyldcard.window.StackWindow;
import com.defano.wyldcard.window.forms.ScriptEditor;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides methods for finding windows.
 */
public interface WindowFinder {

    default WyldCardWindow findWindow(ExecutionContext context, WindowSpecifier specifier) throws PartException {
        if (specifier instanceof WindowTypeSpecifier) {
            return ((WindowTypeSpecifier) specifier).getWindowType().getWindow(context);
        } else if (specifier instanceof WindowNameSpecifier) {
            return findWindowByName(String.valueOf(specifier.getValue()));
        } else if (specifier instanceof WindowIdSpecifier) {
            return findWindowById((int) specifier.getValue());
        } else if (specifier instanceof WindowNumberSpecifier) {
            return findWindowByNumber((int) specifier.getValue());
        }

        throw new IllegalArgumentException("Bug! Unimplemented WindowSpecifier.");
    }

    @RunOnDispatch
    default WyldCardWindow findWindowById(int id) throws PartException {
        Optional<Window> foundWindow = Arrays.stream(JFrame.getWindows())
                .filter(p -> p instanceof WyldCardFrame)
                .filter(p -> System.identityHashCode(p) == id)
                .findFirst();

        if (foundWindow.isPresent()) {
            return (WyldCardWindow) foundWindow.get();
        } else {
            throw new PartException("No such window.");
        }
    }

    @RunOnDispatch
    default WyldCardWindow findWindowByName(String name) throws PartException {
        Optional<Window> foundWindow = Arrays.stream(JFrame.getWindows())
                .filter(p -> p instanceof WyldCardFrame)
                .filter(p -> ((WyldCardFrame) p).getTitle().equalsIgnoreCase(name))
                .findFirst();

        if (foundWindow.isPresent()) {
            return (WyldCardWindow) foundWindow.get();
        } else {
            throw new PartException("No such window.");
        }
    }

    @RunOnDispatch
    default WyldCardWindow findWindowByNumber(int windowNumber) throws PartException {
        List<Window> windows = getWindows();

        if (windowNumber < 1 || windowNumber >= windows.size()) {
            throw new PartException("No such window.");
        } else {
            return (WyldCardWindow) windows.get(windowNumber - 1);
        }
    }

    @RunOnDispatch
    default ScriptEditor findScriptEditorForPart(PartModel model) {
        if (model != null) {
            return (ScriptEditor) getWindows().stream()
                    .filter(p -> p instanceof ScriptEditor && ((ScriptEditor) p).getModel() == model)
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    @RunOnDispatch
    default StackWindow findWindowForStack(StackModel stackModel) {
        return (StackWindow) getWindows().stream()
                .filter(p -> p instanceof StackWindow &&
                        ((StackWindow) p).getStack() != null &&
                        ((StackWindow) p).getStack().getStackModel().equals(stackModel))
                .findFirst()
                .orElse(null);
    }

    @RunOnDispatch
    default List<Value> getWindowNames() {
        ArrayList<Value> windows = new ArrayList<>();

        for (Window thisWindow : getWindows()) {
            if (thisWindow instanceof WyldCardFrame) {
                windows.add(new Value(((WyldCardWindow) thisWindow).getTitle()));
            }
        }

        return windows;
    }

    @RunOnDispatch
    default List<Window> getWindows() {
        return Arrays.stream(JFrame.getWindows())
                .filter(p -> p instanceof WyldCardFrame)
                .filter(p -> ((WyldCardWindow) p).getTitle() != null)
                .sorted(Comparator.comparing(o -> ((WyldCardWindow) o).getTitle()))
                .collect(Collectors.toList());
    }
}
