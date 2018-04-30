package com.defano.wyldcard.window.layouts;

import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.patterns.PatternPaletteButton;
import com.defano.wyldcard.runtime.context.ToolsContext;
import com.defano.wyldcard.window.WyldCardWindow;
import io.reactivex.functions.Consumer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PatternPalette extends WyldCardWindow implements Consumer {

    private final static int PATTERN_WIDTH = 30;
    private final static int PATTERN_HEIGHT = 20;

    private JPanel palettePanel;

    private PatternPaletteButton button1;
    private PatternPaletteButton button2;
    private PatternPaletteButton button3;
    private PatternPaletteButton button4;
    private PatternPaletteButton button5;
    private PatternPaletteButton button6;
    private PatternPaletteButton button7;
    private PatternPaletteButton button8;
    private PatternPaletteButton button9;
    private PatternPaletteButton button10;
    private PatternPaletteButton button11;
    private PatternPaletteButton button12;
    private PatternPaletteButton button13;
    private PatternPaletteButton button14;
    private PatternPaletteButton button15;
    private PatternPaletteButton button16;
    private PatternPaletteButton button17;
    private PatternPaletteButton button18;
    private PatternPaletteButton button19;
    private PatternPaletteButton button20;
    private PatternPaletteButton button21;
    private PatternPaletteButton button22;
    private PatternPaletteButton button23;
    private PatternPaletteButton button24;
    private PatternPaletteButton button25;
    private PatternPaletteButton button26;
    private PatternPaletteButton button27;
    private PatternPaletteButton button28;
    private PatternPaletteButton button29;
    private PatternPaletteButton button30;
    private PatternPaletteButton button31;
    private PatternPaletteButton button32;
    private PatternPaletteButton button33;
    private PatternPaletteButton button34;
    private PatternPaletteButton button35;
    private PatternPaletteButton button36;
    private PatternPaletteButton button37;
    private PatternPaletteButton button38;
    private PatternPaletteButton button39;
    private PatternPaletteButton button40;

    private PatternPaletteButton[] allPatterns;

    public PatternPalette() {
        allPatterns = new PatternPaletteButton[]{
                button1, button2, button3, button4, button5, button6, button7, button8, button9,
                button10, button11, button12, button13, button14, button15, button16, button17, button18, button19,
                button20, button21, button22, button23, button24, button25, button26, button27, button28, button29,
                button30, button31, button32, button33, button34, button35, button36, button37, button38, button39,
                button40
        };

        redrawPatternButtons();
        attachButtonActions();

        ToolsContext.getInstance().getFillPatternProvider().subscribe(this);
        ToolsContext.getInstance().getBackgroundColorProvider().subscribe(this);
        ToolsContext.getInstance().getForegroundColorProvider().subscribe(this);
    }

    @Override
    public JPanel getWindowPanel() {
        return palettePanel;
    }

    @Override
    public void bindModel(Object data) {
        // Nothing to do
    }

    @RunOnDispatch
    private void attachButtonActions() {
        for (int index = 0; index < allPatterns.length; index++) {
            final int i = index;
            allPatterns[index].addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    ToolsContext.getInstance().setPattern(i);
                }
            });
        }
    }

    @RunOnDispatch
    private void redrawPatternButtons() {
        for (int index = 0; index < allPatterns.length; index++) {
            allPatterns[index].setPatternId(index);
            allPatterns[index].setLeftFrame(index % 4 != 0);
            allPatterns[index].setSize(PATTERN_WIDTH, PATTERN_HEIGHT);
            allPatterns[index].setPreferredSize(new Dimension(PATTERN_WIDTH, PATTERN_HEIGHT));
        }

        palettePanel.invalidate();
        palettePanel.repaint();
    }

    @Override
    public void accept(Object newValue) {
        SwingUtilities.invokeLater(() -> {
            if (newValue instanceof Integer) {
                if ((int) newValue >= 0 && (int) newValue < 40) {
                    for (int index = 0; index < allPatterns.length; index++) {
                        allPatterns[index].setSelected(false);
                    }

                    allPatterns[(int) newValue].setSelected(true);
                }
            } else if (newValue instanceof Color) {
                redrawPatternButtons();
                ToolsContext.getInstance().setPattern(ToolsContext.getInstance().getFillPatternProvider().blockingFirst());
            }
        });
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
        palettePanel.setLayout(new GridBagLayout());
        button1 = new PatternPaletteButton();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button1, gbc);
        button2 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button2, gbc);
        button3 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button3, gbc);
        button4 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button4, gbc);
        button5 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button5, gbc);
        button6 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button6, gbc);
        button7 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button7, gbc);
        button8 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button8, gbc);
        button9 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button9, gbc);
        button10 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button10, gbc);
        button11 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button11, gbc);
        button12 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button12, gbc);
        button13 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button13, gbc);
        button14 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button14, gbc);
        button15 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button15, gbc);
        button16 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button16, gbc);
        button17 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button17, gbc);
        button18 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button18, gbc);
        button19 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button19, gbc);
        button20 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button20, gbc);
        button21 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button21, gbc);
        button22 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button22, gbc);
        button23 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button23, gbc);
        button24 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button24, gbc);
        button25 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button25, gbc);
        button26 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button26, gbc);
        button27 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button27, gbc);
        button28 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button28, gbc);
        button29 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button29, gbc);
        button30 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button30, gbc);
        button31 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button31, gbc);
        button32 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button32, gbc);
        button33 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button33, gbc);
        button34 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button34, gbc);
        button35 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 8;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button35, gbc);
        button36 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 8;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button36, gbc);
        button37 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button37, gbc);
        button38 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button38, gbc);
        button39 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 9;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button39, gbc);
        button40 = new PatternPaletteButton();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 9;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        palettePanel.add(button40, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return palettePanel;
    }
}
