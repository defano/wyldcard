package com.defano.hypercard.parts.button.styles;

import com.defano.hypercard.fonts.FontUtils;
import com.defano.hypercard.fonts.FontFactory;
import com.defano.hypercard.icons.ButtonIcon;
import com.defano.hypercard.icons.IconFactory;
import com.defano.hypercard.parts.button.ButtonComponent;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.button.ButtonModel;
import com.defano.hypercard.parts.button.IconAlignable;
import com.defano.hypertalk.ast.common.Value;

import javax.swing.*;
import java.awt.*;

public class DefaultButton extends JButton implements ButtonComponent, IconAlignable {

    private final ToolEditablePart toolEditablePart;

    public DefaultButton(ToolEditablePart toolEditablePart) {
        this.toolEditablePart = toolEditablePart;

        super.addMouseListener(toolEditablePart);
        super.addKeyListener(toolEditablePart);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        toolEditablePart.drawSelectionRectangle(g);
    }

    @Override
    public void onPropertyChanged(String property, Value oldValue, Value newValue) {
        switch (property) {
            case ButtonModel.PROP_NAME:
            case ButtonModel.PROP_SHOWNAME:
                boolean showName = toolEditablePart.getPartModel().getKnownProperty(ButtonModel.PROP_SHOWNAME).booleanValue();
                DefaultButton.super.setText(showName ? newValue.stringValue() : "");
                break;

            case ButtonModel.PROP_ENABLED:
                super.setEnabled(newValue.booleanValue());
                break;

            case ButtonModel.PROP_TEXTSIZE:
                setFont(FontFactory.byNameStyleSize(getFont().getFamily(), getFont().getStyle(), newValue.integerValue()));
                break;

            case ButtonModel.PROP_TEXTFONT:
                setFont(FontFactory.byNameStyleSize(newValue.stringValue(), getFont().getStyle(), getFont().getSize()));
                break;

            case ButtonModel.PROP_TEXTSTYLE:
                setFont(FontFactory.byNameStyleSize(getFont().getFamily(), FontUtils.getFontStyleForValue(newValue), getFont().getSize()));
                break;

            case ButtonModel.PROP_TEXTALIGN:
                setHorizontalAlignment(FontUtils.getAlignmentForValue(newValue));
                break;

            case ButtonModel.PROP_ICON:
                ButtonIcon icon = IconFactory.findIconForValue(newValue);
                setIcon(icon == null ? null : icon.getImage());
                break;

            case ButtonModel.PROP_ICONALIGN:
                setIconAlignment(newValue);
                break;
        }
    }

    @Override
    public JComponent getIconComponent() {
        return this;
    }
}
