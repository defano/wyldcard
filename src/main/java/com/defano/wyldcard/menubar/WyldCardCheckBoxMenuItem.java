package com.defano.wyldcard.menubar;

import com.defano.wyldcard.message.Message;

import javax.swing.*;

public class WyldCardCheckBoxMenuItem extends JCheckBoxMenuItem implements WyldCardMenuItem {

    private Message menuMessage;

    public void setMenuMessage(Message menuMessage) {
        this.menuMessage = menuMessage;
    }

    @Override
    public Message getMenuMessage() {
        return menuMessage;
    }
}
