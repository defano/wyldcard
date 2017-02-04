package hypercard.runtime;

import hypercard.HyperCard;
import hypercard.gui.menu.HyperCardMenuBar;
import hypercard.gui.window.*;

import javax.swing.*;
import java.awt.*;

public class WindowManager {

    private final static StackWindow stackWindow = new StackWindow();
    private final static MessageWindow messageWindow = new MessageWindow();
    private final static PaintToolsPalette paintToolsPalette = new PaintToolsPalette();
    private final static ShapesPalette shapesPalette = new ShapesPalette();
    private final static LinesPalette linesPalette = new LinesPalette();
    private final static PatternPalette patternsPalette = new PatternPalette();
    private final static BrushesPalette brushesPalette = new BrushesPalette();
    private final static ColorPalette colorPalette = new ColorPalette();

    private WindowManager() {
        start();
    }

    public static void start() {

        // Create the main window, center it on the screen and display it
        JFrame stackFrame = WindowBuilder.make(stackWindow)
                .withTitle(HyperCard.getInstance().getStack().getStackName())
                .resizeable(false)
                .quitOnClose()
                .withMenuBar(HyperCardMenuBar.instance)
                .withModel(HyperCard.getInstance().getStack().getCurrentCard())
                .build();

        WindowBuilder.make(messageWindow)
                .withTitle("Message")
                .resizeable(false)
                .withLocationUnderneath(stackFrame)
                .withMenuBar(HyperCardMenuBar.instance)
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(paintToolsPalette)
                .resizeable(false)
                .asPalette()
                .withTitle("")
                .withMenuBar(HyperCardMenuBar.instance)
                .withLocationLeftOf(stackFrame)
                .build();

        WindowBuilder.make(shapesPalette)
                .resizeable(false)
                .asPalette()
                .withTitle("Shapes")
                .withMenuBar(HyperCardMenuBar.instance)
                .withLocationUnderneath(paintToolsPalette.getWindowFrame())
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(linesPalette)
                .resizeable(false)
                .asPalette()
                .withTitle("Lines")
                .withMenuBar(HyperCardMenuBar.instance)
                .withLocationUnderneath(paintToolsPalette.getWindowFrame())
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(brushesPalette)
                .resizeable(false)
                .asPalette()
                .withTitle("")
                .withMenuBar(HyperCardMenuBar.instance)
                .withLocationUnderneath(paintToolsPalette.getWindowFrame())
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(patternsPalette)
                .resizeable(false)
                .asPalette()
                .withTitle("")
                .withMenuBar(HyperCardMenuBar.instance)
                .withLocationLeftOf(paintToolsPalette.getWindowFrame())
                .build();

        WindowBuilder.make(colorPalette)
                .resizeable(false)
                .withTitle("Colors")
                .notInitiallyVisible()
                .withMenuBar(HyperCardMenuBar.instance)
                .build();
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
}
