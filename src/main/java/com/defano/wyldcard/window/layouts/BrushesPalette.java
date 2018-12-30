package com.defano.wyldcard.window.layouts;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.paint.PaintBrush;
import com.defano.wyldcard.window.WyldCardDialog;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import io.reactivex.functions.Consumer;

import javax.swing.*;
import java.awt.*;

public class BrushesPalette extends WyldCardDialog<Object> implements Consumer<PaintBrush> {

    private JPanel brushesPanel;

    private JButton square16;
    private JButton square12;
    private JButton square8;
    private JButton square4;
    private JButton round16;
    private JButton round12;
    private JButton round8;
    private JButton round4;
    private JButton line16;
    private JButton line12;
    private JButton line8;
    private JButton line4;
    private JButton forward16;
    private JButton forward12;
    private JButton forward8;
    private JButton forward4;
    private JButton back16;
    private JButton back12;
    private JButton back8;
    private JButton back4;
    private JButton bar16;
    private JButton bar4;
    private JButton bar8;
    private JButton bar12;

    private JButton[] allButtons;

    public BrushesPalette() {
        allButtons = new JButton[]{
                square16, square12, square8, square4,
                round16, round12, round8, round4,
                line16, line12, line8, line4,
                forward16, forward12, forward8, forward4,
                back16, back12, back8, back4,
                bar16, bar12, bar8, bar4
        };

        square16.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.SQUARE_16X16));
        square12.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.SQUARE_12X12));
        square8.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.SQUARE_8X8));
        square4.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.SQUARE_4X4));
        round16.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.ROUND_16X16));
        round12.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.ROUND_12X12));
        round8.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.ROUND_8X8));
        round4.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.ROUND_4X4));
        line16.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.LINE_16));
        line12.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.LINE_12));
        line8.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.LINE_8));
        line4.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.LINE_4));
        forward16.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.FORWARD_16));
        forward12.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.FORWARD_12));
        forward8.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.FORWARD_8));
        forward4.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.FORWARD_4));
        back16.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.BACK_16));
        back12.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.BACK_12));
        back8.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.BACK_8));
        back4.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.BACK_4));
        bar16.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.BAR_16));
        bar12.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.BAR_12));
        bar8.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.BAR_8));
        bar4.addActionListener(a -> WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.BAR_4));

        WyldCard.getInstance().getToolsManager().getSelectedBrushProvider().subscribe(this);
    }

    @Override
    public JPanel getWindowPanel() {
        return brushesPanel;
    }

    @Override
    public void bindModel(Object data) {
        // Nothing to do
    }

    @Override
    public void accept(PaintBrush brush) {
        for (JButton thisButton : allButtons) {
            thisButton.setEnabled(true);
        }

        getButtonForBrush(brush).setEnabled(false);
    }

    private JButton getButtonForBrush(PaintBrush newBrush) {
        switch (newBrush) {
            case SQUARE_16X16:
                return square16;
            case SQUARE_12X12:
                return square12;
            case SQUARE_8X8:
                return square8;
            case SQUARE_4X4:
                return square4;
            case ROUND_16X16:
                return round16;
            case ROUND_12X12:
                return round12;
            case ROUND_8X8:
                return round8;
            case ROUND_4X4:
                return round4;
            case LINE_16:
                return line16;
            case LINE_12:
                return line12;
            case LINE_8:
                return line8;
            case LINE_4:
                return line4;
            case FORWARD_16:
                return forward16;
            case FORWARD_12:
                return forward12;
            case FORWARD_8:
                return forward8;
            case FORWARD_4:
                return forward4;
            case BACK_16:
                return back16;
            case BACK_12:
                return back12;
            case BACK_8:
                return back8;
            case BACK_4:
                return back4;
            case BAR_16:
                return bar16;
            case BAR_12:
                return bar12;
            case BAR_8:
                return bar8;
            case BAR_4:
                return bar4;
        }

        return square16;
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
        brushesPanel = new JPanel();
        brushesPanel.setLayout(new GridLayoutManager(4, 6, new Insets(0, 0, 0, 0), 0, 0));
        square16 = new JButton();
        square16.setIcon(new ImageIcon(getClass().getResource("/brushes/square_16x16.png")));
        square16.setText("");
        brushesPanel.add(square16, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        square12 = new JButton();
        square12.setIcon(new ImageIcon(getClass().getResource("/brushes/square_12x12.png")));
        square12.setText("");
        brushesPanel.add(square12, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        square8 = new JButton();
        square8.setIcon(new ImageIcon(getClass().getResource("/brushes/square_8x8.png")));
        square8.setText("");
        brushesPanel.add(square8, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        square4 = new JButton();
        square4.setHideActionText(false);
        square4.setIcon(new ImageIcon(getClass().getResource("/brushes/square_4x4.png")));
        square4.setText("");
        brushesPanel.add(square4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        round16 = new JButton();
        round16.setIcon(new ImageIcon(getClass().getResource("/brushes/round_16x16.png")));
        round16.setText("");
        brushesPanel.add(round16, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        round12 = new JButton();
        round12.setIcon(new ImageIcon(getClass().getResource("/brushes/round_12x12.png")));
        round12.setText("");
        brushesPanel.add(round12, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        round8 = new JButton();
        round8.setIcon(new ImageIcon(getClass().getResource("/brushes/round_8x8.png")));
        round8.setText("");
        brushesPanel.add(round8, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        round4 = new JButton();
        round4.setIcon(new ImageIcon(getClass().getResource("/brushes/round_4x4.png")));
        round4.setText("");
        brushesPanel.add(round4, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        line16 = new JButton();
        line16.setIcon(new ImageIcon(getClass().getResource("/brushes/line_16.png")));
        line16.setText("");
        brushesPanel.add(line16, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        line12 = new JButton();
        line12.setIcon(new ImageIcon(getClass().getResource("/brushes/line_12.png")));
        line12.setText("");
        brushesPanel.add(line12, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        line8 = new JButton();
        line8.setIcon(new ImageIcon(getClass().getResource("/brushes/line_8.png")));
        line8.setText("");
        brushesPanel.add(line8, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        line4 = new JButton();
        line4.setIcon(new ImageIcon(getClass().getResource("/brushes/line_4.png")));
        line4.setText("");
        brushesPanel.add(line4, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        forward16 = new JButton();
        forward16.setIcon(new ImageIcon(getClass().getResource("/brushes/forward_16.png")));
        forward16.setText("");
        brushesPanel.add(forward16, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        forward12 = new JButton();
        forward12.setIcon(new ImageIcon(getClass().getResource("/brushes/forward_12.png")));
        forward12.setText("");
        brushesPanel.add(forward12, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        forward8 = new JButton();
        forward8.setIcon(new ImageIcon(getClass().getResource("/brushes/forward_8.png")));
        forward8.setText("");
        brushesPanel.add(forward8, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        forward4 = new JButton();
        forward4.setIcon(new ImageIcon(getClass().getResource("/brushes/forward_4.png")));
        forward4.setText("");
        brushesPanel.add(forward4, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        back16 = new JButton();
        back16.setIcon(new ImageIcon(getClass().getResource("/brushes/back_16.png")));
        back16.setText("");
        brushesPanel.add(back16, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        back12 = new JButton();
        back12.setIcon(new ImageIcon(getClass().getResource("/brushes/back_12.png")));
        back12.setText("");
        brushesPanel.add(back12, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        back8 = new JButton();
        back8.setIcon(new ImageIcon(getClass().getResource("/brushes/back_8.png")));
        back8.setText("");
        brushesPanel.add(back8, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        back4 = new JButton();
        back4.setIcon(new ImageIcon(getClass().getResource("/brushes/back_4.png")));
        back4.setText("");
        brushesPanel.add(back4, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bar16 = new JButton();
        bar16.setIcon(new ImageIcon(getClass().getResource("/brushes/bar_16.png")));
        bar16.setText("");
        brushesPanel.add(bar16, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bar12 = new JButton();
        bar12.setIcon(new ImageIcon(getClass().getResource("/brushes/bar_12.png")));
        bar12.setText("");
        brushesPanel.add(bar12, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bar8 = new JButton();
        bar8.setIcon(new ImageIcon(getClass().getResource("/brushes/bar_8.png")));
        bar8.setText("");
        brushesPanel.add(bar8, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bar4 = new JButton();
        bar4.setIcon(new ImageIcon(getClass().getResource("/brushes/bar_4.png")));
        bar4.setText("");
        brushesPanel.add(bar4, new GridConstraints(3, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return brushesPanel;
    }
}
