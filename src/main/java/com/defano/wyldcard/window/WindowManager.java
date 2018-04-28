package com.defano.wyldcard.window;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.parts.finder.WindowFinder;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.window.forms.*;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import javax.swing.*;
import java.awt.*;

public class WindowManager implements WindowFinder {

    private final static WindowManager instance = new WindowManager();

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
    private final Subject<String> lookAndFeelClassProvider = BehaviorSubject.create();

    private WindowManager() {
    }

    public static WindowManager getInstance() {
        return instance;
    }

    @RunOnDispatch
    public void start() {
        lookAndFeelClassProvider.onNext(UIManager.getSystemLookAndFeelClassName());

        StackWindow stackWindow = getFocusedStackWindow();

        WindowBuilder.make(messageWindow)
                .withTitle("Message")
                .asPalette()
                .focusable(true)
                .withLocationUnderneath(stackWindow)
                .dockTo(stackWindow)
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(paintToolsPalette)
                .asPalette()
                .withTitle("Tools")
                .dockTo(stackWindow)
                .withLocationLeftOf(stackWindow)
                .build();

        WindowBuilder.make(shapesPalette)
                .asPalette()
                .withTitle("Shapes")
                .dockTo(stackWindow)
                .withLocationUnderneath(paintToolsPalette.getWindow())
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(linesPalette)
                .asPalette()
                .withTitle("Lines")
                .dockTo(stackWindow)
                .withLocationUnderneath(paintToolsPalette.getWindow())
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(brushesPalette)
                .asPalette()
                .withTitle("Brushes")
                .dockTo(stackWindow)
                .withLocationUnderneath(paintToolsPalette.getWindow())
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(patternsPalette)
                .asPalette()
                .withTitle("Patterns")
                .dockTo(stackWindow)
                .withLocationLeftOf(paintToolsPalette.getWindow())
                .build();

        WindowBuilder.make(intensityPalette)
                .asPalette()
                .withTitle("Intensity")
                .notInitiallyVisible()
                .dockTo(stackWindow)
                .withLocationUnderneath(paintToolsPalette.getWindow())
                .build();

        WindowBuilder.make(colorPalette)
                .asPalette()
                .focusable(true)
                .withTitle("Colors")
                .notInitiallyVisible()
                .dockTo(stackWindow)
                .build();

        WindowBuilder.make(messageWatcher)
                .asPalette()
                .focusable(false)
                .withTitle("Message Watcher")
                .notInitiallyVisible()
                .dockTo(stackWindow)
                .resizeable(true)
                .build();

        WindowBuilder.make(variableWatcher)
                .asPalette()
                .withTitle("Variable Watcher")
                .focusable(true)
                .notInitiallyVisible()
                .setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE)
                .dockTo(stackWindow)
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

    public void setLookAndFeel(String lafClassName) {
        lookAndFeelClassProvider.onNext(lafClassName);

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(lafClassName);

                for (Window thisWindow : JFrame.getWindows()) {
                    SwingUtilities.updateComponentTreeUI(thisWindow);

                    if (thisWindow instanceof HyperCardWindow) {
                        HyperCardWindow thisWyldWindow = (HyperCardWindow) thisWindow;
                        thisWyldWindow.getWindow().pack();
                        thisWyldWindow.applyMenuBar();
                    }
                }

                getFocusedStackWindow().applyMenuBar();

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        });

    }

    public Observable<String> getLookAndFeelClassProvider() {
        return lookAndFeelClassProvider;
    }

    @RunOnDispatch
    public boolean isMacOs() {
        return UIManager.getLookAndFeel().getName().equalsIgnoreCase("Mac OS X");
    }
}
