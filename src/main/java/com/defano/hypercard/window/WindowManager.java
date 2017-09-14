/*
 * WindowManager
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.window;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.window.forms.*;
import com.defano.jmonet.model.Provider;

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

    private final static Provider<String> lookAndFeelClassProvider = new Provider();

    public static void start() {
        lookAndFeelClassProvider.set(UIManager.getSystemLookAndFeelClassName());

        // Create the main window, center it on the screen and display it
        WindowBuilder.make(stackWindow)
                .withTitle(HyperCard.getInstance().getStack().getStackModel().getStackName())
                .quitOnClose()
                .ownsMenubar()
                .withModel(HyperCard.getInstance().getStack())
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
        lookAndFeelClassProvider.set(lafClassName);

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

    public static Provider<String> getLookAndFeelClassProvider() {
        return lookAndFeelClassProvider;
    }

    public static boolean isMacOs() {
        return UIManager.getLookAndFeel().getName().equalsIgnoreCase("Mac OS X");
    }
}
