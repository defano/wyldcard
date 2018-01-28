package com.defano.hypercard.window;

import com.defano.hypercard.parts.stack.StackPart;
import com.defano.hypercard.window.forms.*;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import javax.swing.*;

public class WindowManager {

    private final static StackWindow stackWindow = new StackWindow();
    private final static MessageWindow messageWindow = new MessageWindow();
    private final static PaintToolsPalette paintToolsPalette = new PaintToolsPalette();
    private final static ShapesPalette shapesPalette = new ShapesPalette();
    private final static LinesPalette linesPalette = new LinesPalette();
    private final static PatternPalette patternsPalette = new PatternPalette();
    private final static BrushesPalette brushesPalette = new BrushesPalette();
    private final static ColorPalette colorPalette = new ColorPalette();

    private final static Subject<String> lookAndFeelClassProvider = BehaviorSubject.create();

    public static void start() {
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

        WindowBuilder.make(colorPalette)
                .withTitle("Colors")
                .notInitiallyVisible()
                .dockTo(stackWindow)
                .build();

        stackFrame.requestFocus();
    }

    public static StackWindow getStackWindow() {
        return stackWindow;
    }

    public static MessageWindow getMessageWindow() {
        return messageWindow;
    }

    public static PaintToolsPalette getPaintToolsPalette() {
        return paintToolsPalette;
    }

    public static ShapesPalette getShapesPalette() {
        return shapesPalette;
    }

    public static LinesPalette getLinesPalette() {
        return linesPalette;
    }

    public static PatternPalette getPatternsPalette() {
        return patternsPalette;
    }

    public static BrushesPalette getBrushesPalette() {
        return brushesPalette;
    }

    public static ColorPalette getColorPalette() {
        return colorPalette;
    }

    public static HyperCardWindow[] allWindows() {
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

    public static void setLookAndFeel(String lafClassName) {
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

    public static Observable<String> getLookAndFeelClassProvider() {
        return lookAndFeelClassProvider;
    }

    public static boolean isMacOs() {
        return UIManager.getLookAndFeel().getName().equalsIgnoreCase("Mac OS X");
    }
}
