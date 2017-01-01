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

    private WindowManager() {
        start();
    }

    public static void start() {

        // Create the main window, center it on the screen and display it
        JFrame stackFrame = WindowBuilder.make(stackWindow)
                .withTitle(HyperCard.getRuntimeEnv().getStack().getStackName())
                .resizeable(true)
                .quitOnClose()
                .withMenuBar(HyperCardMenuBar.instance)
                .withModel(HyperCard.getRuntimeEnv().getStack().getCurrentCard())
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
                .notFocusable()
                .floating()
                .withTitle("")
                .withMenuBar(HyperCardMenuBar.instance)
                .withLocationLeftOf(stackFrame)
                .build();

        WindowBuilder.make(shapesPalette)
                .resizeable(false)
                .withTitle("Shapes")
                .floating()
                .notFocusable()
                .withMenuBar(HyperCardMenuBar.instance)
                .withLocationUnderneath(paintToolsPalette.getWindowFrame())
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(linesPalette)
                .resizeable(false)
                .withTitle("Lines")
                .floating()
                .notFocusable()
                .withMenuBar(HyperCardMenuBar.instance)
                .withLocationUnderneath(paintToolsPalette.getWindowFrame())
                .notInitiallyVisible()
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
}
