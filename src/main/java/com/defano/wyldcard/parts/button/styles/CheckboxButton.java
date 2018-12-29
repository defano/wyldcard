package com.defano.wyldcard.parts.button.styles;

import com.defano.wyldcard.border.PartBorderFactory;
import com.defano.wyldcard.parts.button.HyperCardButton;
import com.defano.wyldcard.parts.button.ButtonPart;
import com.defano.wyldcard.parts.ToolEditablePart;
import com.defano.wyldcard.parts.button.SharedHilight;
import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.fonts.FontUtils;
import com.defano.wyldcard.parts.model.DefaultPropertiesModel;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CheckboxButton extends JCheckBox implements SharedHilight, HyperCardButton, ActionListener {

    private final ToolEditablePart toolEditablePart;

    public CheckboxButton(ToolEditablePart toolEditablePart) {
        this.toolEditablePart = toolEditablePart;

        super.addActionListener(this);
        super.addMouseListener(toolEditablePart);
        super.addKeyListener(toolEditablePart);
        super.setBorder(PartBorderFactory.createEmptyBorder());
        super.setBorderPainted(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        toolEditablePart.drawSelectionRectangle(g);
    }

    @Override
    public void onPropertyChanged(ExecutionContext context, DefaultPropertiesModel model, String property, Value oldValue, Value newValue) {
        switch (property) {
            case ButtonModel.PROP_NAME:
            case ButtonModel.PROP_SHOWNAME:
                boolean showName = toolEditablePart.getPartModel().getKnownProperty(context, ButtonModel.PROP_SHOWNAME).booleanValue();
                CheckboxButton.super.setText(showName ? toolEditablePart.getPartModel().getKnownProperty(context, ButtonModel.PROP_NAME).toString() : "");
                break;

            case ButtonModel.PROP_HILITE:
                CheckboxButton.super.setSelected(newValue.booleanValue());
                break;

            case ButtonModel.PROP_ENABLED:
                CheckboxButton.super.setEnabled(newValue.booleanValue());

            case ButtonModel.PROP_TEXTSIZE:
                setFont(FontUtils.getFontByNameStyleSize(getFont().getFamily(), getFont().getStyle(), newValue.integerValue()));
                break;

            case ButtonModel.PROP_TEXTFONT:
                setFont(FontUtils.getFontByNameStyleSize(newValue.toString(), getFont().getStyle(), getFont().getSize()));
                break;

            case ButtonModel.PROP_TEXTSTYLE:
                setFont(FontUtils.getFontByNameStyleSize(getFont().getFamily(), FontUtils.getFontStyleForValue(context, newValue), getFont().getSize()));
                break;

            case ButtonModel.PROP_TEXTALIGN:
                setHorizontalAlignment(FontUtils.getAlignmentForValue(newValue));
                break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Swing automatically changes the selection state of the component for us; we need to un-do this change when not in auto-hilite mode.
        if (isAutoHilited()) {
            setSharedHilite(new ExecutionContext(), (ButtonPart) toolEditablePart, isSelected());
        } else {
            setSelected(!isSelected());
        }
    }

    private boolean isAutoHilited() {
        return toolEditablePart.getPartModel().getKnownProperty(new ExecutionContext(), ButtonModel.PROP_AUTOHILIGHT).booleanValue();
    }

}
