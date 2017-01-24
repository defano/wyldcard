package hypercard.parts.buttons;

import com.defano.jmonet.tools.util.MarchingAnts;
import hypercard.parts.ButtonPart;
import hypercard.parts.SharedHiliteDelegate;
import hypercard.parts.ToolEditablePart;
import hypercard.parts.model.ButtonModel;
import hypertalk.ast.common.Value;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CheckboxButton extends JCheckBox implements ButtonComponent, ActionListener {

    private final ToolEditablePart toolEditablePart;

    public CheckboxButton(ToolEditablePart toolEditablePart) {
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
                CheckboxButton.super.setText(showName ? toolEditablePart.getPartModel().getKnownProperty(ButtonModel.PROP_NAME).stringValue() : "");
                break;

            case ButtonModel.PROP_HILITE:
                CheckboxButton.super.setSelected(newValue.booleanValue());
                break;
        }
    }



    @Override
    public boolean hasSharedHilite() {
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Swing automatically change the selection state of the component for us; we need to un-do this change when
        // not in auto hilite mode.
        if (!toolEditablePart.isAutoHilited()) {
            setSelected(!isSelected());
        } else {
            SharedHiliteDelegate.changeHilite((ButtonPart) toolEditablePart, isSelected());
        }
    }
}
