/*
 * ShapesPalette
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.window.forms;

import com.defano.hypercard.window.HyperCardDialog;
import com.defano.hypercard.paint.ToolsContext;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class ShapesPalette extends HyperCardDialog implements Observer {

    private JPanel shapesPanel;

    private JButton triangle;
    private JButton rectangle;
    private JButton pentagon;
    private JButton hexagon;
    private JButton octogon;

    private JButton[] allShapes;

    public ShapesPalette() {
        allShapes = new JButton[]{triangle, rectangle, pentagon, hexagon, octogon};

        triangle.addActionListener(e -> selectShape(3));
        rectangle.addActionListener(e -> selectShape(4));
        pentagon.addActionListener(e -> selectShape(5));
        hexagon.addActionListener(e -> selectShape(6));
        octogon.addActionListener(e -> selectShape(8));

        ToolsContext.getInstance().getShapeSidesProvider().addObserverAndUpdate(this);
    }

    @Override
    public JPanel getWindowPanel() {
        return shapesPanel;
    }

    @Override
    public void bindModel(Object data) {
        // Nothing to do
    }

    private JButton getButtonForShape(int sides) {
        switch (sides) {
            case 3:
                return triangle;
            case 4:
                return rectangle;
            case 5:
                return pentagon;
            case 6:
                return hexagon;
            case 8:
                return octogon;
        }

        throw new IllegalStateException("Bug! Unimplemented shape.");
    }

    public void selectShape(int sides) {
        ToolsContext.getInstance().setShapeSides(sides);
    }

    @Override
    public void update(Observable o, Object newValue) {
        if (newValue instanceof Integer) {
            for (JButton thisShape : allShapes) {
                thisShape.setEnabled(true);
            }

            getButtonForShape((int) newValue).setEnabled(false);
        }
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        shapesPanel = new JPanel();
        shapesPanel.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), 0, 0));
        triangle = new JButton();
        triangle.setAlignmentY(0.0f);
        triangle.setIcon(new ImageIcon(getClass().getResource("/shapes/triangle.png")));
        triangle.setMargin(new Insets(0, 0, 0, 0));
        triangle.setText("");
        shapesPanel.add(triangle, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rectangle = new JButton();
        rectangle.setAlignmentY(0.0f);
        rectangle.setIcon(new ImageIcon(getClass().getResource("/shapes/square.png")));
        rectangle.setMargin(new Insets(0, 0, 0, 0));
        rectangle.setText("");
        shapesPanel.add(rectangle, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pentagon = new JButton();
        pentagon.setAlignmentY(0.0f);
        pentagon.setIcon(new ImageIcon(getClass().getResource("/shapes/pentagon.png")));
        pentagon.setMargin(new Insets(0, 0, 0, 0));
        pentagon.setText("");
        shapesPanel.add(pentagon, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        hexagon = new JButton();
        hexagon.setAlignmentY(0.0f);
        hexagon.setIcon(new ImageIcon(getClass().getResource("/shapes/hexagon.png")));
        hexagon.setMargin(new Insets(0, 0, 0, 0));
        hexagon.setText("");
        shapesPanel.add(hexagon, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        octogon = new JButton();
        octogon.setAlignmentY(0.0f);
        octogon.setIcon(new ImageIcon(getClass().getResource("/shapes/octogon.png")));
        octogon.setMargin(new Insets(0, 0, 0, 0));
        octogon.setText("");
        shapesPanel.add(octogon, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return shapesPanel;
    }
}