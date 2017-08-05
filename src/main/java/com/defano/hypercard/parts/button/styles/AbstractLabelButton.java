/*
 * AbstractLabelButton
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.button.styles;

import com.defano.hypercard.fonts.FontUtils;
import com.defano.hypercard.fonts.HyperCardFont;
import com.defano.hypercard.parts.button.ButtonComponent;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.button.ButtonModel;
import com.defano.hypertalk.ast.common.Value;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractLabelButton extends JLabel implements ButtonComponent {

    protected final ToolEditablePart toolEditablePart;
    protected boolean drawnDisabled = false;

    protected abstract void drawBorder(boolean isDisabled, Graphics2D g);
    protected abstract void setName(boolean isDisabled, String name);
    protected abstract void setHilite(boolean isDisabled, boolean isHilited);

    public AbstractLabelButton(ToolEditablePart toolEditablePart) {
        super("", SwingConstants.CENTER);
        setBackground(Color.BLACK);

        this.toolEditablePart = toolEditablePart;
        super.setEnabled(true);

        super.addMouseListener(toolEditablePart);
        super.addKeyListener(toolEditablePart);
    }

    @Override
    public void paintComponent(Graphics g) {
        drawBorder(drawnDisabled, (Graphics2D) g);
        super.paintComponent(g);
        toolEditablePart.drawSelectionRectangle(g);
    }

    @Override
    public void onPropertyChanged(String property, Value oldValue, Value newValue) {
        switch (property) {
            case ButtonModel.PROP_NAME:
            case ButtonModel.PROP_SHOWNAME:
                boolean showName = toolEditablePart.getPartModel().getKnownProperty(ButtonModel.PROP_SHOWNAME).booleanValue();
                setName(drawnDisabled, showName ? toolEditablePart.getPartModel().getKnownProperty(ButtonModel.PROP_NAME).stringValue() : "");

            case ButtonModel.PROP_HILITE:
                setHilite(drawnDisabled, newValue.booleanValue());
                break;

            case ButtonModel.PROP_ENABLED:
                drawnDisabled = !newValue.booleanValue();
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

            case ButtonModel.PROP_TEXTALIGN:
                setHorizontalAlignment(FontUtils.getAlignmentForValue(newValue));
                break;
        }
    }

    protected Color textColor(boolean isDisabled) {
        return isDisabled ? Color.GRAY : Color.BLACK;
    }

}
