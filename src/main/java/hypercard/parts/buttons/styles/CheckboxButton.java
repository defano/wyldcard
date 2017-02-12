package hypercard.parts.buttons.styles;

import com.defano.jmonet.tools.util.MarchingAnts;
import hypercard.parts.ButtonPart;
import hypercard.parts.ToolEditablePart;
import hypercard.parts.buttons.ButtonView;
import hypercard.parts.buttons.SharedHilight;
import hypercard.parts.model.ButtonModel;
import hypercard.utils.FontUtils;
import hypertalk.ast.common.Value;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CheckboxButton extends JCheckBox implements SharedHilight, ButtonView, ActionListener {

    private final ToolEditablePart toolEditablePart;

    public CheckboxButton(ToolEditablePart toolEditablePart) {
        this.toolEditablePart = toolEditablePart;

        super.addActionListener(this);
        super.addMouseListener(toolEditablePart);
        super.addKeyListener(toolEditablePart);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        toolEditablePart.drawSelectionRectangle(g);
    }

    @Override
    public void onPropertyChanged(String property, Value oldValue, Value newValue) {
        switch (property) {
            case ButtonModel.PROP_NAME:
            case ButtonModel.PROP_SHOWNAME:
                boolean showName = toolEditablePart.getPartModel().getKnownProperty(ButtonModel.PROP_SHOWNAME).booleanValue();
                CheckboxButton.super.setText(showName ? toolEditablePart.getPartModel().getKnownProperty(ButtonModel.PROP_NAME).stringValue() : "");
                break;

            case ButtonModel.PROP_HILITE:
                CheckboxButton.super.setSelected(newValue.booleanValue());
                break;

            case ButtonModel.PROP_ENABLED:
                CheckboxButton.super.setEnabled(newValue.booleanValue());

            case ButtonModel.PROP_TEXTSIZE:
                setFont(new Font(getFont().getFamily(), getFont().getStyle(), newValue.integerValue()));
                break;

            case ButtonModel.PROP_TEXTFONT:
                setFont(new Font(newValue.stringValue(), getFont().getStyle(), getFont().getSize()));
                break;

            case ButtonModel.PROP_TEXTSTYLE:
                setFont(new Font(newValue.stringValue(), FontUtils.getStyleForValue(newValue), getFont().getSize()));
                break;

            case ButtonModel.PROP_TEXTALIGN:
                setHorizontalAlignment(FontUtils.getAlignmentForValue(newValue));
                break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Swing automatically changes the selection state of the component for us; we need to un-do this change when not in auto-hilite mode.
        if (toolEditablePart.isAutoHilited()) {
            setSharedHilite((ButtonPart) toolEditablePart, isSelected());
        } else {
            setSelected(!isSelected());
        }
    }

}
