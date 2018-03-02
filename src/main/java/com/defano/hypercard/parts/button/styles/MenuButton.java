package com.defano.hypercard.parts.button.styles;

import com.defano.hypercard.border.PartBorderFactory;
import com.defano.hypercard.fonts.FontUtils;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.button.HyperCardButton;
import com.defano.hypercard.parts.button.ButtonModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.parts.model.PropertiesModel;
import com.defano.hypertalk.ast.model.Value;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class MenuButton extends JComboBox<String> implements HyperCardButton {

    private final ToolEditablePart toolEditablePart;
    private final DefaultComboBoxModel<String> menuItems = new DefaultComboBoxModel<>();

    public MenuButton(ToolEditablePart toolEditablePart) {
        this.toolEditablePart = toolEditablePart;

        this.addMouseListener(toolEditablePart);
        this.addKeyListener(toolEditablePart);
        this.setBorder(PartBorderFactory.createEmptyBorder());

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
        if (itemIndex >= 0 && itemIndex < getItemCount()) {
            setSelectedIndex(itemIndex);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        toolEditablePart.drawSelectionRectangle(g);
    }

    @Override
    public void onPropertyChanged(PropertiesModel model, String property, Value oldValue, Value newValue) {
        switch (property) {
            case PartModel.PROP_CONTENTS:
                int lastSelection = model.getKnownProperty(ButtonModel.PROP_SELECTEDITEM).integerValue();
                putValueInMenu(newValue);
                selectItem(lastSelection);
                break;

            case ButtonModel.PROP_TEXTSIZE:
                setFont(FontUtils.getFontByNameStyleSize(getFont().getFamily(), getFont().getStyle(), newValue.integerValue()));
                break;

            case ButtonModel.PROP_TEXTFONT:
                setFont(FontUtils.getFontByNameStyleSize(newValue.stringValue(), getFont().getStyle(), getFont().getSize()));
                break;

            case ButtonModel.PROP_TEXTSTYLE:
                setFont(FontUtils.getFontByNameStyleSize(getFont().getFamily(), FontUtils.getFontStyleForValue(newValue), getFont().getSize()));
                break;

            case ButtonModel.PROP_SELECTEDITEM:
                selectItem(newValue.integerValue());
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
            if (isDividerElement(getSelectedItem())) {
                setSelectedIndex(0);
            } else {
                toolEditablePart.getPartModel().setKnownProperty(ButtonModel.PROP_SELECTEDITEM, new Value(getSelectedIndex() + 1), true);
            }
        }
    }

    private class MenuButtonCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (isDividerElement(value)) {
                JSeparator separator = new JSeparator();
                separator.setEnabled(false);
                return separator;
            }

            return this;
        }
    }

    private boolean isDividerElement(Object element) {
        return String.valueOf(element).trim().startsWith("-");
    }

}
