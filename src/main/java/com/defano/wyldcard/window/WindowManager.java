package com.defano.wyldcard.window;

import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.window.forms.*;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import javax.swing.*;

public class WindowManager {

    private final static WindowManager instance = new WindowManager();

    private final StackWindow stackWindow = new StackWindow();
    private final MessageWindow messageWindow = new MessageWindow();
    private final PaintToolsPalette paintToolsPalette = new PaintToolsPalette();
    private final ShapesPalette shapesPalette = new ShapesPalette();
    private final LinesPalette linesPalette = new LinesPalette();
    private final PatternPalette patternsPalette = new PatternPalette();
    private final BrushesPalette brushesPalette = new BrushesPalette();
    private final ColorPalette colorPalette = new ColorPalette();
    private final IntensityPalette intensityPalette = new IntensityPalette();

    private final Subject<String> lookAndFeelClassProvider = BehaviorSubject.create();

    public static WindowManager getInstance() {
        return instance;
    }

    private WindowManager() {}

    @RunOnDispatch
    public void start() {
        lookAndFeelClassProvider.onNext(UIManager.getSystemLookAndFeelClassName());

        // Create the main window, center it on the screen and display it
        WindowBuilder.make(stackWindow)
                .quitOnClose()
                .ownsMenubar()
                .withModel(StackPart.newStack())
                .build();

        JFrame stackFrame = stackWindow.getWindow();

        WindowBuilder.make(messageWindow)
                .withTitle("Message")
                .withLocationUnderneath(stackFrame)
                .dockTo(stackWindow)
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(paintToolsPalette)
                .asPalette()
                .dockTo(stackWindow)
                .withLocationLeftOf(stackFrame)
                .build();

        WindowBuilder.make(shapesPalette)
                .asPalette()
                .dockTo(stackWindow)
                .withLocationUnderneath(paintToolsPalette.getWindow())
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(linesPalette)
                .asPalette()
                .dockTo(stackWindow)
                .withLocationUnderneath(paintToolsPalette.getWindow())
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(brushesPalette)
                .asPalette()
                .dockTo(stackWindow)
                .withLocationUnderneath(paintToolsPalette.getWindow())
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(patternsPalette)
                .asPalette()
                .dockTo(stackWindow)
                .withLocationLeftOf(paintToolsPalette.getWindow())
                .build();

        WindowBuilder.make(intensityPalette)
                .notInitiallyVisible()
                .dockTo(stackWindow)
                .withLocationUnderneath(paintToolsPalette.getWindow())
                .build();

        WindowBuilder.make(colorPalette)
                .withTitle("Colors")
                .notInitiallyVisible()
                .dockTo(stackWindow)
                .build();

        stackFrame.requestFocus();
    }

    public StackWindow getStackWindow() {
        return stackWindow;
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

    public HyperCardWindow[] allWindows() {
        return new HyperCardWindow[] {
                getStackWindow(),
                getMessageWindow(),
                getPaintToolsPalette(),
                getShapesPalette(),
                getLinesPalette(),
                getPatternsPalette(),
                getBrushesPalette(),
                getColorPalette()
        };
    }

    @RunOnDispatch
    public void setLookAndFeel(String lafClassName) {
        lookAndFeelClassProvider.onNext(lafClassName);

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(lafClassName);

                for (HyperCardWindow thisWindow : allWindows()) {
                    thisWindow.applyMenuBar();

                    SwingUtilities.updateComponentTreeUI(thisWindow.getWindow());
                    thisWindow.getWindow().pack();
                    thisWindow.getWindow().invalidate();
                }

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
