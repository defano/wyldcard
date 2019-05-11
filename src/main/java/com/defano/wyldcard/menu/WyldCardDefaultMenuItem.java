package com.defano.wyldcard.menu;

import com.defano.wyldcard.message.Message;

import javax.swing.*;

public class WyldCardDefaultMenuItem extends JMenuItem implements WyldCardMenuItem {

    private Message menuMessage;

    public WyldCardDefaultMenuItem() {
    }

    public WyldCardDefaultMenuItem(Action a) {
        super(a);
    }

    @Override
    public Message getMenuMessage() {
        return menuMessage;
    }

    @Override
    public void setMenuMessage(Message menuMessage) {
        this.menuMessage = menuMessage;
    }
}
