package com.defano.wyldcard.window;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.context.DefaultToolsManager;
import com.defano.wyldcard.window.layouts.*;
import com.google.inject.Singleton;
import io.reactivex.subjects.BehaviorSubject;

import javax.swing.FocusManager;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class DefaultWindowManager implements WindowManager {

    private final BehaviorSubject<List<WyldCardFrame>> framesProvider = BehaviorSubject.createDefault(new ArrayList<>());
    private final BehaviorSubject<List<WyldCardFrame>> windowsProvider = BehaviorSubject.createDefault(new ArrayList<>());
    private final BehaviorSubject<List<WyldCardFrame>> palettesProvider = BehaviorSubject.createDefault(new ArrayList<>());
    private final BehaviorSubject<Boolean> palettesDockedProvider = BehaviorSubject.createDefault(false);

    private MessageWindow messageWindow;
    private PaintToolsPalette paintToolsPalette;;
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
    private JFrame hiddenPrintFrame;;

    @RunOnDispatch
    public void start() {
        messageWindow = new MessageWindow();
        paintToolsPalette = new PaintToolsPalette();
        shapesPalette = new ShapesPalette();
        linesPalette = new LinesPalette();
        patternsPalette = new PatternPalette();
        brushesPalette = new BrushesPalette();
        colorPalette = new ColorPalette();
        intensityPalette = new IntensityPalette();
        messageWatcher = new MessageWatcher();
        variableWatcher = new VariableWatcher();
        expressionEvaluator = new ExpressionEvaluator();
        magnifierPalette = new MagnificationPalette();
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

        new WindowBuilder<>(patternsPalette)
                .asPalette()
                .withTitle("Patterns")
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
                .notInitiallyVisible()
                .build();

        new WindowBuilder<>(messageWatcher)
                .asPalette()
                .focusable(false)
                .withTitle("Message Watcher")
                .notInitiallyVisible()
                .resizeable(true)
                .build();

        new WindowBuilder<>(variableWatcher)
                .asPalette()
                .withTitle("Variable Watcher")
                .focusable(true)
                .notInitiallyVisible()
                .setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE)
                .resizeable(true)
                .build();

        new WindowBuilder<>(expressionEvaluator)
                .withTitle("Evaluate Expression")
                .asModal()
                .setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE)
                .notInitiallyVisible()
                .resizeable(true)
                .build();

        new WindowBuilder<>(magnifierPalette)
                .withTitle("Magnifier")
                .asPalette()
                .notInitiallyVisible()
                .setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE)
                .resizeable(false)
                .build();
    }

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

    public StackWindow getFocusedStackWindow() {
        return getWindowForStack(new ExecutionContext(), WyldCard.getInstance().getFocusedStack());
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

    public MagnificationPalette getMagnifierPalette() {
        return magnifierPalette;
    }

    public void showPatternEditor() {
        new WindowBuilder<>(new PatternEditor())
                .withModel(WyldCard.getInstance().getToolsManager().getFillPattern())
                .withTitle("Edit Pattern")
                .resizeable(false)
                .asModal()
                .build();
    }

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
                    .withActionOnClose(window -> WyldCard.getInstance().closeStack(context, ((StackWindow) window).getStack()))
                    .ownsMenubar()
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
                if (index - 1 >= 0) {
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

    public void notifyWindowVisibilityChanged() {
        framesProvider.onNext(getFrames(false));
        windowsProvider.onNext(getWindows(true));
        palettesProvider.onNext(getPalettes(true));
    }

}
