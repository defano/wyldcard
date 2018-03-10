package com.defano.wyldcard.parts.button.styles;

import com.defano.wyldcard.border.PartBorderFactory;
import com.defano.wyldcard.fonts.FontUtils;
import com.defano.wyldcard.icons.ButtonIcon;
import com.defano.wyldcard.icons.IconFactory;
import com.defano.wyldcard.parts.ContainerWrappedPart;
import com.defano.wyldcard.parts.ToolEditablePart;
import com.defano.wyldcard.parts.button.HyperCardButton;
import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.parts.button.IconAlignable;
import com.defano.wyldcard.parts.model.PropertiesModel;
import com.defano.hypertalk.ast.model.Value;

import javax.swing.*;
import java.awt.*;

public class NativeButton extends JPanel implements ContainerWrappedPart, HyperCardButton, IconAlignable {

    private final ToolEditablePart toolEditablePart;
    private final JButton button;

    public NativeButton(ToolEditablePart toolEditablePart) {
        this.toolEditablePart = toolEditablePart;
        this.button = new JButton();

        super.setLayout(new BorderLayout());
        super.add(button);

        button.addMouseListener(toolEditablePart);
        button.addKeyListener(toolEditablePart);
        super.setBorder(PartBorderFactory.createEmptyBorder());
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        toolEditablePart.drawSelectionRectangle(g);
    }

    @Override
    public void onPropertyChanged(PropertiesModel model, String property, Value oldValue, Value newValue) {
        switch (property) {
            case ButtonModel.PROP_NAME:
            case ButtonModel.PROP_SHOWNAME:
                boolean showName = toolEditablePart.getPartModel().getKnownProperty(ButtonModel.PROP_SHOWNAME).booleanValue();
                button.setText(showName ? newValue.stringValue() : "");
                break;

            case ButtonModel.PROP_ENABLED:
                button.setEnabled(newValue.booleanValue());
                break;

            case ButtonModel.PROP_TEXTSIZE:
                button.setFont(FontUtils.getFontByNameStyleSize(getFont().getFamily(), getFont().getStyle(), newValue.integerValue()));
                break;

            case ButtonModel.PROP_TEXTFONT:
                button.setFont(FontUtils.getFontByNameStyleSize(newValue.stringValue(), getFont().getStyle(), getFont().getSize()));
                break;

            case ButtonModel.PROP_TEXTSTYLE:
                button.setFont(FontUtils.getFontByNameStyleSize(getFont().getFamily(), FontUtils.getFontStyleForValue(newValue), getFont().getSize()));
                break;

            case ButtonModel.PROP_TEXTALIGN:
                button.setHorizontalAlignment(FontUtils.getAlignmentForValue(newValue));
                break;

            case ButtonModel.PROP_ICON:
                ButtonIcon icon = IconFactory.findIconForValue(newValue);
                button.setIcon(icon == null ? null : icon.getImage());
                break;

            case ButtonModel.PROP_ICONALIGN:
                setIconAlignment(newValue);
                break;
        }
    }

    @Override
    public JComponent getIconComponent() {
        return button;
    }

    @Override
    public JComponent getWrappedComponent() {
        return button;
    }
}
