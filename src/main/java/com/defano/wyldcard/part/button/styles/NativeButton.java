package com.defano.wyldcard.part.button.styles;

import com.defano.wyldcard.border.PartBorderFactory;
import com.defano.wyldcard.font.FontUtils;
import com.defano.wyldcard.icon.ButtonIcon;
import com.defano.wyldcard.icon.IconDatabase;
import com.defano.wyldcard.part.util.ContainerWrappedPart;
import com.defano.wyldcard.part.ToolEditablePart;
import com.defano.wyldcard.part.button.HyperCardButton;
import com.defano.wyldcard.part.button.ButtonModel;
import com.defano.wyldcard.part.button.IconAlignable;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.property.PropertiesModel;
import com.defano.wyldcard.runtime.ExecutionContext;

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
        super.setBorder(PartBorderFactory.createEmptyBorder());
        super.setBackground(Color.WHITE);
    }

    @Override
    public void onStart() {
        button.addMouseListener(toolEditablePart);
        button.addKeyListener(toolEditablePart);
    }

    @Override
    public void onStop() {
        button.removeMouseListener(toolEditablePart);
        button.removeKeyListener(toolEditablePart);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        toolEditablePart.drawSelectionRectangle(g);
    }

    @Override
    public ToolEditablePart getToolEditablePart() {
        return toolEditablePart;
    }

    @Override
    public void onPropertyChanged(ExecutionContext context, PropertiesModel model, String property, Value oldValue, Value newValue) {
        switch (property) {
            case ButtonModel.PROP_NAME:
            case ButtonModel.PROP_SHOWNAME:
                boolean showName = toolEditablePart.getPartModel().get(context, ButtonModel.PROP_SHOWNAME).booleanValue();
                String buttonName = toolEditablePart.getPartModel().get(context, ButtonModel.PROP_NAME).toString();
                button.setText(showName ? buttonName : "");
                break;

            case ButtonModel.PROP_ENABLED:
                button.setEnabled(newValue.booleanValue());
                break;

            case ButtonModel.PROP_TEXTSIZE:
                button.setFont(FontUtils.getFontByNameStyleSize(getFont().getFamily(), getFont().getStyle(), newValue.integerValue()));
                break;

            case ButtonModel.PROP_TEXTFONT:
                button.setFont(FontUtils.getFontByNameStyleSize(newValue.toString(), getFont().getStyle(), getFont().getSize()));
                break;

            case ButtonModel.PROP_TEXTSTYLE:
                button.setFont(FontUtils.getFontByNameStyleSize(getFont().getFamily(), FontUtils.getFontStyleForValue(context, newValue), getFont().getSize()));
                break;

            case ButtonModel.PROP_TEXTALIGN:
                button.setHorizontalAlignment(FontUtils.getAlignmentForValue(newValue));
                break;

            case ButtonModel.PROP_ICON:
                ButtonIcon icon = IconDatabase.getInstance().findIconForValue(newValue);
                button.setIcon(icon == null ? null : icon.getIcon());
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
