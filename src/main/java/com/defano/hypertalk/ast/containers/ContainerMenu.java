package com.defano.hypertalk.ast.containers;

import com.defano.hypertalk.ast.common.MenuSpecifier;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContainerMenu extends Container {

    private final MenuSpecifier menuSpecifier;

    public ContainerMenu(MenuSpecifier menuSpecifier) {
        this.menuSpecifier = menuSpecifier;
    }

    @Override
    public Value getValue() throws HtSemanticException {
        JMenu menu = menuSpecifier.evaluate();
        ArrayList<Value> menuItems = new ArrayList<>();

        for (int thisItemIndex = 0; thisItemIndex < menu.getItemCount(); thisItemIndex++) {
            JMenuItem thisItem = menu.getItem(thisItemIndex);
            if (thisItem == null || thisItem.getText() == null) {
                menuItems.add(new Value("-"));
            } else {
                menuItems.add(new Value(thisItem.getText()));
            }
        }

        return Value.ofLines(menuItems);
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {
        JMenu menu = menuSpecifier.evaluate();

        switch (preposition) {
            case BEFORE:
                addMenuItems(value, menu, 0);
                break;
            case AFTER:
                addMenuItems(value, menu, menu.getItemCount());
                break;
            case INTO:
                menu.removeAll();
                addMenuItems(value, menu, 0);
                break;
        }
    }

    private void addMenuItems(Value v, JMenu menu, int startingIndex) {
        List<Value> lines = v.getLines();
        Collections.reverse(lines);
        for (Value thisItem : lines) {
            if (thisItem.stringValue().equalsIgnoreCase("-")) {
                menu.add(new JSeparator(), startingIndex);
            } else {
                menu.add(new JMenuItem(thisItem.stringValue()), startingIndex);
            }
        }
    }

}
