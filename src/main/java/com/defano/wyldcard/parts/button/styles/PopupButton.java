package com.defano.wyldcard.parts.button.styles;

import com.defano.wyldcard.border.PartBorderFactory;
import com.defano.wyldcard.fonts.FontUtils;
import com.defano.wyldcard.parts.ToolEditablePart;
import com.defano.wyldcard.parts.button.HyperCardButton;
import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.properties.PropertiesModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class PopupButton extends JComboBox<String> implements HyperCardButton {

    private final ToolEditablePart toolEditablePart;
    private final DefaultComboBoxModel<String> menuItems = new DefaultComboBoxModel<>();

    public PopupButton(ToolEditablePart toolEditablePart) {
        this.toolEditablePart = toolEditablePart;

        this.addMouseListener(toolEditablePart);
        this.addKeyListener(toolEditablePart);
        this.setBorder(PartBorderFactory.createEmptyBorder());

        final Component[] components = this.getComponents();
        for (final Component component : components) {
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
    public ToolEditablePart getToolEditablePart() {
        return toolEditablePart;
    }

    @Override
    public void onPropertyChanged(ExecutionContext context, PropertiesModel model, String property, Value oldValue, Value newValue) {
        switch (property) {
            case PartModel.PROP_CONTENTS:
                int lastSelection = model.get(context, ButtonModel.PROP_SELECTEDITEM).integerValue();
                putValueInMenu(context, newValue);
                selectItem(lastSelection);
                break;

            case ButtonModel.PROP_TEXTSIZE:
                setFont(FontUtils.getFontByNameStyleSize(getFont().getFamily(), getFont().getStyle(), newValue.integerValue()));
                break;

            case ButtonModel.PROP_TEXTFONT:
                setFont(FontUtils.getFontByNameStyleSize(newValue.toString(), getFont().getStyle(), getFont().getSize()));
                break;

            case ButtonModel.PROP_TEXTSTYLE:
                setFont(FontUtils.getFontByNameStyleSize(getFont().getFamily(), FontUtils.getFontStyleForValue(context, newValue), getFont().getSize()));
                break;

            case ButtonModel.PROP_SELECTEDITEM:
                selectItem(newValue.integerValue());
                break;
        }
    }

    private void putValueInMenu(ExecutionContext context, Value v) {
        menuItems.removeAllElements();
        List<Value> items;

        if (v.lineCount(context) > 1) {
            items = v.getLines(context);
        } else {
            items = v.getItems(context);
        }

        for (Value thisItem : items) {
            menuItems.addElement(thisItem.toString());
        }

        // Convert item list to line list
        toolEditablePart.getPartModel().setQuietly(context, PartModel.PROP_CONTENTS, Value.ofLines(items));
    }

    private boolean isDividerElement(Object element) {
        return String.valueOf(element).trim().startsWith("-");
    }

    private class MenuButtonItemListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isDividerElement(getSelectedItem())) {
                setSelectedIndex(0);
            } else {
                toolEditablePart.getPartModel().setQuietly(new ExecutionContext(), ButtonModel.PROP_SELECTEDITEM, new Value(getSelectedIndex() + 1));
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

}
