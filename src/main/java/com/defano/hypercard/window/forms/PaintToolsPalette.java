package com.defano.hypercard.window.forms;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.awt.DoubleClickListenable;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.runtime.context.FontContext;
import com.defano.hypercard.runtime.context.ToolsContext;
import com.defano.hypercard.window.HyperCardDialog;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.ast.model.ToolType;
import com.defano.jmonet.model.PaintToolType;
import com.defano.jmonet.tools.builder.PaintTool;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.l2fprod.common.swing.JFontChooser;
import io.reactivex.functions.Consumer;

import javax.swing.*;
import java.awt.*;

public class PaintToolsPalette extends HyperCardDialog implements Consumer {
    private JPanel palettePanel;

    private JButton selection;
    private JButton lasso;
    private JButton pencil;
    private JButton paintbrush;
    private JButton eraser;
    private JButton line;
    private JButton spraypaint;
    private JButton rectangle;
    private JButton roundRectangle;
    private JButton fill;
    private JButton oval;
    private JButton text;
    private JButton curve;
    private JButton polygon;
    private JButton shape;
    private JButton finger;
    private JButton button;
    private JButton field;

    private JButton[] allTools;
    private ToolType lastTool;

    public PaintToolsPalette() {
        allTools = new JButton[]{selection, lasso, pencil, paintbrush, eraser, line, spraypaint, rectangle, roundRectangle, fill, oval, text, curve, polygon, shape, finger, button, field};

        // Single click actions
        finger.addActionListener(e -> selectTool(ToolType.BROWSE));
        button.addActionListener(e -> selectTool(ToolType.BUTTON));
        field.addActionListener(e -> selectTool(ToolType.FIELD));

        pencil.addActionListener(e -> selectTool(ToolType.PENCIL));
        paintbrush.addActionListener(e -> selectTool(ToolType.BRUSH));
        eraser.addActionListener(e -> selectTool(ToolType.ERASER));
        line.addActionListener(e -> selectTool(ToolType.LINE));
        rectangle.addActionListener(e -> selectTool(ToolType.RECTANGLE));
        roundRectangle.addActionListener(e -> selectTool(ToolType.ROUNDRECT));
        polygon.addActionListener(e -> selectTool(ToolType.POLYGON));
        selection.addActionListener(e -> selectTool(ToolType.SELECT));
        oval.addActionListener(e -> selectTool(ToolType.OVAL));
        shape.addActionListener(e -> selectTool(ToolType.SHAPE));
        text.addActionListener(e -> selectTool(ToolType.TEXT));
        fill.addActionListener(e -> selectTool(ToolType.BUCKET));
        spraypaint.addActionListener(e -> selectTool(ToolType.SPRAY));
        curve.addActionListener(e -> selectTool(ToolType.CURVE));
        lasso.addActionListener(e -> selectTool(ToolType.LASSO));

        // Double-click actions
        eraser.addMouseListener((DoubleClickListenable) e -> eraseAll());
        shape.addMouseListener((DoubleClickListenable) e -> WindowManager.getInstance().getShapesPalette().setVisible(true));
        line.addMouseListener((DoubleClickListenable) e -> WindowManager.getInstance().getLinesPalette().setVisible(true));
        paintbrush.addMouseListener((DoubleClickListenable) e -> WindowManager.getInstance().getBrushesPalette().setVisible(true));
        spraypaint.addMouseListener((DoubleClickListenable) e -> WindowManager.getInstance().getIntensityPalette().setVisible(true));
        rectangle.addMouseListener((DoubleClickListenable) e -> ToolsContext.getInstance().toggleShapesFilled());
        roundRectangle.addMouseListener((DoubleClickListenable) e -> ToolsContext.getInstance().toggleShapesFilled());
        oval.addMouseListener((DoubleClickListenable) e -> ToolsContext.getInstance().toggleShapesFilled());
        curve.addMouseListener((DoubleClickListenable) e -> ToolsContext.getInstance().toggleShapesFilled());
        polygon.addMouseListener((DoubleClickListenable) e -> ToolsContext.getInstance().toggleShapesFilled());
        selection.addMouseListener((DoubleClickListenable) e -> ToolsContext.getInstance().selectAll());
        text.addMouseListener((DoubleClickListenable) e -> FontContext.getInstance().setSelectedFont(JFontChooser.showDialog(WindowManager.getInstance().getStackWindow(), "Choose Font", FontContext.getInstance().getFocusedTextStyle().toFont())));

        ToolsContext.getInstance().getShapesFilledProvider().subscribe(filled -> {
            rectangle.setIcon(new ImageIcon(getClass().getResource(filled ? "/icons/rectangle_filled.png" : "/icons/rectangle.png")));
            roundRectangle.setIcon(new ImageIcon(getClass().getResource(filled ? "/icons/roundrect_filled.png" : "/icons/roundrect.png")));
            oval.setIcon(new ImageIcon(getClass().getResource(filled ? "/icons/oval_filled.png" : "/icons/oval.png")));
            curve.setIcon(new ImageIcon(getClass().getResource(filled ? "/icons/curve_filled.png" : "/icons/curve.png")));
            shape.setIcon(new ImageIcon(getClass().getResource(filled ? "/icons/shape_filled.png" : "/icons/shape.png")));
            polygon.setIcon(new ImageIcon(getClass().getResource(filled ? "/icons/polygon_filled.png" : "/icons/polygon.png")));
        });

        ToolsContext.getInstance().getPaintToolProvider().subscribe(this);
        ToolsContext.getInstance().getToolModeProvider().subscribe(arg -> {
            if (arg == ToolMode.BROWSE) {
                enableAllTools();
                finger.setEnabled(false);
            } else if (arg == ToolMode.BUTTON) {
                enableAllTools();
                button.setEnabled(false);
            } else if (arg == ToolMode.FIELD) {
                enableAllTools();
                field.setEnabled(false);
            }
        });
    }

    @Override
    public JPanel getWindowPanel() {
        return palettePanel;
    }

    @Override
    public void bindModel(Object data) {
        // Nothing to do
    }

    private void eraseAll() {
        HyperCard.getInstance().getActiveStackDisplayedCard().getCanvas().clearCanvas();

        if (lastTool.isHyperCardTool()) {
            selectTool(lastTool);
        }
    }

    private void selectTool(ToolType tool) {
        lastTool = ToolsContext.getInstance().getSelectedTool();
        ToolsContext.getInstance().chooseTool(tool);
    }

    private JButton getButtonForTool(PaintToolType paintToolType) {
        switch (paintToolType) {
            case ERASER:
                return eraser;
            case ARROW:
                return finger;
            case PENCIL:
                return pencil;
            case PAINTBRUSH:
                return paintbrush;
            case LINE:
                return line;
            case RECTANGLE:
                return rectangle;
            case ROUND_RECTANGLE:
                return roundRectangle;
            case OVAL:
                return oval;
            case POLYGON:
                return polygon;
            case SELECTION:
                return selection;
            case SHAPE:
                return shape;
            case TEXT:
                return text;
            case FILL:
                return fill;
            case AIRBRUSH:
                return spraypaint;
            case CURVE:
            case FREEFORM:
                return curve;
            case LASSO:
                return lasso;

            default:
                return null;
        }
    }

    private void enableAllTools() {
        for (JButton thisToolButton : allTools) {
            if (thisToolButton != null) {
                thisToolButton.setEnabled(true);
            }
        }
    }

    @Override
    public void accept(Object newValue) {

        if (newValue instanceof PaintTool) {
            PaintTool selectedTool = (PaintTool) newValue;

            // Special case; "pseudo" transform tools highlight selection tools
            if (selectedTool.getToolType() == PaintToolType.SLANT ||
                    selectedTool.getToolType() == PaintToolType.ROTATE ||
                    selectedTool.getToolType() == PaintToolType.MAGNIFIER ||
                    selectedTool.getToolType() == PaintToolType.PERSPECTIVE ||
                    selectedTool.getToolType() == PaintToolType.PROJECTION ||
                    selectedTool.getToolType() == PaintToolType.RUBBERSHEET) {
                enableAllTools();
                selection.setEnabled(false);
            } else {
                JButton selectedToolButton = getButtonForTool(selectedTool.getToolType());

                if (selectedToolButton != null) {
                    enableAllTools();
                    selectedToolButton.setEnabled(false);
                }
            }
        }
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
        palettePanel = new JPanel();
        palettePanel.setLayout(new GridLayoutManager(7, 3, new Insets(0, 0, 0, 0), 0, 0));
        selection = new JButton();
        selection.setEnabled(true);
        selection.setIcon(new ImageIcon(getClass().getResource("/icons/selection.png")));
        selection.setIconTextGap(0);
        selection.setMargin(new Insets(0, 0, 0, 0));
        selection.setOpaque(true);
        selection.setText("");
        selection.setVisible(true);
        palettePanel.add(selection, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lasso = new JButton();
        lasso.setIcon(new ImageIcon(getClass().getResource("/icons/lasso.png")));
        lasso.setIconTextGap(0);
        lasso.setMargin(new Insets(0, 0, 0, 0));
        lasso.setText("");
        palettePanel.add(lasso, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pencil = new JButton();
        pencil.setContentAreaFilled(true);
        pencil.setIcon(new ImageIcon(getClass().getResource("/icons/pencil.png")));
        pencil.setIconTextGap(0);
        pencil.setMargin(new Insets(0, 0, 0, 0));
        pencil.setOpaque(true);
        pencil.setText("");
        palettePanel.add(pencil, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        paintbrush = new JButton();
        paintbrush.setContentAreaFilled(true);
        paintbrush.setIcon(new ImageIcon(getClass().getResource("/icons/paintbrush.png")));
        paintbrush.setIconTextGap(0);
        paintbrush.setMargin(new Insets(0, 0, 0, 0));
        paintbrush.setOpaque(true);
        paintbrush.setText("");
        palettePanel.add(paintbrush, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        eraser = new JButton();
        eraser.setContentAreaFilled(true);
        eraser.setIcon(new ImageIcon(getClass().getResource("/icons/eraser.png")));
        eraser.setIconTextGap(0);
        eraser.setMargin(new Insets(0, 0, 0, 0));
        eraser.setOpaque(true);
        eraser.setText("");
        palettePanel.add(eraser, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        line = new JButton();
        line.setContentAreaFilled(true);
        line.setIcon(new ImageIcon(getClass().getResource("/icons/line.png")));
        line.setIconTextGap(0);
        line.setMargin(new Insets(0, 0, 0, 0));
        line.setOpaque(true);
        line.setText("");
        palettePanel.add(line, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spraypaint = new JButton();
        spraypaint.setContentAreaFilled(true);
        spraypaint.setIcon(new ImageIcon(getClass().getResource("/icons/spraypaint.png")));
        spraypaint.setIconTextGap(0);
        spraypaint.setMargin(new Insets(0, 0, 0, 0));
        spraypaint.setOpaque(true);
        spraypaint.setText("");
        palettePanel.add(spraypaint, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rectangle = new JButton();
        rectangle.setContentAreaFilled(true);
        rectangle.setIcon(new ImageIcon(getClass().getResource("/icons/rectangle.png")));
        rectangle.setIconTextGap(0);
        rectangle.setMargin(new Insets(0, 0, 0, 0));
        rectangle.setOpaque(true);
        rectangle.setText("");
        palettePanel.add(rectangle, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        roundRectangle = new JButton();
        roundRectangle.setContentAreaFilled(true);
        roundRectangle.setIcon(new ImageIcon(getClass().getResource("/icons/roundrect.png")));
        roundRectangle.setIconTextGap(0);
        roundRectangle.setMargin(new Insets(0, 0, 0, 0));
        roundRectangle.setOpaque(true);
        roundRectangle.setText("");
        palettePanel.add(roundRectangle, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fill = new JButton();
        fill.setContentAreaFilled(true);
        fill.setIcon(new ImageIcon(getClass().getResource("/icons/fill.png")));
        fill.setIconTextGap(0);
        fill.setMargin(new Insets(0, 0, 0, 0));
        fill.setOpaque(true);
        fill.setText("");
        palettePanel.add(fill, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        oval = new JButton();
        oval.setContentAreaFilled(true);
        oval.setIcon(new ImageIcon(getClass().getResource("/icons/oval.png")));
        oval.setIconTextGap(0);
        oval.setMargin(new Insets(0, 0, 0, 0));
        oval.setOpaque(true);
        oval.setText("");
        palettePanel.add(oval, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        curve = new JButton();
        curve.setContentAreaFilled(true);
        curve.setIcon(new ImageIcon(getClass().getResource("/icons/curve.png")));
        curve.setIconTextGap(0);
        curve.setMargin(new Insets(0, 0, 0, 0));
        curve.setOpaque(true);
        curve.setText("");
        palettePanel.add(curve, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        text = new JButton();
        text.setContentAreaFilled(true);
        text.setIcon(new ImageIcon(getClass().getResource("/icons/text.png")));
        text.setIconTextGap(0);
        text.setMargin(new Insets(0, 0, 0, 0));
        text.setOpaque(true);
        text.setText("");
        palettePanel.add(text, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        shape = new JButton();
        shape.setContentAreaFilled(true);
        shape.setIcon(new ImageIcon(getClass().getResource("/icons/shape.png")));
        shape.setIconTextGap(0);
        shape.setMargin(new Insets(0, 0, 0, 0));
        shape.setOpaque(true);
        shape.setText("");
        palettePanel.add(shape, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        polygon = new JButton();
        polygon.setContentAreaFilled(true);
        polygon.setIcon(new ImageIcon(getClass().getResource("/icons/polygon.png")));
        polygon.setIconTextGap(0);
        polygon.setMargin(new Insets(0, 0, 0, 0));
        polygon.setOpaque(true);
        polygon.setText("");
        palettePanel.add(polygon, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        finger = new JButton();
        finger.setIcon(new ImageIcon(getClass().getResource("/icons/finger.png")));
        finger.setIconTextGap(0);
        finger.setMargin(new Insets(0, 0, 0, 0));
        finger.setText("");
        palettePanel.add(finger, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        button = new JButton();
        button.setIcon(new ImageIcon(getClass().getResource("/icons/button.png")));
        button.setIconTextGap(0);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setText("");
        palettePanel.add(button, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        field = new JButton();
        field.setIcon(new ImageIcon(getClass().getResource("/icons/field.png")));
        field.setIconTextGap(0);
        field.setMargin(new Insets(0, 0, 0, 0));
        field.setText("");
        palettePanel.add(field, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        palettePanel.add(separator1, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return palettePanel;
    }
}
