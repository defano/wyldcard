/*
 * WindowManager
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.runtime;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.gui.menu.HyperCardMenuBar;
import com.defano.hypercard.gui.window.*;

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
    private final static FontSizePicker fontSizePicker = new FontSizePicker();

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
                .dockTo(stackWindow)
                .withMenuBar(HyperCardMenuBar.instance)
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(paintToolsPalette)
                .resizeable(false)
                .asPalette()
                .withTitle("")
                .dockTo(stackWindow)
                .withMenuBar(HyperCardMenuBar.instance)
                .withLocationLeftOf(stackFrame)
                .build();

        WindowBuilder.make(shapesPalette)
                .resizeable(false)
                .asPalette()
                .withTitle("Shapes")
                .dockTo(stackWindow)
                .withMenuBar(HyperCardMenuBar.instance)
                .withLocationUnderneath(paintToolsPalette.getWindowFrame())
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(linesPalette)
                .resizeable(false)
                .asPalette()
                .withTitle("Lines")
                .dockTo(stackWindow)
                .withMenuBar(HyperCardMenuBar.instance)
                .withLocationUnderneath(paintToolsPalette.getWindowFrame())
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(brushesPalette)
                .resizeable(false)
                .asPalette()
                .withTitle("")
                .dockTo(stackWindow)
                .withMenuBar(HyperCardMenuBar.instance)
                .withLocationUnderneath(paintToolsPalette.getWindowFrame())
                .notInitiallyVisible()
                .build();

        WindowBuilder.make(patternsPalette)
                .resizeable(false)
                .asPalette()
                .withTitle("")
                .dockTo(stackWindow)
                .withMenuBar(HyperCardMenuBar.instance)
                .withLocationLeftOf(paintToolsPalette.getWindowFrame())
                .build();

        WindowBuilder.make(colorPalette)
                .resizeable(false)
                .withTitle("Colors")
                .notInitiallyVisible()
                .dockTo(stackWindow)
                .withMenuBar(HyperCardMenuBar.instance)
                .build();

        WindowBuilder.make(fontSizePicker)
                .resizeable(false)
                .withTitle("Font Size")
                .notInitiallyVisible()
                .dockTo(stackWindow)
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

    public static FontSizePicker getFontSizePicker() {
        return fontSizePicker;
    }
}
