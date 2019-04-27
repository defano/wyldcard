package com.defano.wyldcard.window;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;
import com.defano.wyldcard.window.layouts.*;
import com.google.inject.Singleton;
import io.reactivex.subjects.BehaviorSubject;

import javax.swing.FocusManager;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class WyldCardWindowManager implements WindowManager {

    private final BehaviorSubject<List<WyldCardFrame>> framesProvider = BehaviorSubject.createDefault(new ArrayList<>());
    private final BehaviorSubject<List<WyldCardFrame>> windowsProvider = BehaviorSubject.createDefault(new ArrayList<>());
    private final BehaviorSubject<List<WyldCardFrame>> palettesProvider = BehaviorSubject.createDefault(new ArrayList<>());
    private final BehaviorSubject<Boolean> palettesDockedProvider = BehaviorSubject.createDefault(false);

    private MessageWindow messageWindow;
    private PaintToolsPalette paintToolsPalette;
    private ShapesPalette shapesPalette;
    private LinesPalette linesPalette;
    private PatternPalette patternsPalette;
    private BrushesPalette brushesPalette;
    private ColorPalette colorPalette;
    private IntensityPalette intensityPalette;
    private MessageWatcher messageWatcher;
    private VariableWatcher variableWatcher;
    private ExpressionEvaluator expressionEvaluator;
    private MagnificationPalette magnifierPalette;
    private FindWindow findWindow;
    private ReplaceWindow replaceWindow;
    private JFrame hiddenPrintFrame;

    @RunOnDispatch
    @Override
    public void start() {
        messageWindow = MessageWindow.getInstance();
        paintToolsPalette = PaintToolsPalette.getInstance();
        shapesPalette = ShapesPalette.getInstance();
        linesPalette = LinesPalette.getInstance();
        patternsPalette = PatternPalette.getInstance();
        brushesPalette = BrushesPalette.getInstance();
        colorPalette = ColorPalette.getInstance();
        intensityPalette = IntensityPalette.getInstance();
        messageWatcher = MessageWatcher.getInstance();
        variableWatcher = VariableWatcher.getInstance();
        expressionEvaluator = ExpressionEvaluator.getInstance();
        magnifierPalette = MagnificationPalette.getInstance();
        findWindow = FindWindow.getInstance();
        replaceWindow = ReplaceWindow.getInstance();
        hiddenPrintFrame = WindowBuilder.buildHiddenScreenshotFrame();

        themeProvider.onNext(UIManager.getLookAndFeel().getName());

        new WindowBuilder<>(messageWindow)
                .withTitle("Message")
                .asPalette()
                .focusable(true)
                .notInitiallyVisible()
                .build();

        new WindowBuilder<>(paintToolsPalette)
                .asPalette()
                .withTitle("Tools")
                .notInitiallyVisible()
                .build();

        new WindowBuilder<>(patternsPalette)
                .asPalette()
                .withTitle("Patterns")
                .notInitiallyVisible()
                .build();

        new WindowBuilder<>(shapesPalette)
                .asPalette()
                .withTitle("Shapes")
                .notInitiallyVisible()
                .build();

        new WindowBuilder<>(linesPalette)
                .asPalette()
                .withTitle("Lines")
                .notInitiallyVisible()
                .build();

        new WindowBuilder<>(brushesPalette)
                .asPalette()
                .withTitle("Brushes")
                .notInitiallyVisible()
                .build();

        new WindowBuilder<>(intensityPalette)
                .asPalette()
                .withTitle("Intensity")
                .notInitiallyVisible()
                .build();

        new WindowBuilder<>(colorPalette)
                .asPalette()
                .focusable(true)
                .withTitle("Colors")
                .withLocationCenteredOnScreen()
                .notInitiallyVisible()
                .build();

        new WindowBuilder<>(messageWatcher)
                .asPalette()
                .focusable(false)
                .withTitle("Message Watcher")
                .withLocationCenteredOnScreen()
                .notInitiallyVisible()
                .resizeable(true)
                .build();

        new WindowBuilder<>(variableWatcher)
                .asPalette()
                .withTitle("Variable Watcher")
                .focusable(true)
                .withLocationCenteredOnScreen()
                .notInitiallyVisible()
                .setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE)
                .resizeable(true)
                .build();

        new WindowBuilder<>(expressionEvaluator)
                .asPalette()
                .withTitle("Evaluate Expression")
                .setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE)
                .focusable(true)
                .notInitiallyVisible()
                .withLocationCenteredOnScreen()
                .resizeable(true)
                .build();

        new WindowBuilder<>(magnifierPalette)
                .withTitle("Magnifier")
                .asPalette()
                .notInitiallyVisible()
                .setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE)
                .resizeable(false)
                .build();

        new WindowBuilder<>(replaceWindow)
                .withTitle("Replace")
                .asPalette()
                .focusable(true)
                .notInitiallyVisible()
                .build();

        new WindowBuilder<>(findWindow)
                .asPalette()
                .withTitle("Find")
                .focusable(true)
                .notInitiallyVisible()
                .build();
    }

    @RunOnDispatch
    @Override
    public void showAllToolPalettes() {
        paintToolsPalette.setVisible(true);
        patternsPalette.setVisible(true);
        magnifierPalette.setVisible(true);
        brushesPalette.setVisible(true);
        linesPalette.setVisible(true);
        intensityPalette.setVisible(true);
        shapesPalette.setVisible(true);
    }

    @RunOnDispatch
    @Override
    public void restoreDefaultLayout() {

        StackWindow stackWindow = getFocusedStackWindow();

        paintToolsPalette
                .setLocationLeftOf(stackWindow)
                .alignTopTo(stackWindow);

        patternsPalette
                .setLocationLeftOf(stackWindow)
                .setLocationBelow(paintToolsPalette.getWindow());

        messageWindow
                .setLocationBelow(stackWindow)
                .alignLeftTo(stackWindow);

        magnifierPalette
                .setLocationRightOf(stackWindow)
                .alignTopTo(stackWindow);

        brushesPalette
                .setLocationRightOf(stackWindow)
                .setLocationBelow(magnifierPalette);

        linesPalette
                .setLocationRightOf(stackWindow)
                .setLocationBelow(brushesPalette.getWindow());

        intensityPalette
                .setLocationRightOf(stackWindow)
                .setLocationBelow(linesPalette.getWindow());

        shapesPalette
                .setLocationRightOf(stackWindow)
                .setLocationBelow(intensityPalette.getWindow());
    }

    @Override
    public StackWindow getFocusedStackWindow() {
        return getWindowForStack(new ExecutionContext(), WyldCard.getInstance().getStackManager().getFocusedStack());
    }

    @Override
    public MessageWindow getMessageWindow() {
        return messageWindow;
    }

    @Override
    public PaintToolsPalette getPaintToolsPalette() {
        return paintToolsPalette;
    }

    @Override
    public ShapesPalette getShapesPalette() {
        return shapesPalette;
    }

    @Override
    public LinesPalette getLinesPalette() {
        return linesPalette;
    }

    @Override
    public PatternPalette getPatternsPalette() {
        return patternsPalette;
    }

    @Override
    public BrushesPalette getBrushesPalette() {
        return brushesPalette;
    }

    @Override
    public ColorPalette getColorPalette() {
        return colorPalette;
    }

    @Override
    public IntensityPalette getIntensityPalette() {
        return intensityPalette;
    }

    @Override
    public MessageWatcher getMessageWatcher() {
        return messageWatcher;
    }

    @Override
    public VariableWatcher getVariableWatcher() {
        return variableWatcher;
    }

    @Override
    public ExpressionEvaluator getExpressionEvaluator() {
        return expressionEvaluator;
    }

    @Override
    public MagnificationPalette getMagnifierPalette() {
        return magnifierPalette;
    }

    @Override
    public FindWindow getFindWindow() {
        return findWindow;
    }

    @Override
    public ReplaceWindow getReplaceWindow() {
        return replaceWindow;
    }

    @Override
    public void showPatternEditor() {
        new WindowBuilder<>(new PatternEditor())
                .withModel(WyldCard.getInstance().getToolsManager().getFillPattern())
                .withTitle("Edit Pattern")
                .resizeable(false)
                .asModal()
                .build();
    }

    @Override
    public void showRecentCardsWindow() {
        new WindowBuilder<>(new RecentCardsWindow())
                .withTitle("Recent Cards")
                .asModal()
                .resizeable(true)
                .setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE)
                .build();
    }

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
    @Override
    public JFrame getScreenshotBufferWindow() {
        return hiddenPrintFrame;
    }

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
    @Override
    public StackWindow getWindowForStack(ExecutionContext context, StackPart stackPart) {
        if (stackPart == null) {
            throw new IllegalArgumentException("Can't get window for null stack part.");
        }

        StackWindow existingWindow = findWindowForStack(stackPart.getStackModel());

        if (existingWindow != null) {
            return existingWindow;
        } else {
            return (StackWindow) new WindowBuilder<>(new StackWindow())
                    .withModel(stackPart)
                    .withActionOnClose(window -> WyldCard.getInstance().getStackManager().closeStack(context, ((StackWindow) window).getDisplayedStack()))
                    .ownsMenubar()
                    .withLocationCenteredOnScreen()
                    .build();
        }
    }

    @Override
    public BehaviorSubject<List<WyldCardFrame>> getFramesProvider() {
        return framesProvider;
    }

    @Override
    public BehaviorSubject<List<WyldCardFrame>> getVisibleWindowsProvider() {
        return windowsProvider;
    }

    @Override
    public BehaviorSubject<List<WyldCardFrame>> getVisiblePalettesProvider() {
        return palettesProvider;
    }

    @Override
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

    @Override
    public WyldCardFrame prevWindow() {
        List<WyldCardFrame> windows = getFocusableFrames(true);

        for (int index = 0; index < windows.size(); index++) {
            if (windows.get(index) == FocusManager.getCurrentManager().getFocusedWindow()) {
                if (index - 1 >= 0) {
                    return windows.get(index - 1);
                } else {
                    return windows.get(windows.size() - 1);
                }
            }
        }

        return null;
    }

    @Override
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

    @Override
    public BehaviorSubject<Boolean> getPalettesDockedProvider() {
        return palettesDockedProvider;
    }

    @Override
    public void onApplicationFocusChanged(boolean appInFocus) {
        WyldCard.getInstance().getKeyboardManager().resetKeyStates();
    }

    @Override
    public void notifyWindowVisibilityChanged() {
        Invoke.onDispatch(() -> {
            framesProvider.onNext(getFrames(false));
            windowsProvider.onNext(getWindows(true));
            palettesProvider.onNext(getPalettes(true));
        });
    }

}
