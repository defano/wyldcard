package com.defano.wyldcard.parts.finder;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.*;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.StackFrame;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.WyldCardDialog;
import com.defano.wyldcard.window.WyldCardWindow;
import com.defano.wyldcard.window.WyldCardFrame;
import com.defano.wyldcard.window.layouts.StackWindow;
import com.defano.wyldcard.window.layouts.ScriptEditor;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Provides methods for finding windows.
 */
public interface WindowFinder {

    default WyldCardFrame findWindow(ExecutionContext context, WindowSpecifier specifier) throws PartException {
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
    default WyldCardFrame findWindowById(int id) throws PartException {
        Optional<WyldCardFrame> foundWindow = getFrames(false).stream()
                .filter(p -> p instanceof WyldCardWindow)
                .filter(p -> System.identityHashCode(p) == id)
                .findFirst();

        if (foundWindow.isPresent()) {
            return foundWindow.get();
        } else {
            throw new PartException("No such window.");
        }
    }

    @RunOnDispatch
    default WyldCardFrame findWindowByName(String name) throws PartException {
        Optional<WyldCardFrame> foundWindow = getFrames(false).stream()
                .filter(p -> p.getTitle().equalsIgnoreCase(name))
                .findFirst();

        if (foundWindow.isPresent()) {
            return foundWindow.get();
        } else {
            throw new PartException("No such window.");
        }
    }

    @RunOnDispatch
    default WyldCardFrame findWindowByNumber(int windowNumber) throws PartException {
        List<WyldCardFrame> windows = getFrames(false);

        if (windowNumber < 1 || windowNumber >= windows.size()) {
            throw new PartException("No such window.");
        } else {
            return windows.get(windowNumber - 1);
        }
    }

    @RunOnDispatch
    default ScriptEditor findScriptEditorForPart(PartModel model) {
        if (model != null) {
            return (ScriptEditor) getFrames(false).stream()
                    .filter(p -> p instanceof ScriptEditor && ((ScriptEditor) p).getModel() == model)
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    @RunOnDispatch
    default StackWindow findWindowForStack(StackModel stackModel) {
        return (StackWindow) getFrames(false).stream()
                .filter(p -> p instanceof StackWindow &&
                        ((StackWindow) p).getStack() != null &&
                        ((StackWindow) p).getStack().getStackModel().equals(stackModel))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets a list of names of all WyldCard-managed windows (windows, dialogs and palettes) irrespective of whether the
     * window is visible or not. Does not return the name of any system-produced window not generated using
     * {@link com.defano.wyldcard.window.WindowBuilder}.
     *
     * @return A list of names of every WyldCard-managed window.
     */
    @RunOnDispatch
    default List<Value> getWindowNames() {
        return getFrames(false).stream()
                .map(wyldCardFrame -> new Value(wyldCardFrame.getTitle()))
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of WyldCard-managed windows of any type. Does not include system dialogs or other windows not
     * produced using {@link com.defano.wyldcard.window.WindowBuilder}.
     *
     * @param onlyVisible When true, only visible windows are returned.
     * @return A list of WyldCard-managed windows.
     */
    @RunOnDispatch
    default List<WyldCardFrame> getFrames(boolean onlyVisible) {
        ArrayList<WyldCardFrame> windows = new ArrayList<>();

        for (Window thisWindow : JFrame.getWindows()) {
            if (thisWindow instanceof WyldCardFrame &&
                    ((WyldCardFrame) thisWindow).getTitle() != null &&
                    (!onlyVisible || thisWindow.isVisible())) {
                windows.add((WyldCardFrame) thisWindow);
            }
        }

        windows.sort(Comparator.comparing(WyldCardFrame::getTitle));
        return windows;
    }

    /**
     * Gets a list of all the WyldCard palette windows.
     *
     * @param onlyVisible When true only visible palette windows are returned.
     * @return A list of palette windows.
     */
    @RunOnDispatch
    default List<WyldCardFrame> getPalettes(boolean onlyVisible) {
        return getFrames(onlyVisible).stream()
                .filter(WyldCardFrame::isPalette)
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of all focusable windows (including dialogs and focusable palettes).
     *
     * @param onlyVisible When true, only visible windows are returned.
     * @return A list of focusable windows (of any kind).
     */
    @RunOnDispatch
    default List<WyldCardFrame> getFocusableFrames(boolean onlyVisible) {
        return getFrames(onlyVisible).stream()
                .filter(wyldCardFrame -> wyldCardFrame.getWindow().isFocusableWindow())
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of all the "standard" WyldCard windows (that is, windows which are neither dialogs nor palettes).
     *
     * @param onlyVisible When true only visible windows are returned.
     * @return A list of standard WyldCard windows.
     */
    @RunOnDispatch
    default List<WyldCardFrame> getWindows(boolean onlyVisible) {
        return getFrames(onlyVisible).stream()
                .filter(wyldCardFrame -> wyldCardFrame instanceof WyldCardWindow)
                .filter(wyldCardFrame -> !wyldCardFrame.isPalette())
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of all WyldCard dialog boxes.
     *
     * @param onlyVisible When true, only visible dialogs are returned.
     * @return A list of WyldCard dialog windows.
     */
    @RunOnDispatch
    default List<WyldCardFrame> getDialogs(boolean onlyVisible) {
        return getFrames(onlyVisible).stream()
                .filter(wyldCardFrame -> wyldCardFrame instanceof WyldCardDialog)
                .collect(Collectors.toList());
    }

}
