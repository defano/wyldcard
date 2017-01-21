package hypercard.runtime;

import hypercard.HyperCard;
import hypercard.gui.menu.HyperCardMenuBar;
import hypercard.gui.window.*;

import javax.swing.*;

public class WindowManager {

    private final static StackWindow stackWindow = new StackWindow();
    private final static MessageWindow messageWindow = new MessageWindow();
    private final static PaintToolsPalette paintToolsPalette = new PaintToolsPalette();
    private final static ShapesPalette shapesPalette = new ShapesPalette();
    private final static LinesPalette linesPalette = new LinesPalette();
    private final static PatternPalette patternsPalette = new PatternPalette();
    private final static BrushesPalette brushesPalette = new BrushesPalette();

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
                .asPalette(stackWindow)
                .resizeable(false)
                .withTitle("")
                .withMenuBar(HyperCardMenuBar.instance)
                .withLocationLeftOf(stackFrame)
                .build();

        WindowBuilder.make(shapesPalette)
                .asPalette(stackWindow)
                .resizeable(false)
                .withTitle("Shapes")
                .withMenuBar(HyperCardMenuBar.instance)
                .withLocationUnderneath(paintToolsPalette.getWindowFrame())
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(linesPalette)
                .asPalette(stackWindow)
                .resizeable(false)
                .withTitle("Lines")
                .withMenuBar(HyperCardMenuBar.instance)
                .withLocationUnderneath(paintToolsPalette.getWindowFrame())
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(brushesPalette)
                .asPalette(stackWindow)
                .resizeable(false)
                .withTitle("")
                .withMenuBar(HyperCardMenuBar.instance)
                .withLocationUnderneath(paintToolsPalette.getWindowFrame())
                .notInitiallyVisible()
                .build();


        WindowBuilder.make(patternsPalette)
                .asPalette(stackWindow)
                .resizeable(false)
                .withTitle("")
                .withMenuBar(HyperCardMenuBar.instance)
                .withLocationLeftOf(paintToolsPalette.getWindowFrame())
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
}
