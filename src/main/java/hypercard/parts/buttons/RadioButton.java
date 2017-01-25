package hypercard.parts.buttons;

import com.defano.jmonet.tools.util.MarchingAnts;
import hypercard.parts.ButtonPart;
import hypercard.parts.ToolEditablePart;
import hypercard.parts.model.ButtonModel;
import hypertalk.ast.common.Value;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RadioButton extends JRadioButton implements SharedHilight, ButtonComponent, ActionListener {

    private final ToolEditablePart toolEditablePart;

    public RadioButton(ToolEditablePart toolEditablePart) {
        this.toolEditablePart = toolEditablePart;

        MarchingAnts.getInstance().addObserver(this::repaint);
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
                RadioButton.super.setText(showName ? toolEditablePart.getPartModel().getKnownProperty(ButtonModel.PROP_NAME).stringValue() : "");
                break;

            case ButtonModel.PROP_HILITE:
                RadioButton.super.setSelected(newValue.booleanValue());
                break;

            case ButtonModel.PROP_ENABLED:
                RadioButton.super.setEnabled(newValue.booleanValue());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Swing automatically changes the selection state of the component for us; we need to un-do this change when
        // not in auto hilite mode.
        if (!toolEditablePart.isAutoHilited()) {
            setSelected(!isSelected());
        } else {
            setSharedHilite((ButtonPart) toolEditablePart, isSelected());
        }
    }
}
