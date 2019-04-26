package com.defano.wyldcard.border;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class PartBorderFactory {

    public static Border createEmptyBorder() {
        return createBorder(BorderFactory.createEmptyBorder());
    }

    public static Border createEmptyBorder(int margin) {
        return createBorder(BorderFactory.createEmptyBorder(margin, margin, margin, margin));
    }

    public static Border createRoundRectShadowBorder(int arcSize) {
        return createBorder(new RoundRectShadowBorder(arcSize));
    }

    public static Border createRoundRectBorder(int arcSize) {
        return createBorder(new RoundRectBorder(arcSize));
    }

    public static Border createLineBorder() {
        return createBorder(new RectangleBorder(1));
    }

    public static Border createOvalBorder(Color color) {
        return createBorder(new OvalBorder(1));
    }

    public static Border createDropShadowBorder() {
        return createBorder(new DropShadowBorder());
    }

    public static Border createDoubleRoundRectBorder(int innerWidth, int innerArcSize, int separation, int outerWidth, int outerArcSize) {
        return createBorder(new DoubleRoundRectBorder(innerWidth, innerArcSize, separation, outerWidth, outerArcSize));
    }

    private static Border createBorder(Border border) {
        return new ClickToEditPartBorder(border);
    }
}
