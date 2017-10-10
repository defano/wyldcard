package com.defano.hypercard.parts.button.styles;

import com.defano.hypercard.fonts.FontUtils;
import com.defano.hypercard.fonts.FontFactory;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.button.ButtonComponent;
import com.defano.hypercard.parts.button.ButtonModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.ast.common.Value;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class MenuButton extends JComboBox<String> implements ButtonComponent {

    private final ToolEditablePart toolEditablePart;
    private final DefaultComboBoxModel<String> menuItems = new DefaultComboBoxModel<>();

    public MenuButton(ToolEditablePart toolEditablePart) {
        this.toolEditablePart = toolEditablePart;

        this.addMouseListener(toolEditablePart);
        this.addKeyListener(toolEditablePart);

        final Component[] components = this.getComponents();
        for(final Component component : components) {
            component.addMouseListener(toolEditablePart);
            component.addKeyListener(toolEditablePart);
        }
        this.getEditor().getEditorComponent().addMouseListener(toolEditablePart);
        this.getEditor().getEditorComponent().addKeyListener(toolEditablePart);


        setRenderer(new MenuButtonCellRenderer());
        setModel(menuItems);
        addActionListener(new MenuButtonItemListener());
    }

    public void selectItem(int item) {
        int itemIndex = item - 1;
        if (itemIndex > 0 && itemIndex < getItemCount()) {
            setSelectedIndex(itemIndex);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        toolEditablePart.drawSelectionRectangle(g);
    }

    @Override
    public void onPropertyChanged(String property, Value oldValue, Value newValue) {
        switch (property) {
            case PartModel.PROP_CONTENTS:
                putValueInMenu(newValue);
                break;

            case ButtonModel.PROP_TEXTSIZE:
                setFont(FontFactory.byNameStyleSize(getFont().getFamily(), getFont().getStyle(), newValue.integerValue()));
                break;

            case ButtonModel.PROP_TEXTFONT:
                setFont(FontFactory.byNameStyleSize(newValue.stringValue(), getFont().getStyle(), getFont().getSize()));
                break;

            case ButtonModel.PROP_TEXTSTYLE:
                setFont(FontFactory.byNameStyleSize(getFont().getFamily(), FontUtils.getStyleForValue(newValue), getFont().getSize()));
                break;
        }
    }

    private void putValueInMenu(Value v) {
        menuItems.removeAllElements();
        List<Value> items;

        if (v.lineCount() > 1) {
            items = v.getLines();
        } else {
            items = v.getItems();
        }

        for (Value thisItem : items) {
            menuItems.addElement(thisItem.stringValue());
        }
    }

    private class MenuButtonItemListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if ("-".equals(getSelectedItem())) {
                setSelectedIndex(0);
            } else {
                toolEditablePart.getPartModel().defineProperty(ButtonModel.PROP_SELECTEDTEXT, new Value(String.valueOf(getSelectedItem())), true);
                toolEditablePart.getPartModel().defineProperty(ButtonModel.PROP_SELECTEDLINE, new Value(getSelectedLineExpression()), true);
            }
        }

        private String getSelectedLineExpression() {
            return "line " +
                    (getSelectedIndex() + 1) +
                    " of " +
                    toolEditablePart.getCardLayer().friendlyName.toLowerCase() +
                    " button id " +
                    toolEditablePart.getId();
        }
    }

    private class MenuButtonCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (String.valueOf(value).equals("-")) {
                JSeparator separator = new JSeparator();
                separator.setEnabled(false);
                return separator;
            }

            return this;
        }
    }

}
