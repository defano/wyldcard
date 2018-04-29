package com.defano.wyldcard.window;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.parts.finder.WindowFinder;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.window.layouts.*;
import io.reactivex.subjects.BehaviorSubject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class WindowManager implements WindowFinder, Themeable {

    private final static WindowManager instance = new WindowManager();

    private final BehaviorSubject<List<WyldCardFrame>> framesProvider = BehaviorSubject.createDefault(new ArrayList<>());
    private final BehaviorSubject<List<WyldCardFrame>> windowsProvider = BehaviorSubject.createDefault(new ArrayList<>());
    private final BehaviorSubject<List<WyldCardFrame>> palettesProvider = BehaviorSubject.createDefault(new ArrayList<>());
    private final BehaviorSubject<Boolean> palettesDockedProvider = BehaviorSubject.createDefault(false);

    private final MessageWindow messageWindow = new MessageWindow();
    private final PaintToolsPalette paintToolsPalette = new PaintToolsPalette();
    private final ShapesPalette shapesPalette = new ShapesPalette();
    private final LinesPalette linesPalette = new LinesPalette();
    private final PatternPalette patternsPalette = new PatternPalette();
    private final BrushesPalette brushesPalette = new BrushesPalette();
    private final ColorPalette colorPalette = new ColorPalette();
    private final IntensityPalette intensityPalette = new IntensityPalette();
    private final MessageWatcher messageWatcher = new MessageWatcher();
    private final VariableWatcher variableWatcher = new VariableWatcher();
    private final ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator();

    private WindowManager() {
    }

    public static WindowManager getInstance() {
        return instance;
    }

    @RunOnDispatch
    public void start() {
        themeProvider.onNext(UIManager.getLookAndFeel().getName());

        StackWindow stackWindow = getFocusedStackWindow();

        WindowBuilder.make(messageWindow)
                .withTitle("Message")
                .asPalette()
                .focusable(true)
                .withLocationUnderneath(stackWindow)
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(paintToolsPalette)
                .asPalette()
                .withTitle("Tools")
                .withLocationLeftOf(stackWindow)
                .build();

        WindowBuilder.make(shapesPalette)
                .asPalette()
                .withTitle("Shapes")
                .withLocationUnderneath(paintToolsPalette.getWindow())
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(linesPalette)
                .asPalette()
                .withTitle("Lines")
                .withLocationUnderneath(paintToolsPalette.getWindow())
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(brushesPalette)
                .asPalette()
                .withTitle("Brushes")
                .withLocationUnderneath(paintToolsPalette.getWindow())
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(patternsPalette)
                .asPalette()
                .withTitle("Patterns")
                .withLocationLeftOf(paintToolsPalette.getWindow())
                .build();

        WindowBuilder.make(intensityPalette)
                .asPalette()
                .withTitle("Intensity")
                .notInitiallyVisible()
                .withLocationUnderneath(paintToolsPalette.getWindow())
                .build();

        WindowBuilder.make(colorPalette)
                .asPalette()
                .focusable(true)
                .withTitle("Colors")
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(messageWatcher)
                .asPalette()
                .focusable(false)
                .withTitle("Message Watcher")
                .notInitiallyVisible()
                .resizeable(true)
                .build();

        WindowBuilder.make(variableWatcher)
                .asPalette()
                .withTitle("Variable Watcher")
                .focusable(true)
                .notInitiallyVisible()
                .setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE)
                .resizeable(true)
                .build();

        WindowBuilder.make(expressionEvaluator)
                .withTitle("Evaluate Expression")
                .asModal()
                .setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE)
                .notInitiallyVisible()
                .resizeable(true)
                .build();
    }

    public StackWindow getFocusedStackWindow() {
        return getWindowForStack(WyldCard.getInstance().getFocusedStack());
    }

    public MessageWindow getMessageWindow() {
        return messageWindow;
    }

    public PaintToolsPalette getPaintToolsPalette() {
        return paintToolsPalette;
    }

    public ShapesPalette getShapesPalette() {
        return shapesPalette;
    }

    public LinesPalette getLinesPalette() {
        return linesPalette;
    }

    public PatternPalette getPatternsPalette() {
        return patternsPalette;
    }

    public BrushesPalette getBrushesPalette() {
        return brushesPalette;
    }

    public ColorPalette getColorPalette() {
        return colorPalette;
    }

    public IntensityPalette getIntensityPalette() {
        return intensityPalette;
    }

    public MessageWatcher getMessageWatcher() {
        return messageWatcher;
    }

    public VariableWatcher getVariableWatcher() {
        return variableWatcher;
    }

    public ExpressionEvaluator getExpressionEvaluator() {
        return expressionEvaluator;
    }

    /**
     * Gets a window (JFrame) in which to display the given stack. If a window already exists for this stack, then the
     * existing window is returned, otherwise a new window is created and bound to the stack. If the given stack
     * is null, a new, unbound stack window will be returned.
     *
     * @param stackPart The stack whose window should be retrieved
     * @return A window (new or existing) bound to the stack.
     */
    @RunOnDispatch
    public StackWindow getWindowForStack(StackPart stackPart) {
        // Special case: return an un-built window for null stack parts (required temporarily on startup)
        if (stackPart == null) {
            return new StackWindow();
        }

        StackWindow existingWindow = findWindowForStack(stackPart.getStackModel());

        if (existingWindow != null) {
            return existingWindow;
        } else {
            return (StackWindow) WindowBuilder.make(new StackWindow())
                    .withActionOnClose(window -> WyldCard.getInstance().closeStack(((StackWindow) window).getStack()))
                    .ownsMenubar()
                    .withModel(stackPart)
                    .build();
        }
    }

    public BehaviorSubject<List<WyldCardFrame>> getFramesProvider() {
        return framesProvider;
    }

    public BehaviorSubject<List<WyldCardFrame>> getVisibleWindowsProvider() {
        return windowsProvider;
    }

    public BehaviorSubject<List<WyldCardFrame>> getVisiblePalettesProvider() {
        return palettesProvider;
    }

    public WyldCardFrame nextWindow() {
        List<WyldCardFrame> windows = getFocusableFrames(true);

        for (int index = 0; index < windows.size(); index++) {
            if (windows.get(index) == FocusManager.getCurrentManager().getFocusedWindow()) {
                if (index + 1 < windows.size()) {
                    return windows.get(index + 1);
                } else {
                    return windows.get(0);
                }
            }
        }

        return null;
    }

    public WyldCardFrame prevWindow() {
        List<WyldCardFrame> windows = getFocusableFrames(true);

        for (int index = 0; index < windows.size(); index++) {
            if (windows.get(index) == FocusManager.getCurrentManager().getFocusedWindow()) {
                if (index - 1 > 0) {
                    return windows.get(index - 1);
                } else {
                    return windows.get(windows.size() - 1);
                }
            }
        }

        return null;
    }

    public void toggleDockPalettes() {
        palettesDockedProvider.onNext(!palettesDockedProvider.blockingFirst());

        if (palettesDockedProvider.blockingFirst()) {
            WindowDock.getInstance().undockWindows(getPalettes(false));
            WindowDock.getInstance().setDock(getFocusedStackWindow());
            WindowDock.getInstance().dockWindows(getPalettes(false));
        } else {
            WindowDock.getInstance().undockWindows(getPalettes(false));
        }
    }

    public BehaviorSubject<Boolean> getPalettesDockedProvider() {
        return palettesDockedProvider;
    }

    void notifyWindowVisibilityChanged() {
        framesProvider.onNext(getFrames(false));
        windowsProvider.onNext(getWindows(true));
        palettesProvider.onNext(getPalettes(true));
    }

}
