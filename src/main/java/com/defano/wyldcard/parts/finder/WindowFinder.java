package com.defano.wyldcard.parts.finder;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.WindowSpecifier;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;
import com.defano.wyldcard.window.WyldCardFrame;
import com.defano.wyldcard.window.WyldCardWindow;
import com.defano.wyldcard.window.layouts.ScriptEditor;
import com.defano.wyldcard.window.layouts.StackWindow;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides methods for finding windows.
 */
public interface WindowFinder {

    /**
     * Finds the window identified by the given window specifier.
     *
     * @param context   The execution context
     * @param specifier The window specifier identifying the desired window.
     * @return The specified window
     */
    default WyldCardFrame findWindow(ExecutionContext context, WindowSpecifier specifier) {
        return Invoke.onDispatch(() -> specifier.find(context, getFrames(false)));
    }

    /**
     * Gets the script editor window associated with the given part.
     *
     * @param model The part whose script editor should be found.
     * @return The script editor, or null if there is no script editor window currently in existence for this part.
     */
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

    /**
     * Gets the window associated with a given stack, or null if the stack does not appear to be currently bound to a
     * window.
     *
     * @param stackModel The stack whose window should be found.
     * @return The stack's window, or null if the stack is bound to a window.
     */
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
        return Arrays.stream(JFrame.getWindows())
                .filter(w -> w instanceof WyldCardFrame)
                .map(w -> (WyldCardFrame) w)
                .filter(w -> w.getTitle() != null)
                .filter(w -> !onlyVisible || ((Window) w).isVisible())
                .sorted(Comparator.comparing(WyldCardFrame::getTitle))
                .collect(Collectors.toList());
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
}
