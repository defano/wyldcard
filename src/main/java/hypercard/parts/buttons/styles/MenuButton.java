package hypercard.parts.buttons.styles;

import hypercard.context.ToolMode;
import hypercard.context.ToolsContext;
import hypercard.fonts.HyperCardFont;
import hypercard.parts.ToolEditablePart;
import hypercard.parts.buttons.ButtonView;
import hypercard.parts.model.ButtonModel;
import hypercard.fonts.FontUtils;
import hypertalk.ast.common.Value;

import javax.swing.*;
import java.awt.*;


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
