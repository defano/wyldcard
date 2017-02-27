/*
 * MenuButton
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.buttons.styles;

import com.defano.hypercard.context.GlobalContext;
import com.defano.hypercard.context.ToolMode;
import com.defano.hypercard.fonts.FontUtils;
import com.defano.hypercard.fonts.HyperCardFont;
import com.defano.hypercard.parts.buttons.ButtonView;
import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.model.AbstractPartModel;
import com.defano.hypercard.parts.model.ButtonModel;
import com.defano.hypertalk.ast.common.Value;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class MenuButton extends JComboBox implements ButtonView {

    private final ToolEditablePart toolEditablePart;
    private final DefaultComboBoxModel<String> menuItems = new DefaultComboBoxModel();

    public MenuButton(ToolEditablePart toolEditablePart) {
        this.toolEditablePart = toolEditablePart;

        super.addActionListener(toolEditablePart);
        super.addKeyListener(toolEditablePart);
        for (Component thisComponent : super.getComponents()) {
            thisComponent.addMouseListener(toolEditablePart);
        }

        ToolsContext.getInstance().getToolModeProvider().addObserverAndUpdate((o, arg) -> setEnabled(ToolMode.BUTTON != arg));

        setModel(menuItems);
        addItemListener(e -> toolEditablePart.getPartModel().defineProperty(AbstractPartModel.PROP_SELECTEDTEXT, new Value(String.valueOf(e.getItem())), true));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        toolEditablePart.drawSelectionRectangle(g);
    }

    @Override
    public void onPropertyChanged(String property, Value oldValue, Value newValue) {
        switch (property) {
            case ButtonModel.PROP_CONTENTS:
                menuItems.removeAllElements();
                for (String thisItem : newValue.stringValue().split("\n")) {
                    menuItems.addElement(thisItem);
                }
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
}
