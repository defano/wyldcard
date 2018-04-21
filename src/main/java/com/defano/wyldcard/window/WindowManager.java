package com.defano.wyldcard.window;

import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.parts.finder.WindowFinder;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.forms.*;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import javax.swing.*;
import java.awt.*;

public class WindowManager implements WindowFinder {

    private final static WindowManager instance = new WindowManager();

    private final BehaviorSubject<StackPart> focusedStack = BehaviorSubject.create();

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

    public static WindowManager getInstance() {
        return instance;
    }

    private WindowManager() {}

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

    public StackWindow getStackWindow(StackPart stackPart) {
        return getWindowForStack(stackPart);
    }

    public StackWindow getStackWindow(ExecutionContext context) {
        return getStackWindow(context.getCurrentStack());
    }

    public StackWindow getFocusedStackWindow() {
        return getWindowForStack(getFocusedStack());
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

    public StackWindow getWindowForStack(StackPart stackPart) {
        StackWindow existingWindow = findWindowForStack(stackPart);
        if (existingWindow != null) {
            return existingWindow;
        }

        if (stackPart != null) {
            return (StackWindow) WindowBuilder.make(new StackWindow())
                    .quitOnClose()
                    .ownsMenubar()
                    .withModel(stackPart)
                    .build();
        }

        return new StackWindow();
    }

    /**
     * Gets the stack that currently has focus.
     * @return The active stack
     */
    public StackPart getFocusedStack() {
        if (focusedStack.hasValue()) {
            return focusedStack.blockingFirst();
        } else {
            return null;
        }
    }

    public boolean hasFocusedStack() {
        return focusedStack.hasValue();
    }

    public void setFocusedStack(StackPart focusedStack) {
        this.focusedStack.onNext(focusedStack);
    }

    public Observable<StackPart> getFocusedStackProvider() {
        return focusedStack;
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
