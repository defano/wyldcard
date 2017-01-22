package hypercard.gui.window;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import hypercard.HyperCard;
import hypercard.context.ToolMode;
import hypercard.context.ToolsContext;
import hypercard.gui.HyperCardWindow;
import hypercard.gui.util.DoubleClickListener;
import com.defano.jmonet.model.PaintToolType;
import com.defano.jmonet.tools.base.PaintTool;
import hypercard.runtime.WindowManager;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class PaintToolsPalette extends HyperCardWindow implements Observer {
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

    public PaintToolsPalette() {
        allTools = new JButton[]{selection, lasso, pencil, paintbrush, eraser, line, spraypaint, rectangle, roundRectangle, fill, oval, text, curve, polygon, shape, finger, button, field};

        // Single click actions
        finger.addActionListener(e -> ToolsContext.getInstance().setToolMode(ToolMode.BROWSE));
        button.addActionListener(e -> ToolsContext.getInstance().setToolMode(ToolMode.BUTTON));
        field.addActionListener(e -> ToolsContext.getInstance().setToolMode(ToolMode.FIELD));

        pencil.addActionListener(e -> toolSelected(PaintToolType.PENCIL));
        paintbrush.addActionListener(e -> toolSelected(PaintToolType.PAINTBRUSH));
        eraser.addActionListener(e -> toolSelected(PaintToolType.ERASER));
        line.addActionListener(e -> toolSelected(PaintToolType.LINE));
        rectangle.addActionListener(e -> toolSelected(PaintToolType.RECTANGLE));
        roundRectangle.addActionListener(e -> toolSelected(PaintToolType.ROUND_RECTANGLE));
        polygon.addActionListener(e -> toolSelected(PaintToolType.POLYGON));
        selection.addActionListener(e -> toolSelected(PaintToolType.SELECTION));
        oval.addActionListener(e -> toolSelected(PaintToolType.OVAL));
        shape.addActionListener(e -> toolSelected(PaintToolType.SHAPE));
        text.addActionListener(e -> toolSelected(PaintToolType.TEXT));
        fill.addActionListener(e -> toolSelected(PaintToolType.FILL));
        spraypaint.addActionListener(e -> toolSelected(PaintToolType.AIRBRUSH));
        curve.addActionListener(e -> toolSelected(PaintToolType.CURVE));
        lasso.addActionListener(e -> toolSelected(PaintToolType.LASSO));

        // Double-click actions
        eraser.addMouseListener((DoubleClickListener) e -> HyperCard.getInstance().getCard().getCanvas().clearCanvas());
        shape.addMouseListener((DoubleClickListener) e -> WindowManager.getShapesPalette().setShown(true));
        line.addMouseListener((DoubleClickListener) e -> WindowManager.getLinesPalette().setShown(true));
        paintbrush.addMouseListener((DoubleClickListener) e -> WindowManager.getBrushesPalette().setShown(true));
        spraypaint.addMouseListener((DoubleClickListener) e -> WindowManager.getBrushesPalette().setShown(true));

        ToolsContext.getInstance().getPaintToolProvider().addObserver(this);
        ToolsContext.getInstance().getToolModeProvider().addObserver((o, arg) -> {
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

    private void toolSelected(PaintToolType toolType) {
        ToolsContext.getInstance().selectPaintTool(toolType);
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
    public void update(Observable o, Object newValue) {

        if (newValue instanceof PaintTool) {
            PaintTool selectedTool = (PaintTool) newValue;

            // Special case; "pseudo-tools" re-enable all HyperCard paint tools
            if (selectedTool.getToolType() == PaintToolType.SLANT ||
                    selectedTool.getToolType() == PaintToolType.ROTATE ||
                    selectedTool.getToolType() == PaintToolType.MAGNIFIER)
            {
                enableAllTools();
            }

            else {
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
        selection.setSelected(false);
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
        pencil.setContentAreaFilled(false);
        pencil.setIcon(new ImageIcon(getClass().getResource("/icons/pencil.png")));
        pencil.setIconTextGap(0);
        pencil.setMargin(new Insets(0, 0, 0, 0));
        pencil.setText("");
        palettePanel.add(pencil, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        paintbrush = new JButton();
        paintbrush.setContentAreaFilled(false);
        paintbrush.setIcon(new ImageIcon(getClass().getResource("/icons/paintbrush.png")));
        paintbrush.setIconTextGap(0);
        paintbrush.setMargin(new Insets(0, 0, 0, 0));
        paintbrush.setText("");
        palettePanel.add(paintbrush, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        eraser = new JButton();
        eraser.setContentAreaFilled(false);
        eraser.setIcon(new ImageIcon(getClass().getResource("/icons/eraser.png")));
        eraser.setIconTextGap(0);
        eraser.setMargin(new Insets(0, 0, 0, 0));
        eraser.setText("");
        palettePanel.add(eraser, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        line = new JButton();
        line.setContentAreaFilled(false);
        line.setIcon(new ImageIcon(getClass().getResource("/icons/line.png")));
        line.setIconTextGap(0);
        line.setMargin(new Insets(0, 0, 0, 0));
        line.setText("");
        palettePanel.add(line, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spraypaint = new JButton();
        spraypaint.setContentAreaFilled(false);
        spraypaint.setIcon(new ImageIcon(getClass().getResource("/icons/spraypaint.png")));
        spraypaint.setIconTextGap(0);
        spraypaint.setMargin(new Insets(0, 0, 0, 0));
        spraypaint.setText("");
        palettePanel.add(spraypaint, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rectangle = new JButton();
        rectangle.setContentAreaFilled(false);
        rectangle.setIcon(new ImageIcon(getClass().getResource("/icons/rectangle.png")));
        rectangle.setIconTextGap(0);
        rectangle.setMargin(new Insets(0, 0, 0, 0));
        rectangle.setText("");
        palettePanel.add(rectangle, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        roundRectangle = new JButton();
        roundRectangle.setContentAreaFilled(false);
        roundRectangle.setIcon(new ImageIcon(getClass().getResource("/icons/roundrect.png")));
        roundRectangle.setIconTextGap(0);
        roundRectangle.setMargin(new Insets(0, 0, 0, 0));
        roundRectangle.setText("");
        palettePanel.add(roundRectangle, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fill = new JButton();
        fill.setContentAreaFilled(false);
        fill.setIcon(new ImageIcon(getClass().getResource("/icons/fill.png")));
        fill.setIconTextGap(0);
        fill.setMargin(new Insets(0, 0, 0, 0));
        fill.setText("");
        palettePanel.add(fill, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        oval = new JButton();
        oval.setContentAreaFilled(false);
        oval.setIcon(new ImageIcon(getClass().getResource("/icons/oval.png")));
        oval.setIconTextGap(0);
        oval.setMargin(new Insets(0, 0, 0, 0));
        oval.setText("");
        palettePanel.add(oval, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        curve = new JButton();
        curve.setContentAreaFilled(false);
        curve.setIcon(new ImageIcon(getClass().getResource("/icons/curve.png")));
        curve.setIconTextGap(0);
        curve.setMargin(new Insets(0, 0, 0, 0));
        curve.setText("");
        palettePanel.add(curve, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        text = new JButton();
        text.setContentAreaFilled(false);
        text.setIcon(new ImageIcon(getClass().getResource("/icons/text.png")));
        text.setIconTextGap(0);
        text.setMargin(new Insets(0, 0, 0, 0));
        text.setText("");
        palettePanel.add(text, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        shape = new JButton();
        shape.setContentAreaFilled(false);
        shape.setIcon(new ImageIcon(getClass().getResource("/icons/shape.png")));
        shape.setIconTextGap(0);
        shape.setMargin(new Insets(0, 0, 0, 0));
        shape.setText("");
        palettePanel.add(shape, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        polygon = new JButton();
        polygon.setContentAreaFilled(false);
        polygon.setIcon(new ImageIcon(getClass().getResource("/icons/polygon.png")));
        polygon.setIconTextGap(0);
        polygon.setMargin(new Insets(0, 0, 0, 0));
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
