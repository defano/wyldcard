package com.defano.wyldcard.window;

import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.parts.finder.WindowFinder;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.layouts.*;
import io.reactivex.subjects.BehaviorSubject;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public interface WindowManager extends WindowFinder, Themeable {

    void start();

    void restoreDefaultLayout();

    StackWindow getFocusedStackWindow();

    MessageWindow getMessageWindow();

    PaintToolsPalette getPaintToolsPalette();

    ShapesPalette getShapesPalette();

    LinesPalette getLinesPalette();

    PatternPalette getPatternsPalette();

    BrushesPalette getBrushesPalette();

    ColorPalette getColorPalette();

    IntensityPalette getIntensityPalette();

    MessageWatcher getMessageWatcher();

    VariableWatcher getVariableWatcher();

    ExpressionEvaluator getExpressionEvaluator();

    MagnificationPalette getMagnifierPalette();

    FindWindow getFindWindow();

    ReplaceWindow getReplaceWindow();

    void showPatternEditor();

    void showRecentCardsWindow();

    /**
     * Returns a JFrame intended to be used when creating card screenshots (for use in visual effects processing and
     * displaying card thumbnails).
     *
     * Swing has some seemingly odd requirements here: Components can only be printed if they're attached to a JFrame
     * and that frame has been made visible at some point. If these conditions are not met, calls to
     * {@link Component#printAll(Graphics)} produce empty or partially populated renderings. Ostensibly, this is a side
     * effect of Swing's Java-to-native component peering architecture.
     *
     * @return A JFrame intended to be used for screen printing.
     */
    JFrame getScreenshotBufferWindow();

    /**
     * Gets a window (JFrame) in which to display the given stack. If a window already exists for this stack, then the
     * existing window is returned, otherwise a new window is created and bound to the stack. If the given stack
     * is null, a new, unbound stack window will be returned.
     *z
     * @param context The execution context
     * @param stackPart The stack whose window should be retrieved
     * @return A window (new or existing) bound to the stack.
     */
    @RunOnDispatch
    StackWindow getWindowForStack(ExecutionContext context, StackPart stackPart);

    BehaviorSubject<List<WyldCardFrame>> getFramesProvider();

    BehaviorSubject<List<WyldCardFrame>> getVisibleWindowsProvider();

    BehaviorSubject<List<WyldCardFrame>> getVisiblePalettesProvider();

    WyldCardFrame nextWindow();

    WyldCardFrame prevWindow();

    void toggleDockPalettes();

    BehaviorSubject<Boolean> getPalettesDockedProvider();

    /**
     * Invoke to notify the window manager that WyldCard has gained or lost focus in the host operating system.
     * @param appInFocus True to indicate that WyldCard has gained focus; false to indicate that it has lost focus.
     */
    void onApplicationFocusChanged(boolean appInFocus);

    void notifyWindowVisibilityChanged();
}
