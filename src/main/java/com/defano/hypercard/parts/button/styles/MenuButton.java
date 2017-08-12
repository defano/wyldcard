/*
 * MenuButton
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.button.styles;

import com.defano.hypercard.context.ToolMode;
import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.fonts.FontUtils;
import com.defano.hypercard.fonts.HyperCardFont;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.button.ButtonComponent;
import com.defano.hypercard.parts.button.ButtonModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.ast.common.Value;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


public class MenuButton extends JComboBox<String> implements ButtonComponent {

    private final ToolModeObserver toolModeObserver = new ToolModeObserver();
    private final ToolEditablePart toolEditablePart;
    private final DefaultComboBoxModel<String> menuItems = new DefaultComboBoxModel<>();

    public MenuButton(ToolEditablePart toolEditablePart) {
        this.toolEditablePart = toolEditablePart;

        super.addActionListener(toolEditablePart);
        super.addKeyListener(toolEditablePart);
        for (Component thisComponent : super.getComponents()) {
            thisComponent.addMouseListener(toolEditablePart);
        }

        ToolsContext.getInstance().getToolModeProvider().addObserverAndUpdate(toolModeObserver);

        setRenderer(new MenuButtonCellRenderer());
        setModel(menuItems);
        addActionListener(new MenuButtonItemListener());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        toolEditablePart.drawSelectionRectangle(g);
    }

    @Override
    public void onPropertyChanged(String property, Value oldValue, Value newValue) {
        switch (property) {
            case PartModel.PROP_CONTENTS:
                putValueInMenu(newValue);
                break;

            case ButtonModel.PROP_TEXTSIZE:
                setFont(HyperCardFont.byNameStyleSize(getFont().getFamily(), getFont().getStyle(), newValue.integerValue()));
                break;

            case ButtonModel.PROP_TEXTFONT:
                setFont(HyperCardFont.byNameStyleSize(newValue.stringValue(), getFont().getStyle(), getFont().getSize()));
                break;

            case ButtonModel.PROP_TEXTSTYLE:
                setFont(HyperCardFont.byNameStyleSize(newValue.stringValue(), FontUtils.getStyleForValue(newValue), getFont().getSize()));
                break;
        }
    }

    private void putValueInMenu(Value v) {
        menuItems.removeAllElements();
        List<Value> items;

        if (v.lineCount() > 1) {
            items = v.getLines();
        } else {
            items = v.getItems();
        }

        for (Value thisItem : items) {
            menuItems.addElement(thisItem.stringValue());
        }
    }

    private class ToolModeObserver implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            setEnabled(ToolMode.BUTTON != arg);
        }
    }

    private class MenuButtonItemListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if ("-".equals(getSelectedItem())) {
                setSelectedIndex(0);
            } else {
                toolEditablePart.getPartModel().defineProperty(ButtonModel.PROP_SELECTEDTEXT, new Value(String.valueOf(getSelectedItem())), false);
            }
        }
    }

    private class MenuButtonCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (String.valueOf(value).equals("-")) {
                JSeparator separator = new JSeparator();
                separator.setEnabled(false);
                return separator;
            }

            return this;
        }
    }

}
